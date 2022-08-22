package com.dl.playfun.ui.message.chatdetail;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.dl.playfun.app.AppConfig;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.app.AppViewModelFactory;
import com.dl.playfun.app.AppsFlyerEvent;
import com.dl.playfun.data.source.local.LocalDataSourceImpl;
import com.dl.playfun.databinding.FragmentChatDetailBinding;
import com.dl.playfun.entity.ApiConfigManagerEntity;
import com.dl.playfun.entity.CrystalDetailsConfigEntity;
import com.dl.playfun.entity.EvaluateItemEntity;
import com.dl.playfun.entity.GiftBagEntity;
import com.dl.playfun.entity.GoodsEntity;
import com.dl.playfun.entity.LocalMessageIMEntity;
import com.dl.playfun.entity.MessageRuleEntity;
import com.dl.playfun.entity.PhotoAlbumEntity;
import com.dl.playfun.entity.TagEntity;
import com.dl.playfun.entity.TaskRewardReceiveEntity;
import com.dl.playfun.entity.UserDataEntity;
import com.dl.playfun.event.MessageGiftNewEvent;
import com.dl.playfun.manager.ConfigManager;
import com.dl.playfun.ui.base.BaseToolbarFragment;
import com.dl.playfun.ui.certification.certificationfemale.CertificationFemaleFragment;
import com.dl.playfun.ui.certification.certificationmale.CertificationMaleFragment;
import com.dl.playfun.ui.dialog.GiftBagDialog;
import com.dl.playfun.ui.message.chatdetail.notepad.NotepadActivity;
import com.dl.playfun.ui.message.photoreview.PhotoReviewFragment;
import com.dl.playfun.ui.message.sendcoinredpackage.SendCoinRedPackageFragment;
import com.dl.playfun.ui.mine.myphotoalbum.MyPhotoAlbumFragment;
import com.dl.playfun.ui.mine.vipsubscribe.VipSubscribeFragment;
import com.dl.playfun.ui.mine.wallet.girl.TwDollarMoneyFragment;
import com.dl.playfun.ui.mine.wallet.recharge.RechargeActivity;
import com.dl.playfun.ui.mine.webview.WebViewFragment;
import com.dl.playfun.ui.userdetail.detail.UserDetailFragment;
import com.dl.playfun.ui.userdetail.report.ReportUserFragment;
import com.dl.playfun.utils.ApiUitl;
import com.dl.playfun.utils.AutoSizeUtils;
import com.dl.playfun.utils.ImageUtils;
import com.dl.playfun.utils.ImmersionBarUtils;
import com.dl.playfun.utils.LogUtils;
import com.dl.playfun.utils.PictureSelectorUtil;
import com.dl.playfun.utils.StringUtil;
import com.dl.playfun.utils.Utils;
import com.dl.playfun.widget.coinrechargesheet.CoinRechargeSheetView;
import com.dl.playfun.widget.coinrechargesheet.GameCoinExchargeSheetView;
import com.dl.playfun.widget.dialog.MMAlertDialog;
import com.dl.playfun.widget.dialog.MVDialog;
import com.dl.playfun.widget.dialog.MessageDetailDialog;
import com.dl.playfun.widget.dialog.TraceDialog;
import com.dl.playfun.widget.dialog.WebViewDialog;
import com.google.gson.Gson;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGASoundManager;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.coustom.CustomIMTextEntity;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.Status;
import com.tencent.qcloud.tuicore.util.ConfigManagerUtil;
import com.tencent.qcloud.tuikit.tuichat.bean.ChatInfo;
import com.tencent.qcloud.tuikit.tuichat.bean.message.CustomImageMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.component.AudioPlayer;
import com.tencent.qcloud.tuikit.tuichat.presenter.C2CChatPresenter;
import com.tencent.qcloud.tuikit.tuichat.presenter.ChatPresenter;
import com.tencent.qcloud.tuikit.tuichat.ui.interfaces.OnItemClickListener;
import com.tencent.qcloud.tuikit.tuichat.ui.view.input.InputView;
import com.tencent.qcloud.tuikit.tuichat.ui.view.message.MessageRecyclerView;
import com.tencent.qcloud.tuikit.tuichat.util.ChatMessageBuilder;
import com.tencent.qcloud.tuikit.tuichat.util.TUIChatUtils;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * @author wulei
 */
public class ChatDetailFragment extends BaseToolbarFragment<FragmentChatDetailBinding, ChatDetailViewModel> implements CustomChatInputFragment.CustomChatInputFragmentListener, InputView.SendOnClickCallback {
    public static final String CHAT_INFO = "chatInfo";

    public static final String TAG = "ChatDetailFragment";
    private ChatInfo mChatInfo;
    private InputView inputLayout;
    private String toSendMessageText = null;

    private String AudioProfitTips = null;
    private String VideoProfitTips = null;

    //快速评价点击更多延迟2秒
    private Long intervalTime = null;
    //SVGA动画view
    private SVGAImageView giftView;

    private GiftBagDialog giftBagDialog;
    //对方用户id
    private Integer toUserDataId = null;
    private C2CChatPresenter presenter;
    private TUIMessageBean photoBean = null;

