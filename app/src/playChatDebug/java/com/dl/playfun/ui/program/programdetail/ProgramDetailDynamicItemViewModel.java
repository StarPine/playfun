package com.dl.playfun.ui.program.programdetail;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.dl.playfun.app.AppContext;
import com.dl.playfun.entity.SignsBeanEntity;
import com.dl.playfun.entity.UserDataEntity;
import com.dl.playfun.ui.userdetail.detail.UserDetailFragment;
import com.dl.playfun.utils.ExceptionReportUtils;

import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/**
 * @author wulei
 */
public class ProgramDetailDynamicItemViewModel extends MultiItemViewModel<ProgramDetailViewModel> {
    public ObservableField<SignsBeanEntity> signsBeanEntityObservableField = new ObservableField<>();
    //头像的点击事件
    public BindingCommand avatarClick = new BindingCommand(() -> {
        try {
            Bundle bundle = UserDetailFragment.getStartBundle(signsBeanEntityObservableField.get().getUserId());
            viewModel.start(UserDetailFragment.class.getCanonicalName(), bundle);
        } catch (Exception e) {
            ExceptionReportUtils.report(e);
        }
    });
    //联系私聊的点击事件
    public BindingCommand chatOnClickCommand = new BindingCommand(() -> {
        try {
            UserDataEntity userDataEntity = AppContext.instance().appRepository.readUserData();
            viewModel.isChat(signsBeanEntityObservableField.get().getUserId(), 1, signsBeanEntityObservableField.get().getUserId().toString(), signsBeanEntityObservableField.get().getNickname());
//            if (userDataEntity.getIsVip() == 1) {
//                ChatUtils.chatUser(signsBeanEntityObservableField.get().getUserId(), signsBeanEntityObservableField.get().getNickname(), viewModel);
//            } else {
//                if(userDataEntity.getSex()==0 || userDataEntity.getCertification()==1){
//                    viewModel.uc.clickPayChat.postValue(ConfigManager.getInstance().getImMoney());
//                }else{
//                    ToastUtils.showShort(signsBeanEntityObservableField.get().getSex() == 1 ? R.string.is_vip_call_him : R.string.is_vip_call_her);
//                }
//
//            }
        } catch (Exception e) {
            ExceptionReportUtils.report(e);
        }
    });
    //举报点击事件
    public BindingCommand reportCommand = new BindingCommand(() -> {
        try {
            viewModel.uc.clickReport.setValue(signsBeanEntityObservableField.get().getId());
        } catch (Exception e) {
            ExceptionReportUtils.report(e);
        }
    });

    public ProgramDetailDynamicItemViewModel(@NonNull ProgramDetailViewModel viewModel, SignsBeanEntity signsBeanEntity) {
        super(viewModel);
        signsBeanEntityObservableField.set(signsBeanEntity);
    }
}
