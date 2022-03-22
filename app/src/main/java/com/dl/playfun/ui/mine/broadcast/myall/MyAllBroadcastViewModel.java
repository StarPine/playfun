package com.dl.playfun.ui.mine.broadcast.myall;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.blankj.utilcode.util.StringUtils;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.app.AppsFlyerEvent;
import com.dl.playfun.data.AppRepository;
import com.dl.playfun.data.source.http.exception.RequestException;
import com.dl.playfun.data.source.http.observer.BaseListEmptyObserver;
import com.dl.playfun.data.source.http.observer.BaseObserver;
import com.dl.playfun.data.source.http.response.BaseListDataResponse;
import com.dl.playfun.data.source.http.response.BaseResponse;
import com.dl.playfun.entity.BroadcastEntity;
import com.dl.playfun.entity.UserDataEntity;
import com.dl.playfun.utils.FileUploadUtils;
import com.dl.playfun.viewmodel.BaseRefreshViewModel;
import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.dl.playfun.ui.mine.broadcast.myprogram.ProgramItemViewModel;
import com.dl.playfun.ui.mine.broadcast.mytrends.TrendItemViewModel;
import com.dl.playfun.ui.radio.issuanceprogram.IssuanceProgramFragment;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

/**
 * Author: 彭石林
 * Time: 2021/10/9 15:02
 * Description: This is MyAllBroadcastViewModel
 */
public class MyAllBroadcastViewModel extends BaseRefreshViewModel<AppRepository> {

    public static final String RadioRecycleType_New = "new";
    public static final String RadioRecycleType_Topical = "topical";

    public int userId;
    public String avatar;

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent clickMore = new SingleLiveEvent<>();
        public SingleLiveEvent clickLike = new SingleLiveEvent<>();
        public SingleLiveEvent clickComment = new SingleLiveEvent<>();
        public SingleLiveEvent clickImage = new SingleLiveEvent<>();
        public SingleLiveEvent programSubject = new SingleLiveEvent<>();

