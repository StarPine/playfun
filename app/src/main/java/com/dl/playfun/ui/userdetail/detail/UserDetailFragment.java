package com.dl.playfun.ui.userdetail.detail;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.dl.playfun.app.AppConfig;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.app.AppViewModelFactory;
import com.dl.playfun.app.AppsFlyerEvent;
import com.dl.playfun.app.Injection;
import com.dl.playfun.databinding.FragmentUserDetailBinding;
import com.dl.playfun.entity.EvaluateEntity;
import com.dl.playfun.entity.EvaluateItemEntity;
import com.dl.playfun.entity.EvaluateObjEntity;
import com.dl.playfun.entity.UserDetailEntity;
import com.dl.playfun.helper.DialogHelper;
import com.dl.playfun.manager.ConfigManager;
import com.dl.playfun.ui.base.BaseToolbarFragment;
import com.dl.playfun.ui.certification.certificationfemale.CertificationFemaleFragment;
import com.dl.playfun.ui.dialog.CommitEvaluateDialog;
import com.dl.playfun.ui.dialog.CopyAccountDialog;
import com.dl.playfun.ui.dialog.MyEvaluateDialog;
import com.dl.playfun.ui.mine.vipsubscribe.VipSubscribeFragment;
import com.dl.playfun.ui.userdetail.playnum.CoinPaySheetUserMain;
import com.dl.playfun.ui.userdetail.report.ReportUserFragment;
import com.dl.playfun.utils.ImmersionBarUtils;
import com.dl.playfun.utils.ListUtils;
import com.dl.playfun.utils.PictureSelectorUtil;
import com.dl.playfun.utils.StringUtil;
import com.dl.playfun.widget.AppBarStateChangeListener;
import com.dl.playfun.widget.bottomsheet.BottomSheet;
import com.dl.playfun.widget.coinpaysheet.CoinPaySheet;
import com.dl.playfun.widget.coinrechargesheet.GameCoinTopupSheetView;
import com.dl.playfun.widget.custom.FlowAdapter;
import com.dl.playfun.widget.dialog.MMAlertDialog;
import com.dl.playfun.widget.dialog.MVDialog;
import com.dl.playfun.widget.emptyview.EmptyState;
import com.google.android.material.appbar.AppBarLayout;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.tencent.qcloud.tuikit.tuichat.component.AudioPlayer;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.ToastUtils;
import me.jessyan.autosize.internal.CustomAdapt;

/**
 * @author wulei 用户详情fragment
 */
@SuppressLint("StringFormatMatches")
public class UserDetailFragment extends BaseToolbarFragment<FragmentUserDetailBinding, UserDetailViewModel> implements View.OnClickListener, CustomAdapt {

    public static final String ARG_USER_DETAIL_USER_ID = "arg_user_detail_user_id";
    public static final String ARG_USER_DETAIL_MORENUMBER = "arg_user_detail_morenumber";

    private int userId;
    private Integer moreNumber;

    private boolean flagShow = false;

    private boolean toolbarUp = false;

