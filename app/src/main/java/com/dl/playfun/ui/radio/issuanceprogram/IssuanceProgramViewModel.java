package com.dl.playfun.ui.radio.issuanceprogram;

import android.app.Application;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.app.AppsFlyerEvent;
import com.dl.playfun.data.AppRepository;
import com.dl.playfun.data.source.http.exception.RequestException;
import com.dl.playfun.data.source.http.observer.BaseDisposableObserver;
import com.dl.playfun.data.source.http.observer.BaseObserver;
import com.dl.playfun.data.source.http.response.BaseDataResponse;
import com.dl.playfun.data.source.http.response.BaseResponse;
import com.dl.playfun.entity.ConfigItemEntity;
import com.dl.playfun.entity.DatingObjItemEntity;
import com.dl.playfun.entity.StatusEntity;
import com.dl.playfun.entity.ThemeItemEntity;
import com.dl.playfun.entity.UserDataEntity;
import com.dl.playfun.event.BadioEvent;
import com.dl.playfun.event.SelectMediaSourcesEvent;
import com.dl.playfun.manager.ConfigManager;
import com.dl.playfun.utils.ApiUitl;
import com.dl.playfun.utils.FileUploadUtils;
import com.dl.playfun.viewmodel.BaseViewModel;
import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.tencent.qcloud.tuicore.Status;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;


/**
 * @author wulei
 */
public class IssuanceProgramViewModel extends BaseViewModel<AppRepository> {
    //消费者
    private Disposable MediaStoreDisposable;
    //用户选择媒体文件：图片/视频
    public ObservableField<String> selectMediaPath = new ObservableField<>();
    public boolean isSelectedVideo = false;
    //心情选中
    public ObservableBoolean moolCheck = new ObservableBoolean(true);
    public ObservableField<String> selThemeItemName = new ObservableField<>("#" + StringUtils.getString(R.string.playfun_mood_item_id1));
    public DatingObjItemEntity $datingObjItemEntity;
    //约会对象
    public BindingRecyclerViewAdapter<RadioDatingItemViewModel> objAdapter = new BindingRecyclerViewAdapter<>();
    public ItemBinding<RadioDatingItemViewModel> objItemBinding = ItemBinding.of(BR.viewModel, R.layout.item_radio_dating);
    public ObservableList<RadioDatingItemViewModel> objItems = new ObservableArrayList<>();
    //内容
    public ObservableField<String> programDesc = new ObservableField<>();
    public ObservableField<ConfigItemEntity> chooseCityItem = new ObservableField<>();
    public ObservableField<String> addressName = new ObservableField<>();
    public ObservableField<String> address = new ObservableField<>();
    public ObservableField<Double> lat = new ObservableField<>();
    public ObservableField<Double> lng = new ObservableField<>();

    public List<String> images = new ArrayList<>();
    public ObservableField<Integer> is_comment = new ObservableField<>(0);
    public ObservableField<Integer> is_hide = new ObservableField<>(0);
    //    城市
    public List<ConfigItemEntity> list_chooseCityItem = new ArrayList<>();
    public Integer sex;
    public ConfigManager configManager;