    //默认记录马上视频的距离底部宽高
    private volatile int defBottomMargin = 0;
    private volatile int defBottomMarginHeight = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ImmersionBarUtils.setupStatusBar(this, true, true);
        return view;
    }

    @Override
    public void initParam() {
        super.initParam();
        mChatInfo = (ChatInfo) getArguments().getSerializable(CHAT_INFO);
        toSendMessageText = getArguments().getString("message");
        //获取对方IM id
        toUserDataId = getArguments().getInt("toUserId");
        //SVGA播放初始化
        SVGASoundManager.INSTANCE.init();
        SVGAParser.Companion.shareParser().init(this.getContext());
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AutoSizeUtils.applyAdapt(this.getResources());
        return R.layout.fragment_chat_detail;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ChatDetailViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(mActivity.getApplication());
        return ViewModelProviders.of(this, factory).get(ChatDetailViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        giftView = binding.giftView;
        binding.chatLayout.getTitleBar().setVisibility(View.GONE);
        //非客服账号加载用户标签和状态
        if (!mChatInfo.getId().startsWith(AppConfig.CHAT_SERVICE_USER_ID)) {
            binding.rlLayout.setVisibility(View.VISIBLE);
            binding.ivNotepad.setVisibility(View.VISIBLE);
            binding.ivSetting.setVisibility(View.VISIBLE);
            initCallVideoHint();
        }
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        if (mChatInfo == null) {
            return;
        }
        hideExchangeRules();
        viewModel.TMToUserId = mChatInfo.getId();
        //非客服账号加载用户标签和状态
        if (!mChatInfo.getId().contains(AppConfig.CHAT_SERVICE_USER_ID)) {
            viewModel.loadUserInfo(getTaUserIdIM());
            viewModel.loadTagUser(String.valueOf(getTaUserIdIM()));
            initCallVideoHint();
            viewModel.isShoweCallingVideo.set(!Status.mIsShowFloatWindow);
        }else {
            viewModel.isHideExchangeRules.set(true);
            viewModel.isShoweCallingVideo.set(false);
        }
        initChatView();
        int userId = getTaUserIdIM(); //获取当前聊天对象的ID
        if (userId != 0) {
            //加载聊天规则
            viewModel.getMessageRule();
            //聊天价格配置
            viewModel.getPriceConfig(userId);
            viewModel.getPhotoAlbum(getTaUserIdIM());
        }else {
            binding.chatLayout.setChatInfo(mChatInfo);
        }
    }

    /**
     * 隐藏水晶兑换规则弹框
     */
    private void hideExchangeRules() {
        CrystalDetailsConfigEntity crystalDetailsConfig = ConfigManager.getInstance().getAppRepository().readCrystalDetailsConfig();
        boolean isHideExchangeRules = ConfigManagerUtil.getInstance().getExchangeRulesFlag();
        boolean isMale = ConfigManager.getInstance().isMale();
        if (isMale){
            if (crystalDetailsConfig.getMaleIsShow() != 1 || isHideExchangeRules){
                viewModel.isHideExchangeRules.set(true);
            }else {
                viewModel.isHideExchangeRules.set(false);
            }
        }else {
            if (crystalDetailsConfig.getFemaleIsShow() != 1 || isHideExchangeRules){
                viewModel.isHideExchangeRules.set(true);
            }else {
                viewModel.isHideExchangeRules.set(false);
            }
        }
    }

    public void initCallVideoHint() {
        defBottomMarginHeight = dp2px(mActivity, 30);
        if (mChatInfo != null && mChatInfo.getId() != null) {
            List<String> userList = new ArrayList<>();
            userList.add(mChatInfo.getId());
            //获取用户资料
            V2TIMManager.getInstance().getUsersInfo(userList, new V2TIMValueCallback<List<V2TIMUserFullInfo>>() {
                @Override
                public void onSuccess(List<V2TIMUserFullInfo> v2TIMUserFullInfos) {
                    if(mActivity==null || mActivity.isFinishing()){
                        return;
                    }
                    if (v2TIMUserFullInfos != null && !v2TIMUserFullInfos.isEmpty()) {
                        String faceUrl = v2TIMUserFullInfos.get(0).getFaceUrl();
                        if (faceUrl != null) {
                            Glide.with(mActivity).load(faceUrl)
                                    .error(R.drawable.default_avatar)
                                    .placeholder(R.drawable.default_avatar)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(binding.imgFaceAvatar);
                        }
                    }
                }

                @Override
                public void onError(int code, String desc) {
                    //错误码 code 和错误描述 desc，可用于定位请求失败原因
                    Log.e("获取用户信息失败", "getUsersProfile failed: " + code + " desc");
                }
            });
        }

    }

    public int dp2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    @Override
    public void initViewObservable() {
        super.initViewObservable();
        //拨打视频电话
        viewModel.uc.callVideoViewEvent.observe(this, event -> {
            if (viewModel.tagEntitys.get() != null) {
                if (viewModel.tagEntitys.get().getBlacklistStatus() == 1 || viewModel.tagEntitys.get().getBlacklistStatus() == 3) {
                    Toast.makeText(mActivity, R.string.playfun_chat_detail_pull_black_other, Toast.LENGTH_SHORT).show();
                    return;
                } else if (viewModel.tagEntitys.get().getBlacklistStatus() == 2) {
                    Toast.makeText(mActivity, R.string.playfun_chat_detail_blocked, Toast.LENGTH_SHORT).show();
                    return;
                }
                new RxPermissions(mActivity)
                        .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) {
                                AppContext.instance().logEvent(AppsFlyerEvent.im_video_call);
                                viewModel.getCallingInvitedInfo(2, getUserIdIM(), mChatInfo.getId());
                            } else {
                                TraceDialog.getInstance(mActivity)
                                        .setCannelOnclick(dialog -> {

                                        })
                                        .setConfirmOnlick(dialog -> new RxPermissions(mActivity)
                                                .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                                                .subscribe(granted1 -> {
                                                    if (granted1) {
                                                        AppContext.instance().logEvent(AppsFlyerEvent.im_video_call);
                                                        viewModel.getCallingInvitedInfo(2, getUserIdIM(), mChatInfo.getId());
                                                    }
                                                }))
                                        .AlertCallAudioPermissions().show();
                            }
                        });
            }
        });
        viewModel.uc.sendDialogViewEvent.observe(this, event -> {
            paySelectionboxChoose(false);
        });
        //跳转笔记界面
        viewModel.uc.starNotepad.observe(this, event -> {
            Intent intent = new Intent(mActivity, NotepadActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("toUserId", toUserDataId);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        });
        //播放SVGA动画
        viewModel.uc.signGiftAnimEvent.observe(this, animEvent -> {
            //调用播放
            startSVGAnimotion();
        });
        //im价格加载提醒
        viewModel.uc.imProfit.observe(this, unused -> {
            String videoTips = viewModel.priceConfigEntityField.getCurrent().getVideoTips();
            if (ConfigManager.getInstance().getTipMoneyShowFlag() && videoTips != null && videoTips.length() > 0) {
                String replaceEnd = videoTips.replaceAll("\\(|\\)", "");
                inputLayout.setProfitTip(replaceEnd, true);
            } else {
                inputLayout.setProfitTip("", false);
            }

        });
        viewModel.uc.sendUserGiftError.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                paySelectionboxChoose(true);

            }
        });
        viewModel.uc.resultMessageRule.observe(this, new Observer<List<MessageRuleEntity>>() {
            @Override
            public void onChanged(List<MessageRuleEntity> messageRuleEntities) {
                //遍历聊天规则
                for (MessageRuleEntity messageRuleEntity : messageRuleEntities) {
                    //相册
//                    askPhotoData(messageRuleEntity);
                }
            }
        });
        //删除评价
        viewModel.uc.removeEvaluateMessage.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                String eventId = mChatInfo.getId() + "_evaluate";
                LocalMessageIMEntity localMessageIMEntity = LocalDataSourceImpl.getInstance().readLocalMessageIM(eventId);
                if (localMessageIMEntity != null) {
                    removeLocalMessage(localMessageIMEntity, eventId, true);
                }
            }
        });
        //点击更多评价
        viewModel.uc.AlertMEvaluate.observe(this, new Observer<List<EvaluateItemEntity>>() {
            @Override
            public void onChanged(List<EvaluateItemEntity> evaluateItemEntities) {
                MMAlertDialog.DialogChatDetail(getContext(), false, 0, evaluateItemEntities, new MMAlertDialog.DilodAlertInterface() {
                    @Override
                    public void confirm(DialogInterface dialog, int which, int sel_Index) {
                        viewModel.commitUserEvaluate(getTaUserIdIM(), evaluateItemEntities.get(sel_Index).getTagId(), dialog);
                    }

                    @Override
                    public void cancel(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
        //发送IM评价插入
        viewModel.uc.sendIMEvaluate.observe(this, new Observer<List<EvaluateItemEntity>>() {
            @Override
            public void onChanged(List<EvaluateItemEntity> evaluateItemEntities) {
                String eventId = mChatInfo.getId() + "_evaluate";
                LocalMessageIMEntity localMessageIMEntity = LocalDataSourceImpl.getInstance().readLocalMessageIM(eventId);
                //if(localMessageIMEntity==null) {
                try {
                    addLocalMessage("message_evaluate", eventId, GsonUtils.toJson(evaluateItemEntities, List.class));
                } catch (Exception e) {

                }
                //}else{
                //removeLocalMessage(localMessageIMEntity,eventId);
                //}
            }
        });
        //效验用户是否已经评价 已经评价不会发出
        viewModel.uc.canEvaluate.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean flagBoolean) {
                if (flagBoolean) {
                    viewModel.getUserEvaluate(getTaUserIdIM(), true);
                } else {
                    String eventId = mChatInfo.getId() + "_evaluate";
                    LocalMessageIMEntity localMessageIMEntity = LocalDataSourceImpl.getInstance().readLocalMessageIM(eventId);
                    if (localMessageIMEntity != null) {
                        removeLocalMessage(localMessageIMEntity, eventId, true);
                    }
                }
            }
        });
        //插入相册
        viewModel.uc.putPhotoAlbumEntity.observe(this, new Observer<PhotoAlbumEntity>() {
            @Override
            public void onChanged(PhotoAlbumEntity photoAlbumEntity) {
                try {
//                    String eventId = mChatInfo.getId() + "_photoAlbum";
//                    addLocalMessage("message_photo", eventId, GsonUtils.toJson(photoAlbumEntity));
                    if (photoAlbumEntity != null){
                        String objData = GsonUtils.toJson(photoAlbumEntity);
                        Map<String, Object> custom_local_data = new HashMap<>();
                        custom_local_data.put("type", "message_photo");
                        custom_local_data.put("data", objData);
                        photoBean = ChatMessageBuilder.buildTextMessage(GsonUtils.toJson(custom_local_data));
                        presenter.setPhotoBean(photoBean);
                    }

                } catch (Exception e) {

                }finally {
                    binding.chatLayout.setChatInfo(mChatInfo);
                }
            }
        });
        viewModel.uc.clickConnMic.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                if (inputLayout != null) {
                    //拨打语音电话
                    inputLayout.startAudioCall();
                }
            }
        });
        viewModel.uc.loadTag.observe(this, new Observer<TagEntity>() {
            @Override
            public void onChanged(TagEntity tagEntity) {
                if (tagEntity.getThisIsGg().intValue() == 1) {//当前用户是GG
                    viewModel.isTagShow.set(true);
                    if (tagEntity.getToIsInvite().intValue() == 1) {//是否填写邀请码 0否 1是
                        binding.tagTitle.setText(R.string.playfun_user_message_tag2);
                    } else {
                        binding.tagTitle.setText(R.string.playfun_user_message_tag1);
                    }
                }
                if (tagEntity.getToIsGg().intValue() == 1) {//对方用户是GG
                    //创建订单时绑定关系
                    viewModel.ChatInfoId = getTaUserIdIM();
                }
            }
        });
        //对方忙线
        viewModel.uc.otherBusy.observe(this, o -> {
            TraceDialog.getInstance(ChatDetailFragment.this.getContext())
                    .chooseType(TraceDialog.TypeEnum.CENTER)
                    .setTitle(StringUtils.getString(R.string.playfun_other_busy_title))
                    .setContent(StringUtils.getString(R.string.playfun_other_busy_text))
                    .setConfirmText(StringUtils.getString(R.string.playfun_mine_trace_delike_confirm))
                    .setConfirmOnlick(new TraceDialog.ConfirmOnclick() {
                        @Override
                        public void confirm(Dialog dialog) {

                            dialog.dismiss();
                        }
                    }).TraceVipDialog().show();
        });
        //水晶兑换规则
        viewModel.uc.clickCrystalExchange.observe(this, data -> {
            TraceDialog.getInstance(ChatDetailFragment.this.getContext())
                    .setConfirmOnlick(new TraceDialog.ConfirmOnclick() {
                        @Override
                        public void confirm(Dialog dialog) {
                            ConfigManagerUtil.getInstance().putExchangeRulesFlag(true);
                            viewModel.isHideExchangeRules.set(true);
                        }
                    })
                    .getCrystalExchange(data)
                    .show();
        });

        //更多按钮
        viewModel.uc.clickMore.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                int userId = getTaUserIdIM();
                if (mChatInfo == null || userId < 1) {
                    return;
                }
                if (viewModel.inBlacklist.get()) {
                    viewModel.menuBlockade.set(getString(R.string.playfun_remove_black_shield_both_sides));
                } else {
                    viewModel.menuBlockade.set(getString(R.string.playfun_pull_black_shield_both_sides));
                }
                if (viewModel.isTrack.get()) {
                    viewModel.menuTrack.set(getString(R.string.playfun_cancel_zuizong));
                } else {
                    viewModel.menuTrack.set(getString(R.string.playfun_mine_my_likes));
                }

                showMoreMenu(userId);
            }
        });
        //首次收入弹窗显示
        viewModel.uc.firstImMsgDialog.observe(this, new Observer<TaskRewardReceiveEntity>() {
            @Override
            public void onChanged(TaskRewardReceiveEntity taskRewardReceiveEntity) {
                if (taskRewardReceiveEntity != null) {
                    TraceDialog.getInstance(getContext())
                            .setConfirmOnlick(new TraceDialog.ConfirmOnclick() {
                                @Override
                                public void confirm(Dialog dialog) {
                                    AppContext.instance().logEvent(AppsFlyerEvent.task_first_profit_toWith);
                                    dialog.dismiss();
                                    //女性是否进行过真人认证
                                    if (!ConfigManager.getInstance().isCertification()) {
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("dialog_tw_money", true);
                                        viewModel.start(CertificationFemaleFragment.class.getCanonicalName(), bundle);
                                    } else {
                                        viewModel.start(TwDollarMoneyFragment.class.getCanonicalName());
                                    }
                                }
                            })
                            .setCannelOnclick(new TraceDialog.CannelOnclick() {
                                @Override
                                public void cannel(Dialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .AlertTaskMoney(getContext().getDrawable(R.drawable.completed), taskRewardReceiveEntity.getTaskType(), taskRewardReceiveEntity.getTaskName(), taskRewardReceiveEntity.getMsg()).show();
                }
            }
        });
        //文件上传成功后发送IM消息
        viewModel.uc.signUploadSendMessage.observe(this, new Observer<TUIMessageBean>() {
            @Override
            public void onChanged(TUIMessageBean messageInfo) {
                if (messageInfo != null) {
                    binding.chatLayout.sendMessage(messageInfo, false);
                }
            }
        });
    }

    private void askPhotoData(MessageRuleEntity messageRuleEntity) {
        if (messageRuleEntity.getType().intValue() == 1) {
            if (messageRuleEntity.getRuleType() == 1) {//按时间
                Integer ruleValue = messageRuleEntity.getRuleValue();
                if (ruleValue != null && ruleValue.intValue() > 0) {
                    String eventId = mChatInfo.getId() + "_photoAlbum";
                    LocalMessageIMEntity localMessageIMEntity = LocalDataSourceImpl.getInstance().readLocalMessageIM(eventId);
                    if (localMessageIMEntity == null) {
                        viewModel.getPhotoAlbum(getTaUserIdIM());
                    } else {
                        long sendTime = localMessageIMEntity.getSendTime();
                        long localTime = System.currentTimeMillis();
                        if ((localTime / 1000) - (sendTime / 1000) > ruleValue.intValue()) {//满足发送时间
                            //LocalDataSourceImpl.getInstance().removeLocalMessage(eventId);
                            //removeLocalMessage(localMessageIMEntity,eventId);
                            //插入相册
                            viewModel.getPhotoAlbum(getTaUserIdIM());
                        }
                    }
                }
            }
        }
    }

    private void showMoreMenu(int userId) {

        View view = getLayoutInflater().inflate(R.layout.pop_chat_more_menu, null);
        PopupWindow mPop = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //常联系
        TextView tvContact = view.findViewById(R.id.tv_contact);
        if(viewModel.isContactsEnabled.get()){
            Drawable drawable = getResources().getDrawable(R.drawable.img_contact_checked);
            if(drawable != null){
                drawable.setBounds(0,0,dp2px(mActivity,24f),dp2px(getContext(),24f));
                tvContact.setCompoundDrawables(drawable,null,null,null );
            }
        }else{
            Drawable drawable = getResources().getDrawable(R.drawable.img_contact_normal);
            if(drawable != null){
                drawable.setBounds(0,0,dp2px(mActivity,24f),dp2px(getContext(),24f));
                tvContact.setCompoundDrawablesRelative(drawable,null,null,null );
            }

        }
        tvContact.setOnClickListener(v->{
            String otherImUserId = mChatInfo ==null ? null : mChatInfo.getId();
            viewModel.friendAddFrequent(viewModel.isContactsEnabled.get(),otherImUserId,getTaUserIdIM());
            mPop.dismiss();
        });
        TextView menuTrack = view.findViewById(R.id.tv_menu_track);
        menuTrack.setText(viewModel.menuTrack.get());
        menuTrack.setOnClickListener(v -> {
            if (viewModel.isTrack.get()) {
                viewModel.delLike(userId);
            } else {
                viewModel.addLike(userId,"");
            }
            mPop.dismiss();
        });
        TextView menuBlockade = view.findViewById(R.id.tv_menu_blockade);
        menuBlockade.setText(viewModel.menuBlockade.get());
        menuBlockade.setOnClickListener(v -> {

            if (viewModel.inBlacklist.get()) {
                viewModel.delBlackList(userId);
            } else {
                MVDialog.getInstance(mActivity)
                        .setContent(getString(R.string.playfun_dialog_add_blacklist_content))
                        .setConfirmText(getString(R.string.playfun_dialog_add_black_list_btn))
                        .setConfirmOnlick(dialog -> {
                            viewModel.addBlackList(userId);
                        })
                        .chooseType(MVDialog.TypeEnum.CENTERWARNED)
                        .show();
            }
            mPop.dismiss();
        });
        view.findViewById(R.id.tv_menu_report).setOnClickListener(v -> {
            Bundle bundle = ReportUserFragment.getStartBundle("home", userId);
            ReportUserFragment reportUserFragment = new ReportUserFragment();
            reportUserFragment.setArguments(bundle);
            start(reportUserFragment);
            mPop.dismiss();
        });

        mPop.setOutsideTouchable(false);
        mPop.setFocusable(true);
        mPop.setElevation(50);
        mPop.getContentView().measure(0, 0);
        int popWidth = mPop.getContentView().getMeasuredWidth();
        mPop.showAsDropDown(binding.ivSetting,-popWidth + binding.ivSetting.getWidth(),0);
    }

    private void initChatView() {
        //CustomChatInputFragment.isAdmin = mChatInfo.getId() != null && mChatInfo.getId().equals(AppConfig.CHAT_SERVICE_USER_ID);
        //初始化
        setTitleBarTitle(mChatInfo.getChatName());
        binding.chatLayout.initDefault();
        presenter = new C2CChatPresenter();
        presenter.setChatInfo(mChatInfo);
        binding.chatLayout.setPresenter(presenter);
        inputLayout = binding.chatLayout.getInputLayout();
//        inputLayout.enableAudioCall();
        CustomChatInputFragment customChatInputFragment = new CustomChatInputFragment();
        inputLayout.replaceMoreInput(customChatInputFragment);
        //设置客服聊天隐藏

        inputLayout.setSendOnClickCallbacks(this);//添加发送按钮拦截事件
        MessageRecyclerView.is_read_Map = null;
        MessageRecyclerView messageLayout = binding.chatLayout.getMessageLayout();
        MessageRecyclerView.setCertification(ConfigManager.getInstance().isCertification());
        messageLayout.setSex(ConfigManager.getInstance().isMale());//设置性别
        messageLayout.setIsVip(ConfigManager.getInstance().isVip());//设置VIP状态
        String key = viewModel.getLocalUserDataEntity().getId() + "_" + getTaUserIdIM() + "like";
        //存储追踪成功改变样式
        MessageRecyclerView.setAddLikeMsgId(viewModel.readKeyValue(key));
        MessageRecyclerView.setFlagTipMoney(ConfigManager.getInstance().getTipMoneyShowFlag());
        if (mChatInfo.getId() != null && mChatInfo.getId().equals(AppConfig.CHAT_SERVICE_USER_ID)) {
            messageLayout.setIsVip(true);
            messageLayout.setSend_num(-1);
            messageLayout.setRead_sum(-1);
        }
        // 设置自己聊天气泡的背景
        messageLayout.setRightBubble(mActivity.getResources().getDrawable(R.drawable.custom_right_gift_backdrop));
        // 设置朋友聊天气泡的背景
        messageLayout.setLeftBubble(mActivity.getResources().getDrawable(R.drawable.custom_left_gift_backdrop));
        // 设置聊天内容字体大小，朋友和自己用一种字体大小
        messageLayout.setChatContextFontSize(14);
        // 设置自己聊天内容字体颜色
        messageLayout.setRightChatContentFontColor(0xFFFFFFFF);
        // 设置朋友聊天内容字体颜色
        messageLayout.setLeftChatContentFontColor(0xFF666666);
        // 设置聊天时间线的背景
        messageLayout.setChatTimeBubble(new ColorDrawable(0x00000000));
        // 设置聊天时间的字体大小
        messageLayout.setChatTimeFontSize(11);
        // 设置聊天时间的字体颜色
        messageLayout.setChatTimeFontColor(0xFF999999);
        // 设置提示的背景
        messageLayout.setTipsMessageBubble(new ColorDrawable(0x00000000));
        // 设置提示的字体大小
        messageLayout.setTipsMessageFontSize(11);
        // 设置提示的字体颜色
        messageLayout.setTipsMessageFontColor(0xFF999999);
        // 设置默认头像，默认与朋友与自己的头像相同
        messageLayout.setAvatar(R.drawable.default_avatar);
        //设置自己的头像
        messageLayout.setOwnAvatar(ConfigManager.getInstance().getAvatar());
        // 设置头像圆角，不设置则默认不做圆角处理
        messageLayout.setAvatarRadius(50);
        // 设置头像大小
        messageLayout.setAvatarSize(new int[]{48, 48});
        // 从 ChatLayout 里获取 NoticeLayout
        //NoticeLayout noticeLayout = binding.chatLayout.getNoticeLayout();
        // 可以使通知区域一致展示
        //noticeLayout.alwaysShow(true);
        // 设置通知主题
        // noticeLayout.getContent().setText("當前聊天對象可能是機器人！");
        // 设置通知提醒文字
        //noticeLayout.getContentExtra().setText("参看有奖");

        messageLayout.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onMessageLongClick(View view, int position, TUIMessageBean messageInfo) {
                //因为adapter中第一条为加载条目，位置需减1
                messageLayout.showItemPopMenu(position - 1, messageInfo, view);
            }

            @Override
            public void onUserIconClick(View view, int position, TUIMessageBean messageInfo) {

                if (null == messageInfo) {
                    return;
                }
                String id = messageInfo.getV2TIMMessage().getSender();
                if(id==null){
                    return;
                }
                //客服不允许进入主页
                if (id.trim().contains(AppConfig.CHAT_SERVICE_USER_ID)) {
                    return;
                }
                //不能点击自己的用户头像进入主页
                if (id.trim().equals(getUserIdIM())) {
                    return;
                }
                viewModel.transUserIM(id);
            }

            @Override
            public void onUserIconLongClick(View view, int position, TUIMessageBean messageInfo) {

            }

            @Override
            public void onReEditRevokeMessage(View view, int position, TUIMessageBean messageInfo) {

            }

            @Override
            public void onRecallClick(View view, int position, TUIMessageBean messageInfo) {

            }

            @Override
            public void onToastVipText(TUIMessageBean messageInfo) {
                String text = String.valueOf(messageInfo.getExtra());
                if (Utils.isJSON2(text)) {
                    Map<String, Object> map_data = new Gson().fromJson(text, Map.class);
                    if (map_data != null && map_data.get("type") != null && map_data.get("type").equals("toast_local")) {
                        if (map_data.get("status") != null && map_data.get("status").equals("3")) {//发送真人认证提示 :已经发送过
                            CertificationFemaleFragment certificationFemaleFragment = new CertificationFemaleFragment();
                            start(certificationFemaleFragment);
                            return;
                        } else if (map_data.get("status") != null && map_data.get("status").equals("2")) {
                            //添加社区账号、前往聊天对象个人主页
                            if (null == messageInfo) {
                                return;
                            }
                            int userId = getTaUserIdIM(); //获取当前聊天对象的ID
                            if (userId == ConfigManager.getInstance().getAppRepository().readUserData().getId()) {
                                return;
                            }
                            Bundle bundle = UserDetailFragment.getStartBundle(userId);
                            viewModel.start(UserDetailFragment.class.getCanonicalName(), bundle);
                        }
                    }
                }
            }

            @Override
            public void onTextReadUnlock(TextView textView, View view, TUIMessageBean messageInfo) {
                AppContext.instance().logEvent(AppsFlyerEvent.IM_Unlock);
            }

            @Override
            public void onTextTOWebView(TUIMessageBean messageInfo) {
                try {
                    String extra = messageInfo.getExtra().toString();
                    if (extra != null && extra.indexOf("href") != -1 && extra.indexOf("</a>") != -1) {
                        String str = ApiUitl.getRegHref(extra);
                        if (str != null) {
                            Uri uri = Uri.parse(str);
                            Intent web = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(web);
                            //viewModel.start(WebDetailFragment.class.getCanonicalName(), WebDetailFragment.getStartBundle(str));
                        }

                    }
                } catch (Exception e) {

                }
            }

            //去往用户主页
            @Override
            public void toUserHome() {
                int userId = getTaUserIdIM(); //获取当前聊天对象的ID
                if (userId == ConfigManager.getInstance().getAppRepository().readUserData().getId()) {
                    return;
                }
                AppContext.instance().logEvent(AppsFlyerEvent.Pchat_photo);
                Bundle bundle = UserDetailFragment.getStartBundle(userId);
                viewModel.start(UserDetailFragment.class.getCanonicalName(), bundle);
            }

            @Override
            public void openUserImage(com.tencent.coustom.PhotoAlbumItemEntity itemEntity) {
                if (itemEntity != null) {
                    PictureSelectorUtil.previewImage(mActivity, StringUtil.getFullImageWatermarkUrl(itemEntity.getSrc()));
                    //AppContext.instance().logEvent(AppsFlyerEvent.Pchat_photo);
                }
            }

            //评价
            @Override
            public void onClickEvaluate(int position, TUIMessageBean messageInfo, com.tencent.coustom.EvaluateItemEntity evaluateItemEntity, boolean more) {
                AppContext.instance().logEvent(AppsFlyerEvent.Pchat_Evaluation);
                try {
                    if (more) {//更多
                        long dayTime = System.currentTimeMillis();
                        if (intervalTime != null && (dayTime / 1000) - intervalTime.longValue() <= 2) {
                            return;
                        }
                        intervalTime = dayTime / 1000;
                        viewModel.getUserEvaluate(getTaUserIdIM(), false);
                    } else {
                        viewModel.commitUserEvaluate(getTaUserIdIM(), evaluateItemEntity.getTagId(), null);
                        //messageInfo.remove();
                        //viewModel.commitUserEvaluate(getTaUserIdIM(),evaluateItemEntity.getTagId());
                        //ToastUtils.showShort("你选择了 "+evaluateItemEntity.getName());
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onClickCustomText(int position, TUIMessageBean messageInfo, CustomIMTextEntity customIMTextEntity) {
                if (customIMTextEntity != null) {
                    if (customIMTextEntity.getEvent() == 1) {//上传照片
                        AppContext.instance().logEvent(AppsFlyerEvent.im_tips_photo);
                        viewModel.start(MyPhotoAlbumFragment.class.getCanonicalName());
                    } else if (customIMTextEntity.getEvent() == 2) {//送礼物
                        AppContext.instance().logEvent(AppsFlyerEvent.im_tips_gifts);
                        giftBagDialogShow();
                    } else if (customIMTextEntity.getEvent() == 3) {//追踪
                        AppContext.instance().logEvent(AppsFlyerEvent.im_tips_follow);
                        viewModel.addLike(getTaUserIdIM(), messageInfo.getId());
                    } else if (customIMTextEntity.getEvent() == 4) {//唤醒语音视频聊天
                        AppContext.instance().logEvent(AppsFlyerEvent.im_tips_vv);
                        DialogCallPlayUser();
                    } else if (customIMTextEntity.getEvent() == 11) {//真人认证
                        AppContext.instance().logEvent(AppsFlyerEvent.im_tips_auth);
                        if (ConfigManager.getInstance().isMale()) {
                            viewModel.start(CertificationMaleFragment.class.getCanonicalName());
                        } else {
                            viewModel.start(CertificationFemaleFragment.class.getCanonicalName());
                        }
                    }
                }
            }

            @Override
            public void onClickDialogRechargeShow() {
                paySelectionboxChoose(false);
            }

            @Override
            public void clickToUserMain() {
                int userId = getTaUserIdIM(); //获取当前聊天对象的ID
                if (userId == ConfigManager.getInstance().getAppRepository().readUserData().getId()) {
                    return;
                }
                Bundle bundle = UserDetailFragment.getStartBundle(userId);
                viewModel.start(UserDetailFragment.class.getCanonicalName(), bundle);
            }

            @Override
            public void onClickCustomText() {
                AppContext.instance().logEvent(AppsFlyerEvent.im_tips_auth);
                if (ConfigManager.getInstance().isMale()) {
                    viewModel.start(CertificationMaleFragment.class.getCanonicalName());
                } else {
                    viewModel.start(CertificationFemaleFragment.class.getCanonicalName());
                }
            }

            @Override
            public void onImageClick(TUIMessageBean messageInfo) {
                CustomImageMessageBean customImageMessageBean = (CustomImageMessageBean) messageInfo;
                if (customImageMessageBean != null && customImageMessageBean.getImgPath() != null) {
                    MessageDetailDialog.getImageDialog(mActivity, customImageMessageBean.getImgPath()).show();
                }
            }
        });

        viewModel.uc.loadMessage.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    messageLayout.setIsVip(true);//设置VIP状态
                    messageLayout.setRead_sum(-1);
                    messageLayout.setSend_num(-1);
                    MessageRecyclerView messageRecyclerView = binding.chatLayout.getMessageLayout();
                    //binding.chatLayout.loadMessages();
                    messageRecyclerView.getAdapter().notifyDataSetChanged();
                    //viewModel.loadUserInfo();
                    ConfigManager.getInstance().DesInstance();
                    ConfigManager.DesInstance();
                }
            }
        });
        viewModel.uc.addLikeSuccess.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String msgId) {
                String key = viewModel.getLocalUserDataEntity().getId() + "_" + getTaUserIdIM() + "like";
                //存储追踪成功改变样式
                viewModel.putKeyValue(key, msgId);
                MessageRecyclerView.setAddLikeMsgId(msgId);
                List<TUIMessageBean> listDataSource = messageLayout.getAdapter().getDataSource();
                for (int i = 0; i < listDataSource.size(); i++) {
                    if (listDataSource.get(i).getId().equals(msgId)) {
                        messageLayout.getAdapter().notifyItemChanged(i);
                        messageLayout.getAdapter().notifyItemChanged(i + 1);
                        break;
                    }
                }
            }
        });
        if (!StringUtil.isEmpty(toSendMessageText)) {
            Map<String, Object> custom_local_data = new HashMap<>();
            custom_local_data.put("type", "message_tag");
            custom_local_data.put("text", toSendMessageText);
            String str = GsonUtils.toJson(custom_local_data);
            inputLayout.getMessageHandler().sendMessage(ChatMessageBuilder.buildTextMessage(str));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AudioPlayer.getInstance().stopPlay();
    }

    @Override
    public void onPictureActionClick() {
        PictureSelectorUtil.selectImage(mActivity, true, 1, new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(List<LocalMedia> result) {
                viewModel.uploadFileOSS(result.get(0));
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @Override
    public void onVideoActionClick() {
        PictureSelectorUtil.selectVideo(mActivity, true, 1, new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(List<LocalMedia> result) {
                LocalMedia localMedia = result.get(0);

                String path = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    path = localMedia.getAndroidQToPath();
                    if (path == null || path.isEmpty()) {
                        path = localMedia.getRealPath();
                    }
                } else {
                    path = localMedia.getRealPath();
                }

                if (StringUtil.isEmpty(path)) {
                    path = localMedia.getPath();
                }

                String thumbPath = ImageUtils.getVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND, 0, 0);
                TUIMessageBean info = ChatMessageBuilder.buildVideoMessage(thumbPath, path, localMedia.getWidth(), localMedia.getHeight(), localMedia.getDuration());
                binding.chatLayout.sendMessage(info, false);
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @Override
    public void onBurnActionClick() {
        PictureSelectorUtil.selectImage(mActivity, true, 1, new OnResultCallbackListener<LocalMedia>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResult(List<LocalMedia> result) {
                PhotoReviewFragment photoReviewFragment = new PhotoReviewFragment();
                photoReviewFragment.setArguments(PhotoReviewFragment.getStartBundle(result.get(0).getCompressPath()));
                startForResult(photoReviewFragment, 2001);
            }

            @Override
            public void onCancel() {
            }
        });

    }

    @Override
    public void onRedPackageActionClick() {
        System.out.println();
    }

    @Override
    public void onCoinRedPackageActionClick() {
        if (mChatInfo == null) {
            return;
        }
        if (mChatInfo.getId() != null && mChatInfo.getId().equals(AppConfig.CHAT_SERVICE_USER_ID)) {
            return;
        }
        Bundle bundle = SendCoinRedPackageFragment.getStartBundle(getTaUserIdIM());
        SendCoinRedPackageFragment sendCoinRedPackageFragment = new SendCoinRedPackageFragment();
        sendCoinRedPackageFragment.setArguments(bundle);
        startForResult(sendCoinRedPackageFragment, 2002);
    }

    @Override
    public void onMicActionClick() {
        if (mChatInfo != null) {
            try {
                viewModel.checkConnMic(getTaUserIdIM());
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showShort("error");
            }
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        if (resultCode != ISupportFragment.RESULT_OK) {
            return;
        }
        if (requestCode == 2002) {
            int id = data.getInt(SendCoinRedPackageFragment.ARG_RED_PACKAGE_ID);
            String desc = data.getString(SendCoinRedPackageFragment.ARG_DESC);
            int number = data.getInt(SendCoinRedPackageFragment.ARG_NUMBER, 0);
            CustomMessageData customMessageData = CustomMessageData.genCoinRedPackageMessage(id, number, desc);
            TUIMessageBean info = ChatMessageBuilder.buildCustomMessage(GsonUtils.toJson(customMessageData), null, null);
            binding.chatLayout.sendMessage(info, false);
        } else if (requestCode == 1003) {

        } else if (requestCode == 2001) {
            String imageSrcKey = data.getString(PhotoReviewFragment.ARG_IMAGE_SRC_KEY);
            CustomMessageData customMessageData = CustomMessageData.genBurnMessage(imageSrcKey);
            TUIMessageBean info = ChatMessageBuilder.buildCustomMessage(GsonUtils.toJson(customMessageData), null, null);
            binding.chatLayout.sendMessage(info, false);
        }
    }

    @Override
    public void onDestroy() {
        if (binding.chatLayout != null) {
            binding.chatLayout.exitChat();
        }
        binding.chatLayout.getMessageLayout().setIsReadMap();//清除本次会话可看信息
        if (giftView != null) {
            if (giftView.isAnimating()) {
                giftView.stopAnimation();
            }
            viewModel.animGiftPlaying = true;
            viewModel.animGiftList.clear();
            giftView = null;
        }
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onDestroy();
    }

    /**
     * 发送语音消息拦截
     *
     * @param messageHandler
     * @param messageInfo
     */
    @Override
    public void sendOnClickAudioMessage(InputView.MessageHandler messageHandler, TUIMessageBean messageInfo) {
        if (viewModel.priceConfigEntityField == null) {
            return;
        }
        if (messageHandler != null) {
            messageHandler.sendMessage(messageInfo);
        }
    }

    //支付框样式选择
    private void paySelectionboxChoose(boolean b) {
        if (ConfigManager.getInstance().isMale()) {
            if (ConfigManager.getInstance().isVip()) {
                googleCoinValueBox(b);
            } else {
                dialogRechargeShow(b);
            }
        } else {
            googleCoinValueBox(b);
        }
    }

    @Override
    public void onClickPhoneVideo() {//点击选中图片、视频
        MessageDetailDialog.CheckImgViewFile(mActivity, true, new MessageDetailDialog.AudioCallHintOnClickListener() {
            @Override
            public void check1OnClick() {
                if (Status.mIsShowFloatWindow){
                    me.goldze.mvvmhabit.utils.ToastUtils.showShort(R.string.audio_in_call);
                    return;
                }
                //选择视频
                onVideoActionClick();
            }

            @Override
            public void check2OnClick() {
                //选择图片
                onPictureActionClick();
            }
        }).show();
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("*/*");
//        String[] mimetypes = {"image/*", "video/*"};
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//
//        startActivityForResult(intent, InputMoreFragment.REQUEST_CODE_PHOTO);
    }

    @Override
    public void onClickGift() {//点击调用礼物
        if (viewModel.tagEntitys.get() != null) {
            if (viewModel.tagEntitys.get().getBlacklistStatus() == 1 || viewModel.tagEntitys.get().getBlacklistStatus() == 3) {
                Toast.makeText(mActivity, R.string.playfun_chat_detail_pull_black_other2, Toast.LENGTH_SHORT).show();
                return;
            } else if (viewModel.tagEntitys.get().getBlacklistStatus() == 2) {
                Toast.makeText(mActivity, R.string.playfun_chat_detail_blocked2, Toast.LENGTH_SHORT).show();
                return;
            }
            AppContext.instance().logEvent(AppsFlyerEvent.im_gifts);
            giftBagDialogShow();
        }
    }

    @Override
    public void sendBlackStatus(int status) {
        try {
            if (!ObjectUtils.isEmpty(viewModel) && viewModel.tagEntitys.get() != null) {
                viewModel.tagEntitys.get().setBlacklistStatus(status);
            }
        } catch (Exception e) {
            LogUtils.i("sendBlackStatus: ");
        }
    }

    @Override
    public void onChangedFaceLayout(boolean flag, int height, int faceHeight) {
        try {
            //非客服账号加载用户标签和状态
            if (!mChatInfo.getId().startsWith(AppConfig.CHAT_SERVICE_USER_ID)) {
                if (flag) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.rlLayout.getLayoutParams();
                    if (defBottomMargin == 0) {
                        defBottomMargin = layoutParams.bottomMargin;
                    }
                    int ctHeight = 0;
                    if (height == 0 || faceHeight == 0) {
                        ctHeight = faceHeight == 0 ? 1084 : faceHeight;
                    }
                    layoutParams.bottomMargin = height + ctHeight + defBottomMarginHeight;
                    binding.rlLayout.setLayoutParams(layoutParams);
                } else {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.rlLayout.getLayoutParams();
                    if (defBottomMargin == 0) {
                        defBottomMargin = layoutParams.bottomMargin;
                    }
                    layoutParams.bottomMargin = defBottomMargin;
                    binding.rlLayout.setLayoutParams(layoutParams);
                }
            }
        } catch (Exception e) {

        }
    }

    public void giftBagDialogShow() {
        giftBagDialog = new GiftBagDialog(getContext(), false, viewModel.maleBalance, 0);
        giftBagDialog.setGiftOnClickListener(new GiftBagDialog.GiftOnClickListener() {
            @Override
            public void sendGiftClick(Dialog dialog, int number, GiftBagEntity.giftEntity giftEntity) {
                dialog.dismiss();
                AppContext.instance().logEvent(AppsFlyerEvent.im_send_gifts);
                viewModel.sendUserGift(dialog, giftEntity, getTaUserIdIM(), number);
            }

            @Override
            public void rechargeStored(Dialog dialog) {
                AppContext.instance().logEvent(AppsFlyerEvent.im_gifts_topup);
                dialog.dismiss();
//                dialogRechargeShow(false);
                paySelectionboxChoose(false);
            }
        });
        giftBagDialog.show();
    }

    @Override
    public void onClickCallPlayUser() {//点击调用拨打通话
        if (Status.mIsShowFloatWindow){
            me.goldze.mvvmhabit.utils.ToastUtils.showShort(com.tencent.qcloud.tuikit.tuichat.R.string.audio_in_call);
            return;
        }
        if (viewModel.tagEntitys.get() != null) {
            if (viewModel.tagEntitys.get().getBlacklistStatus() == 1 || viewModel.tagEntitys.get().getBlacklistStatus() == 3) {
                Toast.makeText(mActivity, R.string.playfun_chat_detail_pull_black_other, Toast.LENGTH_SHORT).show();
                return;
            } else if (viewModel.tagEntitys.get().getBlacklistStatus() == 2) {
                Toast.makeText(mActivity, R.string.playfun_chat_detail_blocked, Toast.LENGTH_SHORT).show();
                return;
            }
            DialogCallPlayUser();
        }

    }

    //调起拨打音视频通话
    private void DialogCallPlayUser() {

        if (viewModel.priceConfigEntityField != null) {
            AudioProfitTips = viewModel.priceConfigEntityField.getCurrent().getAudioProfitTips();
            VideoProfitTips = viewModel.priceConfigEntityField.getCurrent().getVideoProfitTips();
        }
        //收益开关绝对是否展示通话金额
        if (!ConfigManager.getInstance().getTipMoneyShowFlag()) {
            AudioProfitTips = null;
            VideoProfitTips = null;
        }

        MessageDetailDialog.AudioAndVideoCallDialog(mActivity,
                true,
                AudioProfitTips,
                VideoProfitTips,
                new MessageDetailDialog.AudioAndVideoCallOnClickListener() {
                    @Override
                    public void audioOnClick() {
                        new RxPermissions(mActivity)
                                .request(Manifest.permission.RECORD_AUDIO)
                                .subscribe(granted -> {
                                    if (granted) {
                                        AppContext.instance().logEvent(AppsFlyerEvent.im_voice_call);
                                        viewModel.getCallingInvitedInfo(1, getUserIdIM(), mChatInfo.getId());
                                    } else {
                                        TraceDialog.getInstance(mActivity)
                                                .setCannelOnclick(new TraceDialog.CannelOnclick() {
                                                    @Override
                                                    public void cannel(Dialog dialog) {

                                                    }
                                                })
                                                .setConfirmOnlick(new TraceDialog.ConfirmOnclick() {
                                                    @Override
                                                    public void confirm(Dialog dialog) {
                                                        new RxPermissions(mActivity)
                                                                .request(Manifest.permission.RECORD_AUDIO)
                                                                .subscribe(granted -> {
                                                                    if (granted) {
                                                                        AppContext.instance().logEvent(AppsFlyerEvent.im_voice_call);
                                                                        viewModel.getCallingInvitedInfo(1, getUserIdIM(), mChatInfo.getId());
                                                                    }
                                                                });
                                                    }
                                                }).AlertCallAudioPermissions().show();
                                    }
                                });
                    }

                    @Override
                    public void videoOnClick() {
                        new RxPermissions(mActivity)
                                .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                                .subscribe(granted -> {
                                    if (granted) {
                                        AppContext.instance().logEvent(AppsFlyerEvent.im_video_call);
                                        viewModel.getCallingInvitedInfo(2, getUserIdIM(), mChatInfo.getId());
                                    } else {
                                        TraceDialog.getInstance(mActivity)
                                                .setCannelOnclick(new TraceDialog.CannelOnclick() {
                                                    @Override
                                                    public void cannel(Dialog dialog) {

                                                    }
                                                })
                                                .setConfirmOnlick(new TraceDialog.ConfirmOnclick() {
                                                    @Override
                                                    public void confirm(Dialog dialog) {
                                                        new RxPermissions(mActivity)
                                                                .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                                                                .subscribe(granted -> {
                                                                    if (granted) {
                                                                        AppContext.instance().logEvent(AppsFlyerEvent.im_video_call);
                                                                        viewModel.getCallingInvitedInfo(2, getUserIdIM(), mChatInfo.getId());
                                                                    }
                                                                });
                                                    }
                                                })
                                                .AlertCallAudioPermissions().show();
                                    }
                                });
                    }
                }).show();
    }

    //弹出钻石充值
    private void dialogRechargeShow(boolean isGiftSend) {
        if (!isGiftSend) {
            AppContext.instance().logEvent(AppsFlyerEvent.im_topup);
        }
        ApiConfigManagerEntity apiConfigManagerEntity = ConfigManager.getInstance().getAppRepository().readApiConfigManagerEntity();
        if(apiConfigManagerEntity!=null && apiConfigManagerEntity.getPlayFunWebUrl()!=null){
            String url = apiConfigManagerEntity.getPlayFunWebUrl() + AppConfig.PAY_RECHARGE_URL;
            new WebViewDialog(getContext(), mActivity, url, new WebViewDialog.ConfirmOnclick() {
                @Override
                public void webToVipRechargeVC(Dialog dialog) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    viewModel.start(VipSubscribeFragment.class.getCanonicalName());
                }

                @Override
                public void vipRechargeDiamondSuccess(Dialog dialog, Integer coinValue) {
                    if (dialog != null) {
                        this.cancel();
                        dialog.dismiss();
                    }
                }

                @Override
                public void moreRechargeDiamond(Dialog dialog) {
                    dialog.dismiss();
                    if(mActivity!=null && !mActivity.isFinishing()){
                        mActivity.runOnUiThread(() -> googleCoinValueBox(false));
                    }
                }

                @Override
                public void cancel() {
                }
            }).noticeDialog().show();
        }else{
            googleCoinValueBox(false);
        }
    }

    private void googleCoinValueBox(boolean isGiftSend) {
        if (!isGiftSend) {
            AppContext.instance().logEvent(AppsFlyerEvent.im_topup);
        }
        AppContext.instance().logEvent(AppsFlyerEvent.Top_up);
        CoinRechargeSheetView coinRechargeFragmentView = new CoinRechargeSheetView(mActivity);
        coinRechargeFragmentView.setClickListener(new CoinRechargeSheetView.ClickListener() {
            @Override
            public void toGooglePlayView(GoodsEntity goodsEntity) {
                Intent intent = new Intent(mActivity, RechargeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Goods_info", goodsEntity);
                intent.putExtras(bundle);
                toGooglePlayIntent.launch(intent);
            }
        });
        coinRechargeFragmentView.show();
    }

    //跳转谷歌支付act
    ActivityResultLauncher<Intent> toGooglePlayIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Log.e("进入支付页面回调","=========");
        if (result.getData() != null) {
            Intent intentData = result.getData();
            GoodsEntity goodsEntity = (GoodsEntity) intentData.getSerializableExtra("goodsEntity");
            if(goodsEntity!=null){
                Log.e("支付成功","===============");
            }
        }
    });

    @Override
    public void sendOnClickCallbackOk(InputView.MessageHandler messageHandler, TUIMessageBean messageInfo) {
        if (messageHandler != null) {
            UserDataEntity userDataEntity = viewModel.getLocalUserDataEntity();
            if (userDataEntity == null) {
                return;
            }
            if (mChatInfo.getId().equals(AppConfig.CHAT_SERVICE_USER_ID)) { //客服放行
                messageHandler.sendMessage(messageInfo);
                return;
            }
            if (TUIChatUtils.isLineNumber(messageInfo.getExtra().toString())
                    || TUIChatUtils.isContains(messageInfo.getExtra().toString(), viewModel.sensitiveWords.get())) {
                //包含台湾电话号码或者包含屏蔽关键字
                sendLocalMessage(messageInfo.getExtra().toString(), "send_violation_message", null);
                return;
            }
            messageHandler.sendMessage(messageInfo);

        }
    }

    /**
     * 发送自定义消息。只能自己看
     */
    public void sendLocalMessage(String value, String type, Object status) {
        Map<String, Object> custom_local_data = new HashMap<>();
        custom_local_data.put("type", type);
        custom_local_data.put("status", status);
        custom_local_data.put("text", value);
        String str = GsonUtils.toJson(custom_local_data);
        //发送本地消息，并且自定它
        sendLocalMessage(str);
    }

    /**
     * 发送自定义消息。只能自己看
     */
    public void sendLocalMessage(String value) {
        //发送本地消息，并且自定它
        ChatMessageBuilder.C2CMessageToLocal(value, mChatInfo.getId(), new V2TIMValueCallback() {
            @Override
            public void onError(int code, String desc) {
            }

            @Override
            public void onSuccess(Object o) {
                V2TIMMessage v2TIMMessage = (V2TIMMessage) o;
                String msgId = v2TIMMessage.getMsgID();
                List<String> list = new ArrayList<>();
                list.add(msgId);
                V2TIMManager.getMessageManager().findMessages(list, new V2TIMValueCallback() {
                    @Override
                    public void onError(int code, String desc) {
                    }

                    @Override
                    public void onSuccess(Object o) {
                        ArrayList<V2TIMMessage> messages = (ArrayList) o;
                        TUIMessageBean msgInfos = ChatMessageBuilder.buildMessage(messages.get(0));
                        ChatPresenter chatPresenter = binding.chatLayout.getChatPresenter();
                        boolean bl = chatPresenter.addMessageList(msgInfos, false);
                    }
                });
            }
        });
    }

    //发送本地消息记录 照片、评价
    public void addLocalMessage(String type, final String EventId, String objData) {
        Map<String, Object> custom_local_data = new HashMap<>();
        custom_local_data.put("type", type);
        custom_local_data.put("data", objData);
        String str = GsonUtils.toJson(custom_local_data);
        LocalMessageIMEntity localMessageIMEntity = LocalDataSourceImpl.getInstance().readLocalMessageIM(EventId);
        MessageRecyclerView messageRecyclerView = binding.chatLayout.getMessageLayout();
        if (messageRecyclerView != null) {
            if (ObjectUtils.isEmpty(localMessageIMEntity)) {//没有历史消息
//            Log.e("聊天规则配置插入相册","没有历史消息");
                //发送本地消息，并且自定它
                ChatMessageBuilder.C2CMessageToLocal(str, mChatInfo.getId(), new V2TIMValueCallback() {
                    @Override
                    public void onError(int code, String desc) {
                    }

                    @Override
                    public void onSuccess(Object o) {
                        V2TIMMessage v2TIMMessage = (V2TIMMessage) o;
                        String msgId = v2TIMMessage.getMsgID();
                        List<String> list = new ArrayList<>();
                        list.add(msgId);
                        V2TIMManager.getMessageManager().findMessages(list, new V2TIMValueCallback() {
                            @Override
                            public void onError(int code, String desc) {
                            }

                            @Override
                            public void onSuccess(Object o) {
                                ArrayList<V2TIMMessage> messages = (ArrayList) o;
                                TUIMessageBean msgInfos = ChatMessageBuilder.buildMessage(messages.get(0));
                                if (type.equals("message_photo")) {
                                    binding.chatLayout.getChatPresenter().addMessageInfo(msgInfos);
                                } else {
                                    boolean bl = binding.chatLayout.getChatPresenter().addMessageList(msgInfos, true);
                                }
                                //添加本地记录
                                LocalDataSourceImpl.getInstance().putLocalMessageIM(EventId, msgInfos.getId(), System.currentTimeMillis());
                                // binding.chatLayout.setDataProvider(iChatProvider);
                            }
                        });
                    }
                });
            } else {
//            Log.e("聊天规则配置插入相册","有历史消息");
                LocalDataSourceImpl.getInstance().removeLocalMessage(EventId);
                removeLocalMessage(localMessageIMEntity, EventId, false);
                String LocalMsgId = localMessageIMEntity.getMsgId();
                List<TUIMessageBean> listMessage = messageRecyclerView.getAdapter().getDataSource();
                boolean flag = false;
                String msgIds = null;
                Integer toUserId = getTaUserIdIM();
                if (LocalMsgId.indexOf(getTaUserIdIM()) != -1) {
                    flag = true;
                    msgIds = LocalMsgId.replace(toUserId + "-", "");
                }
                for (int i = 0; i < listMessage.size(); i++) {
                    if (flag && (listMessage.get(i).getId().indexOf(msgIds) != -1 || listMessage.get(i).getV2TIMMessage().getMsgID().indexOf(msgIds) != -1)) {
                        messageRecyclerView.getAdapter().getItem(i).setExtra(objData);
                        messageRecyclerView.getAdapter().getItem(i).notify();
                        //iChatProvider.getAdapter().getItem(i).notify();
                    }
                }
                //发送本地消息，并且自定它
                ChatMessageBuilder.C2CMessageToLocal(str, mChatInfo.getId(), new V2TIMValueCallback() {
                    @Override
                    public void onError(int code, String desc) {
                    }

                    @Override
                    public void onSuccess(Object o) {
                        V2TIMMessage v2TIMMessage = (V2TIMMessage) o;
                        String msgId = v2TIMMessage.getMsgID();
                        LocalDataSourceImpl.getInstance().putLocalMessageIM(EventId, msgId, System.currentTimeMillis());
                    }
                });
            }
        }
    }

    //获取聊天对象的UserId
    public Integer getTaUserIdIM() {
        return toUserDataId == null ? 0 : toUserDataId;
    }

    //获取当前用户的IM id
    public String getUserIdIM() {
        return ConfigManager.getInstance().getUserImID();
    }

    public synchronized void removeLocalMessage(LocalMessageIMEntity localMessageIMEntity, String eventId, boolean updateView) {
        List<String> list = new ArrayList<>();
        list.add(localMessageIMEntity.getMsgId());
        V2TIMManager.getMessageManager().findMessages(list, new V2TIMValueCallback() {
            @Override
            public void onError(int code, String desc) {
            }

            @Override
            public void onSuccess(Object o) {
                ArrayList<V2TIMMessage> messages = (ArrayList) o;
                if (messages == null || messages.isEmpty()) {
                    return;
                }
                //binding.chatLayout.getChatManager().removeMessage(localMessageIMEntity.getMsgId(),toUserId);
                if (updateView) {
                    String toUserId = getUserIdIM();
                    binding.chatLayout.getChatPresenter().removeMessage(localMessageIMEntity.getMsgId(), toUserId);
                }
                V2TIMManager.getMessageManager().deleteMessageFromLocalStorage(messages.get(0), new V2TIMCallback() {
                    @Override
                    public void onError(int code, String desc) {
                    }

                    @Override
                    public void onSuccess() {

                        //  binding.chatLayout.getChatManager().deleteMessageInfo(messages.get(0));
                        //LocalDataSourceImpl.getInstance().removeLocalMessage(eventId);
                    }
                });

            }
        });
    }

    /**
     * @return void
     * @Desc TODO(播放SVGA)
     * @author 彭石林
     * @parame [giftEntity]
     * @Date 2022/3/12
     */
    private synchronized void startSVGAnimotion() {
        MessageGiftNewEvent giftEntity = viewModel.animGiftList.get(0);
        String formUserId = giftEntity.getFormUserId();
        LogUtils.i("startSVGAnimotion: "+formUserId);
        if (formUserId == null || (!formUserId.equals(mChatInfo.getId()) && !formUserId.equals(getUserIdIM()))){
            finishSVGA();
            return;
        }
        SVGAParser svgaParser = SVGAParser.Companion.shareParser();
        try {
            svgaParser.decodeFromURL(new URL(StringUtil.getFullAudioUrl(giftEntity.getGiftEntity().getSvgaPath())), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    if (videoItem != null && giftView != null) {
                        giftView.setVisibility(View.VISIBLE);
                        giftView.setVideoItem(videoItem);
                        giftView.setLoops(1);
                        giftView.setElevation(999);
                        giftView.setCallback(new SVGACallback() {
                            @Override
                            public void onPause() {
                            }

                            @Override
                            public void onFinished() {
                                finishSVGA();
                            }

                            @Override
                            public void onRepeat() {
                            }

                            @Override
                            public void onStep(int i, double v) {
                            }
                        });
                        giftView.startAnimation();
                    }
                }

                @Override
                public void onError() {
                    Log.e("播放失败", "===========");
                    finishSVGA();
                }
            }, null);
        } catch (Exception e) {
            Log.e("播放异常", "===========");
            finishSVGA();
        }
    }

    private void finishSVGA() {
        if (viewModel.animGiftList != null && viewModel.animGiftList.size() > 0) {
            viewModel.animGiftList.remove(0);
        }
        //播放完成
        if(giftView!=null){
            giftView.setVisibility(View.GONE);
        }
        viewModel.animGiftPlaying = false;
        viewModel.playSVGAGift();
    }
}