    public static Bundle getStartBundle(int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_USER_DETAIL_USER_ID, userId);
        return bundle;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        ImmersionBarUtils.setupStatusBar(this, false, true);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        ImmersionBarUtils.setupStatusBar(this, true, true);
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_user_detail;
    }

    @Override
    public void initParam() {
        super.initParam();
        userId = getArguments().getInt(ARG_USER_DETAIL_USER_ID, 0);
        moreNumber = Integer.getInteger(getArguments().getString(ARG_USER_DETAIL_MORENUMBER));
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public UserDetailViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(mActivity.getApplication());
        UserDetailViewModel userDetailViewModel = ViewModelProviders.of(this, factory).get(UserDetailViewModel.class);
        userDetailViewModel.userId.set(userId);
        userDetailViewModel.stateModel.setEmptyState(EmptyState.NORMAL);
        return userDetailViewModel;
    }

    @Override
    public void initViewObservable() {
//        viewModel.uc.loadEvaluate.observe(this, new Observer<List<EvaluateEntity>>() {
//            @Override
//            public void onChanged(List<EvaluateEntity> evaluateEntities) {
//
//            }
//        });
        //监听下拉刷新完成
        viewModel.uc.finishRefreshing.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                //结束刷新
               binding.refreshLayout.finishRefresh(100);
            }
        });
        viewModel.uc.evaluateItemEntityList.observe(this, new Observer<List<EvaluateItemEntity>>() {
            @Override
            public void onChanged(List<EvaluateItemEntity> evaluateItemEntities) {
                if (!ListUtils.isEmpty(evaluateItemEntities)) {
                    // 设置 Adapter
                    FlowAdapter adapter = new FlowAdapter(UserDetailFragment.this.getContext(), evaluateItemEntities);
                    binding.flowLayout.setAdapter(adapter);
                    // 设置最多显示的行数
                    binding.flowLayout.setMaxLines(4);
                    // 获取显示的 item 数
                    binding.flowLayout.post(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }
        });
        viewModel.uc.lineEvent.observe(this, new Observer<UserDetailEntity>() {
            @Override
            public void onChanged(UserDetailEntity detailEntity) {
                if (StringUtil.isEmpty(detailEntity.getWeixin()) && StringUtil.isEmpty(detailEntity.getInsgram())) {
                    binding.accountLine.setText(StringUtils.getString(R.string.playfun_no_input));
                    return;
                }
                if (detailEntity.getIsUnlockAccount() != 1) {
                    binding.accountLine.setText(StringUtils.getString(R.string.playfun_click_check_social_account));
                    return;
                }

                if (detailEntity.getIsWeixinShow() == 1 || detailEntity.getIsUnlockAccount() == 1) {
                    if (!StringUtils.isEmpty(detailEntity.getWeixin())) {
                        binding.accountLine.setText("Line: " + detailEntity.getWeixin());
                    } else if (!StringUtils.isEmpty(detailEntity.getInsgram()) && detailEntity.getIsUnlockAccount() == 1) {
                        binding.accountLine.setText("Instagram: " + detailEntity.getInsgram());
                    } else {
                        binding.accountLine.setText(StringUtils.getString(R.string.playfun_no_input));
                    }
                } else {
                    binding.accountLine.setText(StringUtils.getString(R.string.playfun_click_check_social_account));
                }
            }
        });
        viewModel.uc.clickMore.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                if (viewModel.detailEntity.get() == null) {
                    return;
                }
//                String[] items = new String[]{getString(R.string.pull_black_shield_both_sides), getString(R.string.remark_user_title), getString(R.string.report_user_title)};
                String[] items = new String[]{getString(R.string.playfun_pull_black_shield_both_sides), getString(R.string.playfun_report_user_title)};
                if(viewModel.detailEntity.get().getCollect()){
                    items = new String[]{getString(R.string.playfun_pull_black_shield_both_sides), getString(R.string.playfun_report_user_title),getString(R.string.playfun_cancel_zuizong)};
                }
                if (viewModel.detailEntity.get().getIsBlacklist() == 1) {
                    items[0] = getString(R.string.playfun_remove_black_shield_both_sides);
                }
                new BottomSheet.Builder(mActivity).setDatas(items).setOnItemSelectedListener((bottomSheet, position) -> {
                    bottomSheet.dismiss();
                    if (position == 0) {
                        if (viewModel.detailEntity.get().getIsBlacklist() == 1) {
                            viewModel.delBlackList();
                        } else {
                            MVDialog.getInstance(mActivity)
                                    .setContent(getString(R.string.playfun_dialog_add_blacklist_content))
                                    .setConfirmText(getString(R.string.playfun_dialog_add_blacklist_content2))
                                    .setConfirmOnlick(dialog -> {
                                        viewModel.addBlackList();
                                    })
                                    .chooseType(MVDialog.TypeEnum.CENTERWARNED)
                                    .show();
                        }
//                    } else if (position == 1) {
//                        Bundle bundle = RemarkUserFragment.getStartBundle(userId);
//                        RemarkUserFragment remarkUserFragment = new RemarkUserFragment();
//                        remarkUserFragment.setArguments(bundle);
//                        start(remarkUserFragment);
                    } else if (position == 1) {
                        Bundle bundle = ReportUserFragment.getStartBundle("home", userId);
                        ReportUserFragment reportUserFragment = new ReportUserFragment();
                        reportUserFragment.setArguments(bundle);
                        start(reportUserFragment);
                    }else if(position == 2) {
                        if(viewModel.detailEntity.get().getCollect()){//取消追踪
                            viewModel.collectOnClickCommand.execute();
                        }
                    }
                }).setCancelButton(StringUtils.getString(R.string.playfun_cancel), new BottomSheet.CancelClickListener() {
                    @Override
                    public void onCancelClick(BottomSheet bottomSheet) {
                        bottomSheet.dismiss();
                    }
                }).build().show();
            }
        });
        viewModel.uc.clickEvaluate.observe(this, evaluateEntities -> {
            if (viewModel.detailEntity.get() == null) {
                return;
            }
            List<EvaluateObjEntity> list = null;
            if (viewModel.detailEntity.get().getSex() == 1) {
                list = Injection.provideDemoRepository().readMaleEvaluateConfig();
            } else {
                list = Injection.provideDemoRepository().readFemaleEvaluateConfig();
            }
            List<EvaluateItemEntity> items = new ArrayList<>();
            for (EvaluateObjEntity configEntity : list) {
                EvaluateItemEntity evaluateItemEntity = new EvaluateItemEntity(configEntity.getId(), configEntity.getName(), configEntity.getType() == 1);
                items.add(evaluateItemEntity);
                for (EvaluateEntity evaluateEntity : evaluateEntities) {
                    if (configEntity.getId() == evaluateEntity.getTagId()) {
                        evaluateItemEntity.setNumber(evaluateEntity.getNumber());
                    }
                }
            }
            MyEvaluateDialog dialog = new MyEvaluateDialog(viewModel.detailEntity.get().getSex() == 1 ? MyEvaluateDialog.TYPE_USER_MALE : MyEvaluateDialog.TYPE_USER_FEMALE, items);
            dialog.setEvaluateDialogListener(new MyEvaluateDialog.EvaluateDialogListener() {
                @Override
                public void onEvaluateClick(MyEvaluateDialog dialog) {
                    if (viewModel.canEvaluate.get()==false) {
                        ToastUtils.showShort(viewModel.detailEntity.get().isMale() ? R.string.playfun_no_information_cant_comment_him : R.string.playfun_no_information_cant_comment);
                        return;
                    }
                    dialog.dismiss();
                    CommitEvaluateDialog commitEvaluateDialog = new CommitEvaluateDialog(items);
                    commitEvaluateDialog.show(getChildFragmentManager(), CommitEvaluateDialog.class.getCanonicalName());
                    commitEvaluateDialog.setCommitEvaluateDialogListener((dialog12, entity) -> {
                        dialog12.dismiss();
                        if (entity != null) {
                            if (entity.isNegativeEvaluate()) {
                                MVDialog.getInstance(mActivity)
                                        .setContent(getString(R.string.playfun_provide_screenshot))
                                        .setConfirmText(getString(R.string.playfun_choose_photo))
                                        .setConfirmOnlick(dialog1 -> {
                                            dialog1.dismiss();
                                            PictureSelectorUtil.selectImage(mActivity, false, 1, new OnResultCallbackListener<LocalMedia>() {
                                                @Override
                                                public void onResult(List<LocalMedia> result) {
                                                    if (!result.isEmpty()) {
                                                        viewModel.commitNegativeEvaluate(entity.getTagId(), result.get(0).getCompressPath());
                                                    }
                                                }

                                                @Override
                                                public void onCancel() {
                                                }
                                            });
                                        })
                                        .chooseType(MVDialog.TypeEnum.CENTERWARNED)
                                        .show();
                            } else {
                                viewModel.commitUserEvaluate(entity.getTagId(), null);
                            }
                        }else{
                            ToastUtils.showShort(R.string.playfun_user_detail_evaluation_hint2);
                        }
                    });
                }

                @Override
                public void onAnonymousReportClick(MyEvaluateDialog dialog) {
                    dialog.dismiss();
                    Bundle bundle = ReportUserFragment.getStartBundle("home", userId);
                    ReportUserFragment fragment = new ReportUserFragment();
                    fragment.setArguments(bundle);
                    start(fragment);
                }
            });
            dialog.show(getChildFragmentManager(), MyEvaluateDialog.class.getCanonicalName());
        });
        viewModel.uc.clickApplyCheckDetail.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String nickName) {
                MVDialog.getInstance(mActivity)
                        .setTitele(String.format(getString(R.string.playfun_setting_limit), nickName))
                        .setContent(getString(R.string.playfun_setting_limit_content))
                        .setConfirmText(getString(R.string.playfun_choose_photo))
                        .setConfirmOnlick(dialog -> {
                            dialog.dismiss();
                            choosePhoto();
                        })
                        .chooseType(MVDialog.TypeEnum.CENTER)
                        .show();
            }
        });
        viewModel.uc.clickPayAlbum.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (viewModel.detailEntity.get() == null) {
                    return;
                }
                //非会员查看相册
                payLockAlbum(userId, viewModel.detailEntity.get().getNickname(), viewModel.detailEntity.get().getAlbumPayMoney());
            }
        });
        viewModel.uc.clickVipCheckAlbum.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                //会员查看相册
                if (viewModel.detailEntity.get() == null) {
                    return;
                }
                vipLockAlbum(viewModel.detailEntity.get().getNickname(), count, viewModel.detailEntity.get().getAlbumPayMoney());
            }
        });
        viewModel.uc.clickPayChat.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer coinPrice) {
                payCheckChat(coinPrice);
            }
        });
        viewModel.uc.clickVipChat.observe(this, integer -> vipCheckChat(integer, ConfigManager.getInstance().getImMoney()));
        viewModel.uc.clickConnMic.observe(this, aVoid -> System.out.println("连麦"));
        viewModel.uc.clickUnlockChatAccount.observe(this, coinPrice -> payUnlockChatAccount(coinPrice));
        viewModel.uc.todayCheckNumber.observe(this, integer -> DialogHelper.showCheckUserNumberDialog(UserDetailFragment.this, integer));
        viewModel.uc.clickCheckChatAccount.observe(this, aVoid -> checkChatAccount());

        viewModel.uc.isAlertVipMonetyunlock.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                getUserdetailUnlock();
            }
        });
        viewModel.uc.VipSuccessFlag.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                flagShow = true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // if(!ConfigManager.getInstance().isVip()){
