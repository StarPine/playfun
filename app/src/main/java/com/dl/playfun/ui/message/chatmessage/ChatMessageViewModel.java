package com.dl.playfun.ui.message.chatmessage;

import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.dl.playfun.R;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.app.AppsFlyerEvent;
import com.dl.playfun.data.AppRepository;
import com.dl.playfun.data.source.http.exception.RequestException;
import com.dl.playfun.data.source.http.observer.BaseObserver;
import com.dl.playfun.data.source.http.response.BaseDataResponse;
import com.dl.playfun.data.source.http.response.BaseResponse;
import com.dl.playfun.entity.BrowseNumberEntity;
import com.dl.playfun.entity.IMTransUserEntity;
import com.dl.playfun.entity.ImUserSigEntity;
import com.dl.playfun.entity.TokenEntity;
import com.dl.playfun.entity.UserDataEntity;
import com.dl.playfun.event.LoginExpiredEvent;
import com.dl.playfun.ui.mine.trace.man.TraceManFragment;
import com.dl.playfun.ui.userdetail.detail.UserDetailFragment;
import com.dl.playfun.utils.ToastCenterUtils;
import com.dl.playfun.viewmodel.BaseViewModel;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.interfaces.TUICallback;

import java.util.List;

import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * @author wulei
 */
public class ChatMessageViewModel extends BaseViewModel<AppRepository> {

    UIChangeObservable uc = new UIChangeObservable();

    public ObservableField<String> NewNumberText = new ObservableField<String>();
    //跳转谁看过我
    public BindingCommand traceOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            AppContext.instance().logEvent(AppsFlyerEvent.chat_seen_me);
            Bundle bundle = new Bundle();
            bundle.putInt("userId", model.readUserData().getId());
            start(TraceManFragment.class.getCanonicalName(), bundle);
            NewNumberText.set(null);
        }
    });

    public ChatMessageViewModel(@NonNull Application application, AppRepository repository) {
        super(application, repository);
    }
    //请求谁看过我、粉丝间隔时间
    private Long intervalTime = null;

    public void newsBrowseNumber() {
        long dayTime = System.currentTimeMillis();
        if (intervalTime != null && (dayTime / 1000) - intervalTime.longValue() <= 2) {
            return;
        }
        if (intervalTime == null) {
            intervalTime = dayTime / 1000;
        }
        model.newsBrowseNumber()
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new BaseObserver<BaseDataResponse<BrowseNumberEntity>>() {
                    @Override
                    public void onSuccess(BaseDataResponse<BrowseNumberEntity> browseNumberEntity) {
                        uc.loadBrowseNumber.setValue(browseNumberEntity.getData());
                    }
                });
    }
    /**
    * @Desc TODO(转换IM用户id)
    * @author 彭石林
    * @parame [ImUserId]
    * @return void
    * @Date 2022/4/2
    */
    public void transUserIM(String ImUserId,boolean userDetailView){
        model.transUserIM(ImUserId)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(dispose -> showHUD())
                .subscribe(new BaseObserver<BaseDataResponse<IMTransUserEntity>>() {

                    @Override
                    public void onSuccess(BaseDataResponse<IMTransUserEntity> response) {
                        IMTransUserEntity  imTransUserEntity = response.getData();
                        if(imTransUserEntity!=null && imTransUserEntity.getUserId()!=null){
                            if(userDetailView){//去往用户主页
                                Bundle bundle = UserDetailFragment.getStartBundle(imTransUserEntity.getUserId());
                                start(UserDetailFragment.class.getCanonicalName(), bundle);
                            }else{//进入私聊页面
                                userMessageCollation(imTransUserEntity.getUserId());
                            }

                        }
                    }
                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    public void userMessageCollation(int userId) {
        model.userMessageCollation(userId)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        uc.startChatUserView.postValue(userId);
                    }

                    @Override
                    public void onError(RequestException e) {
                        if (e.getCode() == 11111) { //注销
                            ToastCenterUtils.showShort(R.string.playfun_user_detail_user_disable3);
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

    public void flushSign() {
        model.flushSign()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new BaseObserver<BaseDataResponse<ImUserSigEntity>>() {

                    @Override
                    public void onSuccess(BaseDataResponse<ImUserSigEntity> response) {
                        ImUserSigEntity data = response.getData();
                        if (data == null || TextUtils.isEmpty(data.getUserSig())){
                            RxBus.getDefault().post(new LoginExpiredEvent());
                            return;
                        }
                        TokenEntity tokenEntity = model.readLoginInfo();
                        tokenEntity.setUserSig(data.getUserSig());
                        TUILogin.login(Utils.getContext(), model.readApiConfigManagerEntity().getImAppId(), tokenEntity.getUserID(), tokenEntity.getUserSig(), new TUICallback() {
                            @Override
                            public void onSuccess() {
                                model.saveLoginInfo(tokenEntity);
                                uc.loginSuccess.call();
                            }

                            @Override
                            public void onError(int errorCode, String errorMessage) {
                                RxBus.getDefault().post(new LoginExpiredEvent());
                            }
                        });
                    }

                    @Override
                    public void onError(RequestException e) {
                        RxBus.getDefault().post(new LoginExpiredEvent());
                    }

                    @Override
                    public void onComplete() {
                    }

                });
    }

    public static class UIChangeObservable {
        public SingleLiveEvent<BrowseNumberEntity> loadBrowseNumber = new SingleLiveEvent<>();
        //跳转进入私聊页面
        public SingleLiveEvent<Integer> startChatUserView = new SingleLiveEvent<>();
        public SingleLiveEvent<Void> loginSuccess = new SingleLiveEvent<>();
    }

}