        public SingleLiveEvent clickPublish = new SingleLiveEvent<>();
        public SingleLiveEvent clickSignUp = new SingleLiveEvent<>();
        public SingleLiveEvent clickCheck = new SingleLiveEvent<>();
        public SingleLiveEvent signUpSucceed = new SingleLiveEvent<>();

    }

    //跳转发布界面
    public BindingCommand toProgramVIew = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            start(IssuanceProgramFragment.class.getCanonicalName());
        }
    });

    public BindingRecyclerViewAdapter<MultiItemViewModel> adapter = new BindingRecyclerViewAdapter<>();
    public ObservableList<MultiItemViewModel> observableList = new ObservableArrayList<>();
    public ItemBinding<MultiItemViewModel> itemBinding = ItemBinding.of(new OnItemBind<MultiItemViewModel>() {
        @Override
        public void onItemBind(ItemBinding itemBinding, int position, MultiItemViewModel item) {
            //通过item的类型, 动态设置Item加载的布局
            String itemType = (String) item.getItemType();
            if (RadioRecycleType_New.equals(itemType)) {
                //设置new
                itemBinding.set(BR.viewModel, R.layout.item_trend);
            } else if (RadioRecycleType_Topical.equals(itemType)) {
//                设置topical
                itemBinding.set(BR.viewModel, R.layout.item_program);
            }
        }
    });

    public MyAllBroadcastViewModel(@NonNull @NotNull Application application, AppRepository model) {
        super(application, model);
        initUserDate();
        getBroadcast(1);
    }

    public void initUserDate() {
        UserDataEntity userDataEntity = model.readUserData();
        userId = userDataEntity.getId();
        avatar = userDataEntity.getAvatar();
    }

    @Override
    public void loadDatas(int page) {
        getBroadcast(page);
    }


    /**
     * 电台首页
     */
    private void getBroadcast(int page) {
        model.broadcastAll(page)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new BaseListEmptyObserver<BaseListDataResponse<BroadcastEntity>>(this) {
                    @Override
                    public void onSuccess(BaseListDataResponse<BroadcastEntity> response) {
                        super.onSuccess(response);
                        stateModel.setEmptyBroadcastCommand(StringUtils.getString(R.string.playfun_my_all_broadcast_empty), R.drawable.my_all_broadcast_empty_img, R.color.all_broadcast_empty, StringUtils.getString(R.string.playfun_task_fragment_task_new10), toProgramVIew);
                        if (page == 1) {
                            observableList.clear();
                        }
                        if (response.getData().getData() != null) {
                            for (BroadcastEntity broadcastEntity : response.getData().getData()) {
                                if (broadcastEntity.getNews() != null) {
//                                动态
                                    TrendItemViewModel trendItemViewModel = new TrendItemViewModel(MyAllBroadcastViewModel.this, broadcastEntity);
                                    trendItemViewModel.multiItemType(RadioRecycleType_New);
                                    observableList.add(trendItemViewModel);
                                } else {
//                                节目
                                    ProgramItemViewModel programItemViewModel = new ProgramItemViewModel(MyAllBroadcastViewModel.this, broadcastEntity);
                                    programItemViewModel.multiItemType(RadioRecycleType_Topical);
                                    observableList.add(programItemViewModel);
                                }
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                        stopRefreshOrLoadMore();
                    }
                });
    }

    //动态点赞
    public void newsGive(int posion) {
        model.newsGive(((TrendItemViewModel) observableList.get(posion)).newsEntityObservableField.get().getId())
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ToastUtils.showShort(R.string.playfun_give_success);
                        ((TrendItemViewModel) observableList.get(posion)).addGiveUser();
                        AppContext.instance().logEvent(AppsFlyerEvent.Like);
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //节目点赞
    public void topicalGive(int posion) {
        model.TopicalGive(((ProgramItemViewModel) observableList.get(posion)).topicalListEntityObservableField.get().getId())
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ToastUtils.showShort(R.string.playfun_give_success);
                        ((ProgramItemViewModel) observableList.get(posion)).addGiveUser();
                        AppContext.instance().logEvent(AppsFlyerEvent.Like);
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //动态评论
    public void newsComment(Integer id, String content, Integer toUserId, String toUserName) {
        model.newsComment(id, content, toUserId)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ToastUtils.showShort(R.string.playfun_comment_success);
                        for (int i = 0; i < observableList.size(); i++) {
                            if (observableList.get(i) instanceof TrendItemViewModel) {
                                if (id == ((TrendItemViewModel) observableList.get(i)).newsEntityObservableField.get().getId()) {
                                    AppContext.instance().logEvent(AppsFlyerEvent.Message);
                                    ((TrendItemViewModel) observableList.get(i)).addComment(id, content, toUserId, toUserName, model.readUserData().getNickname());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(RequestException e) {
                        if (e.getCode() == 10016) {
                            ToastUtils.showShort(StringUtils.getString(R.string.playfun_comment_close));
                            for (int i = 0; i < observableList.size(); i++) {
                                if (observableList.get(i) instanceof TrendItemViewModel) {
                                    if (id == ((TrendItemViewModel) observableList.get(i)).newsEntityObservableField.get().getId()) {
                                        AppContext.instance().logEvent(AppsFlyerEvent.Message);
                                        ((TrendItemViewModel) observableList.get(i)).newsEntityObservableField.get().getBroadcast().setIsComment(1);
                                    }
                                }
                            }
                        } else {
                            if (e.getMessage() != null) {
                                ToastUtils.showShort(e.getMessage());
                            } else {
                                ToastUtils.showShort(R.string.error_http_internal_server_error);
                            }

                            super.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //节目评论
    public void topicalComment(Integer id, String content, Integer toUserId, String toUserName) {
        model.topicalComment(id, content, toUserId)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ToastUtils.showShort(R.string.playfun_comment_success);
                        for (int i = 0; i < observableList.size(); i++) {
                            if (observableList.get(i) instanceof ProgramItemViewModel) {
                                if (id == ((ProgramItemViewModel) observableList.get(i)).topicalListEntityObservableField.get().getId()) {
                                    AppContext.instance().logEvent(AppsFlyerEvent.Message);
                                    ((ProgramItemViewModel) observableList.get(i)).addComment(id, content, toUserId, toUserName, model.readUserData().getNickname());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(RequestException e) {
                        if (e.getCode() == 10016) {
                            ToastUtils.showShort(StringUtils.getString(R.string.playfun_comment_close));
                            for (int i = 0; i < observableList.size(); i++) {
                                if (observableList.get(i) instanceof ProgramItemViewModel) {
                                    if (id == ((ProgramItemViewModel) observableList.get(i)).topicalListEntityObservableField.get().getId()) {
                                        AppContext.instance().logEvent(AppsFlyerEvent.Message);
                                        ((ProgramItemViewModel) observableList.get(i)).topicalListEntityObservableField.get().getBroadcast().setIsComment(1);
                                    }
                                }
                            }
                        } else {
                            super.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //节目结束报名
    public void TopicalFinish(int posion) {
        model.TopicalFinish(((ProgramItemViewModel) observableList.get(posion)).topicalListEntityObservableField.get().getId())
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ((ProgramItemViewModel) observableList.get(posion)).topicalListEntityObservableField.get().setIsEnd(1);
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //我要报名
    public void report(int posion, String imags) {
        model.singUp(((ProgramItemViewModel) observableList.get(posion)).topicalListEntityObservableField.get().getId(), imags)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ToastUtils.showShort(R.string.playfun_sign_up_success);
                        ((ProgramItemViewModel) observableList.get(posion)).report();
                        AppContext.instance().logEvent(AppsFlyerEvent.Apply);
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    public void imagUpload(String filePath, int posion) {
        Observable.just(filePath)
                .doOnSubscribe(this)
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return FileUploadUtils.ossUploadFile("radio/", FileUploadUtils.FILE_TYPE_IMAGE, s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String fileKey) {
                        dismissHUD();
                        report(posion, fileKey);
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissHUD();
                        ToastUtils.showShort(R.string.playfun_upload_failed);
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //开启/关闭评论
    public void setComment(int posion, String type) {
        int broadcastId;
        int isComment;
        if (type.equals(RadioRecycleType_Topical)) {
            broadcastId = ((ProgramItemViewModel) observableList.get(posion)).topicalListEntityObservableField.get().getBroadcast().getId();
            isComment = ((ProgramItemViewModel) observableList.get(posion)).topicalListEntityObservableField.get().getBroadcast().getIsComment();
        } else {
            broadcastId = ((TrendItemViewModel) observableList.get(posion)).newsEntityObservableField.get().getBroadcast().getId();
            isComment = ((TrendItemViewModel) observableList.get(posion)).newsEntityObservableField.get().getBroadcast().getIsComment();
        }
        model.setComment(broadcastId,
                isComment == 0 ? 1 : 0)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        if (type.equals(RadioRecycleType_Topical)) {
                            ToastUtils.showShort(((ProgramItemViewModel) observableList.get(posion)).topicalListEntityObservableField.get().getBroadcast().getIsComment() == 1 ? StringUtils.getString(R.string.playfun_open_comment_success) : StringUtils.getString(R.string.playfun_close_success));
                            ((ProgramItemViewModel) observableList.get(posion)).topicalListEntityObservableField.get().getBroadcast().setIsComment(
                                    ((ProgramItemViewModel) observableList.get(posion)).topicalListEntityObservableField.get().getBroadcast().getIsComment() == 0 ? 1 : 0);
                        } else {
                            ToastUtils.showShort(((TrendItemViewModel) observableList.get(posion)).newsEntityObservableField.get().getBroadcast().getIsComment() == 1 ? StringUtils.getString(R.string.playfun_open_comment_success) : StringUtils.getString(R.string.playfun_close_success));
                            ((TrendItemViewModel) observableList.get(posion)).newsEntityObservableField.get().getBroadcast().setIsComment(
                                    ((TrendItemViewModel) observableList.get(posion)).newsEntityObservableField.get().getBroadcast().getIsComment() == 0 ? 1 : 0);
                        }
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    public void checkTopical() {
        model.checkTopical()
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        uc.programSubject.call();
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //删除动态
    public void deleteNews(int posion) {
        model.deleteNews(((TrendItemViewModel) observableList.get(posion)).newsEntityObservableField.get().getId())
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        observableList.remove(posion);
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //删除节目
    public void deleteTopical(int posion) {
        model.deleteTopical(((ProgramItemViewModel) observableList.get(posion)).topicalListEntityObservableField.get().getId())
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        observableList.remove(posion);
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }
}