//                if(flagShow){
//                    return;
//                }
            if ((viewModel.detailEntity.get().getMoreNumber() != null && viewModel.detailEntity.get().getMoreNumber() <= 0) || !viewModel.detailEntity.get().isBrowse()) {
                getUserdetailUnlock();
            }

            // }
        } else {
            if (AudioPlayer.getInstance().isPlaying()) {
                AudioPlayer.getInstance().stopPlay();
                binding.audioStart.setImageResource(R.drawable.mine_audio_start_img);
            }
        }
    }

//    @Override
//    public boolean onBackPressedSupport() {
//        Log.e("userDetail返回按键触发","===================");
//       return true;
//        // return mDelegate.onBackPressedSupport();
//    }

    public void getUserdetailUnlock() {
        if (flagShow) {//支付成功后不弹出
            return;
        }
        //只有男士会进入
        if (!ConfigManager.getInstance().isMale()) {
            return;
        }
        UserDetailEntity userDetailEntity = viewModel.detailEntity.get();
        if (!ObjectUtils.isEmpty(userDetailEntity.getIsView()) && userDetailEntity.getIsView().intValue() == 1 && userDetailEntity.isBrowse()) {
            return;
        }
        Integer MoreNumber = ConfigManager.getInstance().getGetUserHomeMoney();
        Integer getMoreNumber = viewModel.detailEntity.get().getMoreNumber();
        if (getMoreNumber == null) {
            return;
        }
        String title1 = null;
        String title2 = null;
        if (getMoreNumber.intValue() <= 2) {
            if (getMoreNumber.intValue() <= 0) {
                title1 = getString(R.string.playfun_userdetail_unlock_title1);
            } else {
                title1 = String.format(getString(R.string.playfun_userdetail_unlock_title), viewModel.detailEntity.get().getMoreNumber());
            }
            title2 = String.format(getString(R.string.playfun_pay_unlock), ConfigManager.getInstance().getGetUserHomeMoney());
        } else {
            title1 = String.format(getString(R.string.playfun_userdetail_unlock_title), viewModel.detailEntity.get().getMoreNumber());
        }
        String vipTitle = null;
        if (!ConfigManager.getInstance().isVip()) {
            vipTitle = getString(R.string.playfun_userdetail_unlock_btn_vip);
        }
        MMAlertDialog.isAlertVipMonetyunlock(UserDetailFragment.this.getContext(), true, title1, getString(R.string.playfun_userdetail_unlock_context), vipTitle, title2, new MMAlertDialog.AlertVipMonetyunlockInterface() {

            @Override
            public void confirm(DialogInterface dialog, int which) {
                dialog.dismiss();
                AppContext.instance().logEvent(AppsFlyerEvent.Become_A_VIP);
                viewModel.start(VipSubscribeFragment.class.getCanonicalName());
            }

            @Override
            public void confirmTwo(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    if (UserDetailFragment.this.getActivity() == null) {
                        return;
                    }
                    new CoinPaySheetUserMain.Builder(mActivity).setPayParams(12, userId, MoreNumber, false, new CoinPaySheetUserMain.UserdetailUnlockListener() {
                        @Override
                        public void onPaySuccess(CoinPaySheetUserMain sheet, String orderNo, Integer payPrice) {
                            AppContext.instance().logEvent(AppsFlyerEvent.Unlock_Now);
                            flagShow = true;
                            sheet.dismiss2(1);
                            dialog.dismiss();
                            viewModel.loadData();
                            ToastUtils.showShort(R.string.playfun_pay_success);
                        }

                        @Override
                        public void onPauError(int type) {
                            switch (type) {
                                case -1:
                                    getUserdetailUnlock();
                                    break;
                                case 1:
                                    break;
                                default:
                                    getUserdetailUnlock();
                                    break;
                            }
                        }
                    }).build().show();
                } catch (Exception e) {

                }
            }

            @Override
            public void cannel(DialogInterface dialog) {
                dialog.dismiss();
                if (UserDetailFragment.this.getActivity() == null) {
                    return;
                }
                if (!viewModel.detailEntity.get().isBrowse()) {
                    pop();
                }
            }
        }).show();
        //flagShow = true;
    }

    @Override
    public void initData() {
        super.initData();
        binding.refreshLayout.setEnableLoadMore(false);
        binding.meAvatar.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.blackBack.setOnClickListener(this);
        binding.appbarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    binding.layoutTitle.setVisibility(View.GONE);
                    ImmersionBarUtils.setupStatusBar(UserDetailFragment.this, false, true);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    binding.layoutTitle.setVisibility(View.VISIBLE);
                    ImmersionBarUtils.setupStatusBar(UserDetailFragment.this, true, true);
                } else {
                    //中间状态
//                    Toast.makeText(getActivity(),"中间状态",Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.nestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    //判断版本 大于安卓7
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!toolbarUp) {
                            if (scrollX > binding.appbarLayout.getHeight() || oldScrollY > binding.appbarLayout.getHeight()) {
                                toolbarUp = true;
                                binding.layoutTitle.setVisibility(View.VISIBLE);
                                ImmersionBarUtils.setupStatusBar(UserDetailFragment.this, true, true);
                            }
                        }
                    }

                }

                if (scrollY == 0) {
                    //Log.e("=====", "滑倒顶部");
                    if (toolbarUp) {
                        toolbarUp = false;
                        //展开状态
                        binding.layoutTitle.setVisibility(View.GONE);
                        ImmersionBarUtils.setupStatusBar(UserDetailFragment.this, false, true);
                    }
                }
            }
        });
        binding.audioStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.audioStart.setImageResource(R.drawable.mine_audio_stop_img);
                if (AudioPlayer.getInstance().isPlaying()) {
                    AudioPlayer.getInstance().stopPlay();
                    binding.audioStart.setImageResource(R.drawable.mine_audio_start_img);
                    return;
                }
                Glide.with(UserDetailFragment.this.getContext()).asGif().load(R.drawable.audio_waves)
                        .error(R.drawable.audio_waves_stop)
                        .placeholder(R.drawable.audio_waves_stop)
                        .into(binding.audioStart);
                AudioPlayer.getInstance().startPlay(StringUtil.getFullAudioUrl(viewModel.detailEntity.get().getSound()), new AudioPlayer.Callback() {
                    @Override
                    public void onCompletion(Boolean success) {
                        binding.audioStart.setImageResource(R.drawable.mine_audio_start_img);
                    }
                });
            }
        });
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.social_account_tag_anim);
        binding.detailBottomToolbar.ivSocialAccountTag.startAnimation(animation);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.me_avatar) {
            if (viewModel.detailEntity.get() == null) {
                return;
            }
            PictureSelectorUtil.previewImage(mActivity, StringUtil.getFullImageWatermarkUrl(viewModel.detailEntity.get().getAvatar()));
        } else if (view.getId() == R.id.iv_back) {
            onBackClick(null);
        } else if (view.getId() == R.id.black_back) {
            onBackClick(null);
        }
    }

    private void choosePhoto() {
        PictureSelectorUtil.selectImage(mActivity, false, 1, new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(List<LocalMedia> result) {
                viewModel.uploadImage(result.get(0).getCompressPath());
            }

            @Override
            public void onCancel() {
            }
        });
    }

    private void payLockAlbum(Integer userId, String nickName, Integer coinPrice) {
        String btn1 = "";
        if (AppContext.instance().appRepository.readUserData().getSex() == 1) {
            btn1 = getString(R.string.playfun_to_be_member_free_albums);
        } else {
            if (AppContext.instance().appRepository.readUserData().getCertification() == 1) {
                btn1 = getString(R.string.playfun_to_be_goddess_free_albums);
            } else {
                btn1 = getString(R.string.playfun_warn_no_certification);
            }
        }
        MVDialog.getInstance(UserDetailFragment.this.getContext())
                .setContent(String.format(getString(R.string.playfun_unlock_paid_album), nickName))
                .setConfirmText(btn1)
                .setConfirmTwoText(String.format(getString(R.string.playfun_pay_ro_unlock_diamond), coinPrice))
                .setConfirmOnlick(dialog -> {
                    dialog.dismiss();
                    if (AppContext.instance().appRepository.readUserData().getSex() == 1) {
                        viewModel.start(VipSubscribeFragment.class.getCanonicalName());
                    } else {
                        viewModel.start(CertificationFemaleFragment.class.getCanonicalName());
                    }
                })
                .setConfirmTwoOnclick(dialog -> {
                    dialog.dismiss();
                    new CoinPaySheet.Builder(mActivity).setPayParams(3, userId, getString(R.string.playfun_unlock_album), false, new CoinPaySheet.CoinPayDialogListener() {
                        @Override
                        public void onPaySuccess(CoinPaySheet sheet, String orderNo, Integer payPrice) {
                            sheet.dismiss();
                            ToastUtils.showShort(R.string.playfun_pay_success);
                            viewModel.payLockAlbumSuccess(userId);
                        }

                        @Override
                        public void onRechargeSuccess(GameCoinTopupSheetView gameCoinTopupSheetView) {
                            // do nothing
                        }
                    }).build().show();
                })
                .chooseType(MVDialog.TypeEnum.CENTER)
                .show();
    }

    private void vipLockAlbum(String nickName, Integer number, Integer coinPrice) {
        if (number == 0) {
            MVDialog.getInstance(UserDetailFragment.this.getContext())
                    .setContent(String.format(getString(R.string.playfun_pay_dimond_content), coinPrice, nickName))
                    .setConfirmText(String.format(getString(R.string.playfun_pay_diamond), coinPrice))
                    .setConfirmOnlick(dialog -> {
                        dialog.dismiss();
                        new CoinPaySheet.Builder(mActivity).setPayParams(3, userId, getString(R.string.playfun_unlock_album), false, new CoinPaySheet.CoinPayDialogListener() {
                            @Override
                            public void onPaySuccess(CoinPaySheet sheet, String orderNo, Integer payPrice) {
                                sheet.dismiss();
                                ToastUtils.showShort(R.string.playfun_pay_success);
                                viewModel.payLockAlbumSuccess(userId);
                            }

                            @Override
                            public void onRechargeSuccess(GameCoinTopupSheetView gameCoinTopupSheetView) {
                                // do nothing
                            }
                        }).build().show();
                    })
                    .chooseType(MVDialog.TypeEnum.CENTER)
                    .show();
        } else {
            MVDialog.getInstance(UserDetailFragment.this.getContext())
                    .setContent(String.format(getString(R.string.playfun_use_one_chance_content), nickName, number))
                    .setConfirmText(getString(R.string.playfun_use_one_chance))
                    .setConfirmOnlick(new MVDialog.ConfirmOnclick() {
                        @Override
                        public void confirm(MVDialog dialog) {
                            dialog.dismiss();
                            viewModel.useVipChat(userId, 2, 2);
                        }
                    })
                    .chooseType(MVDialog.TypeEnum.CENTER)
                    .show();
        }
    }

    private boolean isVip() {
        return AppContext.instance().appRepository.readUserData().getIsVip() == 1;
    }

    private void payCheckChat(Integer coinPrice) {
        String btn1 = "";
        String title = "";
        int sex = AppContext.instance().appRepository.readUserData().getSex();
        if (sex == AppConfig.MALE) {
            title = getString(R.string.playfun_to_chat_her);
            btn1 = getString(R.string.playfun_to_be_member_free_chat);
        } else {
            title = getString(R.string.playfun_to_chat_he);
            if (AppContext.instance().appRepository.readUserData().getCertification() == 1) {
                btn1 = getString(R.string.playfun_to_be_goddess_free_chat);
            } else {
                btn1 = getString(R.string.playfun_warn_no_certification);
            }
        }
        if (!isVip()) {
            MVDialog.getInstance(UserDetailFragment.this.getContext())
                    .setContent(title)
                    .setConfirmText(btn1)
                    .setConfirmTwoText(String.format(getString(R.string.playfun_paid_viewing_private_chat), coinPrice))
                    .setConfirmOnlick(dialog -> {
                        dialog.dismiss();
                        if (sex == AppConfig.MALE) {
                            viewModel.start(VipSubscribeFragment.class.getCanonicalName());
                        } else {
                            viewModel.start(CertificationFemaleFragment.class.getCanonicalName());
                        }
                    })
                    .setConfirmTwoOnclick(dialog -> {
                        dialog.dismiss();
                        showCoinPaySheet(false);
                    })
                    .chooseType(MVDialog.TypeEnum.CENTER)
                    .show();
        } else {
            MVDialog.getInstance(UserDetailFragment.this.getContext())
                    .setContent(title)
                    .setConfirmText(String.format(getString(R.string.playfun_paid_viewing_private_chat), coinPrice))
                    .setConfirmTextSize(12)
                    .setConfirmOnlick(dialog -> {
                        dialog.dismiss();
                        showCoinPaySheet(false);
                    })
                    .chooseType(MVDialog.TypeEnum.CENTER)
                    .show();
        }
    }

    private void showCoinPaySheet(boolean autoPay) {
        new CoinPaySheet.Builder(mActivity).setPayParams(6, userId, getString(R.string.playfun_check_detail), autoPay, new CoinPaySheet.CoinPayDialogListener() {
            @Override
            public void onPaySuccess(CoinPaySheet sheet, String orderNo, Integer payPrice) {
                sheet.dismiss();
                ToastUtils.showShort(R.string.playfun_pay_success);
                viewModel.chatPaySuccess();
            }

            @Override
            public void onRechargeSuccess(GameCoinTopupSheetView gameCoinTopupSheetView) {
                // 充值成功，再次唤起浮层，且自动支付
                try {
                    AppContext.runOnUIThread(gameCoinTopupSheetView::dismiss, 100);
                    AppContext.runOnUIThread(() -> showCoinPaySheet(true), 500);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).build().show();
    }

    private void vipCheckChat(Integer number, Integer coinPrice) {
        if (number <= 0) {
            // 实际上不会走到这个分支？
            MVDialog.getInstance(UserDetailFragment.this.getContext())
                    .setContent(String.format(getString(R.string.playfun_pay_diamond_content), coinPrice))
                    .setConfirmText(String.format(getString(R.string.playfun_pay_diamond), coinPrice))
                    .setConfirmOnlick(dialog -> {
                        dialog.dismiss();
                        new CoinPaySheet.Builder(mActivity).setPayParams(6, userId, getString(R.string.playfun_check_detail), false, new CoinPaySheet.CoinPayDialogListener() {
                            @Override
                            public void onPaySuccess(CoinPaySheet sheet, String orderNo, Integer payPrice) {
                                sheet.dismiss();
                                ToastUtils.showShort(R.string.playfun_pay_success);
                                viewModel.chatPaySuccess();
                            }

                            @Override
                            public void onRechargeSuccess(GameCoinTopupSheetView gameCoinTopupSheetView) {
                                // do nothing
                            }
                        }).build().show();
                    })
                    .chooseType(MVDialog.TypeEnum.CENTER)
                    .show();
        } else {
            MVDialog.getInstance(UserDetailFragment.this.getContext())
                    .setContent(String.format(getString(R.string.playfun_use_one_chance_chat), number))
                    .setConfirmText(getString(R.string.playfun_use_one_chance))
                    .setConfirmOnlick(dialog -> {
                        dialog.dismiss();
                        viewModel.useVipChat(userId, 1, 1);
                    })
                    .chooseType(MVDialog.TypeEnum.CENTER)
                    .show();
        }
    }

    private void payUnlockChatAccount(String coinPrice) {
        AppContext.instance().logEvent(AppsFlyerEvent.Social_Account);

        if (ConfigManager.getInstance().isVip()) {
            MVDialog.getInstance(UserDetailFragment.this.getContext())
                    .setTitele(getString(R.string.playfun_unlock_account_title_dialog))
                    .setConfirmText(String.format(getString(R.string.playfun_pay_unlock), ConfigManager.getInstance().getUnLockAccountMoneyVip()))
                    .setConfirmOnlick(new MVDialog.ConfirmOnclick() {
                        @Override
                        public void confirm(MVDialog dialog) {
                            dialog.dismiss();
                            new CoinPaySheet.Builder(mActivity).setPayParams(11, userId, getString(R.string.playfun_unlock_social_account), false, new CoinPaySheet.CoinPayDialogListener() {
                                @Override
                                public void onPaySuccess(CoinPaySheet sheet, String orderNo, Integer payPrice) {
                                    sheet.dismiss();
                                    AppContext.instance().logEvent(AppsFlyerEvent.Unlock);
                                    ToastUtils.showShort(R.string.playfun_pay_success);
                                    viewModel.payUnlockChatAccountSuccess(userId);
                                    viewModel.uc.clickCheckChatAccount.call();
                                    if (!StringUtils.isEmpty(viewModel.detailEntity.get().getWeixin())) {
                                        binding.accountLine.setText("Line: " + viewModel.detailEntity.get().getWeixin());
                                    } else if (!StringUtils.isEmpty(viewModel.detailEntity.get().getInsgram()) && viewModel.detailEntity.get().getIsUnlockAccount() == 1) {
                                        binding.accountLine.setText("Instagram: " + viewModel.detailEntity.get().getInsgram());
                                    }
                                }

                                @Override
                                public void onRechargeSuccess(GameCoinTopupSheetView gameCoinTopupSheetView) {
                                    // do nothing
                                }
                            }).build().show();
                        }
                    })
                    .chooseType(MVDialog.TypeEnum.CENTER)
                    .show();
        } else {
            MVDialog.getInstance(UserDetailFragment.this.getContext())
                    .setTitele(getString(R.string.playfun_unlock_account_title_dialog))
                    .setConfirmText(String.format(getString(R.string.playfun_join_vip_unlock), ConfigManager.getInstance().getUnLockAccountMoneyVip()))
                    .setConfirmTwoText(String.format(getString(R.string.playfun_no_vip_unlock), ConfigManager.getInstance().getUnLockAccountMoney()))
                    .setConfirmOnlick(dialog -> {
                        dialog.dismiss();
                        AppContext.instance().logEvent(AppsFlyerEvent.Become_A_VIP);
                        if (AppContext.instance().appRepository.readUserData().getSex() == 1) {
                            viewModel.start(VipSubscribeFragment.class.getCanonicalName());
                        } else {
                            viewModel.start(CertificationFemaleFragment.class.getCanonicalName());
                        }
                    })
                    .setConfirmTwoOnclick(dialog -> {
                        dialog.dismiss();
                        new CoinPaySheet.Builder(mActivity).setPayParams(11, userId, getString(R.string.playfun_unlock_social_account), false, new CoinPaySheet.CoinPayDialogListener() {
                            @Override
                            public void onPaySuccess(CoinPaySheet sheet, String orderNo, Integer payPrice) {
                                AppContext.instance().logEvent(AppsFlyerEvent.Unlock_Now);
                                sheet.dismiss();
                                ToastUtils.showShort(R.string.playfun_pay_success);
                                viewModel.payUnlockChatAccountSuccess(userId);
                                viewModel.uc.clickCheckChatAccount.call();
                                if (!StringUtils.isEmpty(viewModel.detailEntity.get().getWeixin())) {
                                    binding.accountLine.setText("Line: " + viewModel.detailEntity.get().getWeixin());
                                } else if (!StringUtils.isEmpty(viewModel.detailEntity.get().getInsgram()) && viewModel.detailEntity.get().getIsUnlockAccount() == 1) {
                                    binding.accountLine.setText("Instagram: " + viewModel.detailEntity.get().getInsgram());
                                }
                            }

                            @Override
                            public void onRechargeSuccess(GameCoinTopupSheetView gameCoinTopupSheetView) {
                                // do nothing
                            }
                        }).build().show();
                    })
                    .chooseType(MVDialog.TypeEnum.CENTER)
                    .show();
        }
    }

    private void checkChatAccount() {
        if (viewModel.detailEntity.get() == null) {
            return;
        }
        CopyAccountDialog copyAccountDialog = null;
        String account = null;
        if (viewModel.detailEntity.get().getIsUnlockAccount() == 1 && viewModel.detailEntity.get().getIsWeixinShow() == 1) {
            if (viewModel.detailEntity.get().getWeixin() != null && viewModel.detailEntity.get().getWeixin().length() > 0) {
                account = "Line: " + viewModel.detailEntity.get().getWeixin();
            } else if (viewModel.detailEntity.get().getInsgram() != null && viewModel.detailEntity.get().getInsgram().length() > 0) {
                account = "Insgram: " + viewModel.detailEntity.get().getInsgram();
            } else {
                ToastUtils.showShort(R.string.playfun_user_detail_link_empty);
                return;
            }
        }
        copyAccountDialog = new CopyAccountDialog(account);
        copyAccountDialog.setBtnText(getString(R.string.playfun_confirm));
//        if (account != null) {
//            copyAccountDialog = new CopyAccountDialog(account);
//        } else {
//            copyAccountDialog = new CopyAccountDialog();
//        }
//        copyAccountDialog.setTitle(getString(R.string.she_social_accounts));
//        if (viewModel.detailEntity.get().getIsWeixinShow() != 1) {
//            copyAccountDialog.setContent(getString(R.string.chat_ask_me));
//            copyAccountDialog.setBtnText(getString(R.string.chat_she));
//        } else {
//            if (account == null) {
//                copyAccountDialog.setContent(getString(R.string.unlock_account));
//                copyAccountDialog.setBtnText(getString(R.string.immediately_unlock));
//            } else {
//                copyAccountDialog.setBtnText(getString(R.string.confirm));
//            }
//
//        }
        copyAccountDialog.show(getChildFragmentManager(), CopyAccountDialog.class.getCanonicalName());
        copyAccountDialog.setCopyAccountDialogListener(new CopyAccountDialog.CopyAccountDialogListener() {
            @Override
            public void onCopyClick(CopyAccountDialog dialog) {
                dialog.dismiss();
                if (viewModel.detailEntity.get().getWeixin() != null && viewModel.detailEntity.get().getWeixin().length() > 0) {
                    copyStr(viewModel.detailEntity.get().getWeixin());
                } else if (viewModel.detailEntity.get().getInsgram() != null && viewModel.detailEntity.get().getInsgram().length() > 0) {
                    copyStr(viewModel.detailEntity.get().getInsgram());
                }
                ToastUtils.showShort(R.string.playfun_copy_clipboard);
            }

            @Override
            public void onChatClick(CopyAccountDialog dialog) {
                dialog.dismiss();
                if (viewModel.detailEntity.get().getIsWeixinShow() != 1) {
                    viewModel.isChat(userId, 1);
                } else {
                    if (dialog.getAccount() == null) {
                        viewModel.isChat(userId, 4);
                    }
                }
            }
        });
    }

    protected void copyStr(String text) {
        ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("play_fun", text);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public boolean isBaseOnWidth() {
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 360;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart(this.getClass().getSimpleName());
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
//    }
}