    public BindingCommand removeMediaPath = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            selectMediaPath.set(null);
        }
    });
    UIChangeObservable uc = new UIChangeObservable();
    //跳转视频、图片剪辑页面
    public BindingCommand toClipImageVideoView = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (Status.mIsShowFloatWindow){
                ToastUtils.showShort(R.string.audio_in_call);
                return;
            }
            uc.startVideoActivity.call();
        }
    });
    //发布
    public BindingCommand issuanceClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            AppContext.instance().logEvent(AppsFlyerEvent.Post2);
            publishCheck(1);
        }
    });

    public IssuanceProgramViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
        list_chooseCityItem.addAll(model.readCityConfig());
        sex = model.readUserData().getSex();
        configManager = ConfigManager.getInstance();
    }

    public void sendConfirm() {
        String mediaPath = selectMediaPath.get();
        if (!StringUtils.isEmpty(mediaPath)) {
            uploadAvatar(mediaPath);
        } else {
            topicalCreateMood();
        }
    }

    private void publishCheck(final int type) {
        model.publishCheck(type)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseDataResponse<StatusEntity>>() {
                    @Override
                    public void onSuccess(BaseDataResponse<StatusEntity> response) {
                        dismissHUD();
                        if (response.getData().getStatus() == 1) {
                            sendConfirm();
                        } else {
                            if (sex == 1) {
                                if (model.readUserData().getIsVip() != 1) {
                                    uc.clickNotVip.setValue(type);
                                    return;
                                }
                            }
                            sendConfirm();
                        }
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    public void uploadAvatar(String filePath) {
        Observable.just(filePath)
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showHUD())
                .subscribeOn(Schedulers.io())
                .map((Function<String, String>) s -> {
                    if (filePath.endsWith(".mp4")) {
                        return FileUploadUtils.ossUploadFileVideo("Issuance/", FileUploadUtils.FILE_TYPE_IMAGE, s, new FileUploadUtils.FileUploadProgressListener() {
                            public void fileCompressProgress(int progress) {
                                showProgressHUD(String.format(StringUtils.getString(R.string.playfun_compressing), progress), progress);
                            }

                            @Override
                            public void fileUploadProgress(int progress) {
                                showProgressHUD(String.format(StringUtils.getString(R.string.playfun_uploading), progress), progress);
                            }
                        });
                    } else {
                        return FileUploadUtils.ossUploadFile("Issuance/", FileUploadUtils.FILE_TYPE_IMAGE, s, new FileUploadUtils.FileUploadProgressListener() {
                            public void fileCompressProgress(int progress) {
                                showProgressHUD(String.format(StringUtils.getString(R.string.playfun_compressing), progress), progress);
                            }

                            @Override
                            public void fileUploadProgress(int progress) {
                                showProgressHUD(String.format(StringUtils.getString(R.string.playfun_uploading), progress), progress);
                            }
                        });
                    }

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String fileKey) {
                        dismissHUD();
                        if (fileKey != null) {
                            if (fileKey.endsWith(".mp4")) {
                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                try {
                                    //根据url获取缩略图
                                    retriever.setDataSource(selectMediaPath.get());
                                    //获得第一帧图片
                                    Bitmap bitmap = retriever.getFrameAtTime(1);
                                    String filename = ApiUitl.getDiskCacheDir(Utils.getApp()) + "/Overseas" + ApiUitl.getDateTimeFileName() + ".jpg";
                                    ApiUitl.saveBitmap(bitmap, filename, flag -> {
                                        //取视频第一帧图片保存成功后再次上报发送
//                                        File deleteFile = new File(filePath);
//                                        deleteFile.delete();
                                        selectMediaPath.set(fileKey);
                                        uploadAvatar(filename);
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    retriever.release();
                                }
                            } else {
//                                File deleteFile = new File(filePath);
//                                deleteFile.delete();
                                images.add(fileKey);
                                topicalCreateMood();
                            }
                        }

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

    public void OnClickItem(DatingObjItemEntity datingObjItemEntity) {
        selThemeItemName.set("#" + datingObjItemEntity.getName());
        $datingObjItemEntity = datingObjItemEntity;
    }

    public void topicalCreateMood() {
        String video = null;
        if (!StringUtils.isEmpty(selectMediaPath.get())) {
            if (selectMediaPath.get().endsWith(".mp4")) {
                video = selectMediaPath.get();
            }
        }
        model.topicalCreateMood(programDesc.get(), null, images == null ? null : images, is_comment.get(), is_hide.get(), lng.get(), lat.get(), video, $datingObjItemEntity.getId())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseDisposableObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        ToastUtils.showShort(R.string.playfun_issuance_success);
                        RxBus.getDefault().post(new BadioEvent(1));
                        UserDataEntity userDataEntity = model.readUserData();
                        if (ObjectUtils.isEmpty(userDataEntity.getPermanentCityIds())) {
                            List<Integer> list = new ArrayList<>();
                            list.add(1);
                            userDataEntity.setPermanentCityIds(list);
                            model.saveUserData(userDataEntity);
                        }
                        pop();
                    }

                    @Override
                    public void onError(RequestException e) {
                        ToastUtils.showShort(e.getMessage());
                        images.clear();
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    public class UIChangeObservable {
        public SingleLiveEvent<Integer> clickNotVip = new SingleLiveEvent<>();
        public SingleLiveEvent clickAddress = new SingleLiveEvent<>();
        //跳转Activity
        public SingleLiveEvent startVideoActivity = new SingleLiveEvent<>();
    }

    @Override
    public void registerRxBus() {
        super.registerRxBus();
        MediaStoreDisposable = RxBus.getDefault().toObservable(SelectMediaSourcesEvent.class).subscribe(selectMediaSourcesEvent -> {
            selectMediaPath.set(selectMediaSourcesEvent.getPath());
        });
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(MediaStoreDisposable);
    }


}