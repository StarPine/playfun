package com.dl.playfun.ui.mine.likelist;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.dl.playfun.data.AppRepository;
import com.dl.playfun.data.source.http.exception.RequestException;
import com.dl.playfun.data.source.http.observer.BaseListEmptyObserver;
import com.dl.playfun.data.source.http.observer.BaseObserver;
import com.dl.playfun.data.source.http.response.BaseListDataResponse;
import com.dl.playfun.data.source.http.response.BaseResponse;
import com.dl.playfun.entity.TraceEntity;
import com.dl.playfun.event.LikeChangeEvent;
import com.dl.playfun.event.TraceEvent;
import com.dl.playfun.manager.PermissionManager;
import com.dl.playfun.viewmodel.BaseViewModel;
import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.dl.playfun.ui.userdetail.detail.UserDetailFragment;

import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * @author wulei
 */
public class LikeListViewModel extends BaseViewModel<AppRepository> {

    public BindingRecyclerViewAdapter<LikeItemViewModel> adapter = new BindingRecyclerViewAdapter<>();
    public ObservableList<LikeItemViewModel> observableList = new ObservableArrayList<>();
    public ItemBinding<LikeItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.trace_item_park_list);
    public Integer totalCount = 0;
    public UIChangeObservable uc = new UIChangeObservable();
    private int currentPage = 1;
    //下拉刷新
    public BindingCommand onRefreshCommand = new BindingCommand(() -> {
        currentPage = 1;
        loadDatas(currentPage);
    });
    public BindingCommand onLoadMoreCommand = new BindingCommand(() -> nextPage());

    public LikeListViewModel(@NonNull Application application, AppRepository repository) {
        super(application, repository);
    }

    protected void nextPage() {
        currentPage++;
        loadDatas(currentPage);
    }

    @Override
    public void onEnterAnimationEnd() {
        super.onEnterAnimationEnd();
        loadDatas(1);
    }

    public void loadDatas(int page) {
        collect(page);
    }

    /**
     * 停止下拉刷新或加载更多动画
     */
    protected void stopRefreshOrLoadMore() {
        if (currentPage == 1) {
            uc.finishRefreshing.call();
        } else {
            uc.finishLoadmore.call();
        }
    }

    /**
     * @return void
     * @Desc TODO(跳转前往用户详细界面)
     * @author 彭石林
     * @parame [userId]
     * @Date 2021/8/5
     */
    public void toUserDetails(Integer userId) {
        Bundle bundle = UserDetailFragment.getStartBundle(userId);
        start(UserDetailFragment.class.getCanonicalName(), bundle);
    }

    public void collect(Integer page) {
        model.collect(page)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new BaseListEmptyObserver<BaseListDataResponse<TraceEntity>>(this) {
                    @Override
                    public void onSuccess(BaseListDataResponse<TraceEntity> response) {
                        super.onSuccess(response);
                        if (currentPage == 1) {
                            observableList.clear();
                        }
                        for (TraceEntity itemEntity : response.getData().getData()) {
                            LikeItemViewModel item = new LikeItemViewModel(LikeListViewModel.this, itemEntity, 0);
                            observableList.add(item);
                        }
                        totalCount = response.getData().getTotal();
                        RxBus.getDefault().post(new TraceEvent(response.getData().getData().size(), 0));
                    }

                    @Override
                    public void onComplete() {
                        stopRefreshOrLoadMore();
                        uc.loadRefresh.call();
                    }
                });
    }

    public void delLike(int position) {
        TraceEntity traceEntity = observableList.get(position).itemEntity.get();
        model.deleteCollect(traceEntity.getId())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        observableList.remove(position);
                        RxBus.getDefault().post(new LikeChangeEvent(LikeListViewModel.this, traceEntity.getId(), false));
                    }

                    @Override
                    public void onError(RequestException e) {
                        super.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dismissHUD();
                    }
                });
    }

    public void addLike(int position) {
        TraceEntity traceEntity = observableList.get(position).itemEntity.get();
        if (!PermissionManager.getInstance().canCollectUser(traceEntity.getSex())) {
            if (traceEntity.getSex() == 1) {
                ToastUtils.showShort(R.string.playfun_men_cannot_collect_men);
                traceEntity.setFollow(false);
            } else {
                ToastUtils.showShort(R.string.playfun_lady_cannot_collect_lady);
                traceEntity.setFollow(false);
            }
            return;
        }
        model.addCollect(traceEntity.getId())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        traceEntity.setFollow(true);
                        RxBus.getDefault().post(new LikeChangeEvent(LikeListViewModel.this, observableList.get(position).itemEntity.get().getId(), true));
                    }

                    @Override
                    public void onError(RequestException e) {
                        super.onError(e);
                        traceEntity.setFollow(false);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dismissHUD();
                    }
                });
    }

    public class UIChangeObservable {
        //取消关注
        public SingleLiveEvent<Integer> clickDelLike = new SingleLiveEvent<>();
        //添加关注
        public SingleLiveEvent<Integer> clickAddLike = new SingleLiveEvent<>();

        //完成刷新
        public SingleLiveEvent<Void> loadRefresh = new SingleLiveEvent<>();

        //下拉刷新开始
        public SingleLiveEvent<Void> startRefreshing = new SingleLiveEvent<>();
        //下拉刷新完成
        public SingleLiveEvent<Void> finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent<Void> finishLoadmore = new SingleLiveEvent<>();

    }

}
