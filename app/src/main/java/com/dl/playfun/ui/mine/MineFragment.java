package com.dl.playfun.ui.mine;

import static com.dl.playfun.ui.dialog.MyEvaluateDialog.TYPE_MYSELF;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.billingclient.api.BillingClient;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.app.AppViewModelFactory;
import com.dl.playfun.app.AppsFlyerEvent;
import com.dl.playfun.app.BillingClientLifecycle;
import com.dl.playfun.app.Injection;
import com.dl.playfun.databinding.FragmentMineBinding;
import com.dl.playfun.entity.BrowseNumberEntity;
import com.dl.playfun.entity.EvaluateEntity;
import com.dl.playfun.entity.EvaluateItemEntity;
import com.dl.playfun.entity.EvaluateObjEntity;
import com.dl.playfun.entity.UserInfoEntity;
import com.dl.playfun.manager.ConfigManager;
import com.dl.playfun.ui.base.BaseRefreshFragment;
import com.dl.playfun.ui.certification.certificationfemale.CertificationFemaleFragment;
import com.dl.playfun.ui.dialog.MyEvaluateDialog;
import com.dl.playfun.ui.mine.setredpackagephoto.SetRedPackagePhotoFragment;
import com.dl.playfun.ui.mine.setredpackagevideo.SetRedPackageVideoFragment;
import com.dl.playfun.ui.mine.setting.account.bind.CommunityAccountBindFragment;
import com.dl.playfun.utils.AutoSizeUtils;
import com.dl.playfun.utils.ImmersionBarUtils;
import com.dl.playfun.utils.PictureSelectorUtil;
import com.dl.playfun.utils.SoftKeyBoardListener;
import com.dl.playfun.utils.StringUtil;
import com.dl.playfun.widget.animate.JumpingSpan;
import com.dl.playfun.widget.bottomsheet.BottomSheet;
import com.dl.playfun.widget.dialog.MMAlertDialog;
import com.dl.playfun.widget.dialog.MVDialog;
import com.dl.playfun.widget.dialog.UserBehaviorDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.tencent.qcloud.tuicore.Status;
import com.tencent.qcloud.tuikit.tuichat.component.AudioPlayer;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * @author wulei
 */
public class MineFragment extends BaseRefreshFragment<FragmentMineBinding, MineViewModel> {

    protected InputMethodManager inputMethodManager;
    private boolean SoftKeyboardShow = false;

    private int toolbarHeight = -1;
    private boolean toolbarUp = false;

    static String KEY_USER_BIND_PHONE_HINT = "key_user_bind_phone_hint_";

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
       // ImmersionBarUtils.setupStatusBar(this, false, true);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        ImmersionBarUtils.setupStatusBar(this, true, true);
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AutoSizeUtils.applyAdapt(this.getResources());
        return R.layout.fragment_mine;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public MineViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(mActivity.getApplication());
        return ViewModelProviders.of(this, factory).get(MineViewModel.class);
    }

    /**
     * 如果软键盘显示就隐藏软键盘
     */
    protected void hideSoftKeyboard() {
        if (SoftKeyboardShow) {
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    /**
     * @return void
     * @Desc TODO(页面再次进入)
     * @author 彭石林
     * @parame [hidden]
     * @Date 2021/8/4
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            JumpingBeans.with(binding.taskRightTitle)
//                    .makeTextJump(0, binding.taskRightTitle.getText().length())
//                    .setIsWave(true)
//                    .setLoopDuration(1300)
//                    .build();

            viewModel.newsBrowseNumber();
        } else {
            if (AudioPlayer.getInstance().isPlaying()) {
                AudioPlayer.getInstance().stopPlay();
                binding.audioWaves.setImageResource(R.drawable.audio_waves_stop);
            }
        }
    }

    @Override
    public void initData() {
        super.initData();
        toolbarHeight = binding.imgTopAvatar.getHeight();
        binding.refreshLayout.setEnableLoadMore(false);
        //允许语音开关
        binding.shAudio.setOnCheckedChangeListener((buttonView, isChecked) ->
                viewModel.setAllowPrivacy(viewModel.ALLOW_TYPE_AUDIO, isChecked));
        //允许视讯开关
        binding.shVideo.setOnCheckedChangeListener((buttonView, isChecked) ->
                viewModel.setAllowPrivacy(viewModel.ALLOW_TYPE_VIDEO, isChecked));
        binding.viewNestedScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                //判断版本 大于安卓7
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!toolbarUp) {
                        if (scrollX > toolbarHeight || oldScrollY > toolbarHeight) {
                            toolbarUp = true;
                            //折叠状态
                            binding.layoutTitle.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            if (scrollY == 0) {
                //Log.e("=====", "滑倒顶部");
                //判断版本 大于安卓7
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (toolbarUp) {
                        toolbarUp = false;
                        binding.layoutTitle.setVisibility(View.GONE);
                    }
                }
            }
        });
        binding.audioStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Status.mIsShowFloatWindow){
                    ToastUtils.showShort(R.string.audio_in_call);
                    return;
                }
                UserInfoEntity userInfoEntity = viewModel.userInfoEntity.get();
                if(userInfoEntity!=null && !StringUtils.isEmpty(userInfoEntity.getSound()) ){
                    binding.audioStop.setImageResource(R.drawable.mine_audio_stop_img);
                    if (AudioPlayer.getInstance().isPlaying()) {
                        AudioPlayer.getInstance().stopPlay();
                        Glide.with(MineFragment.this.getContext()).asGif().load(R.drawable.audio_waves_stop)
                                .error(R.drawable.audio_waves_stop)
                                .placeholder(R.drawable.audio_waves_stop)
                                .into(binding.audioWaves);
                        return;
                    }
                    Glide.with(MineFragment.this.getContext()).asGif().load(R.drawable.audio_waves)
                            .error(R.drawable.audio_waves_stop)
                            .placeholder(R.drawable.audio_waves_stop)
                            .into(binding.audioWaves);
                    AudioPlayer.getInstance().startPlay(StringUtil.getFullAudioUrl(userInfoEntity.getSound()), new AudioPlayer.Callback() {
                        @Override
                        public void onCompletion(Boolean success, Boolean isOutTime) {
                            binding.audioStop.setImageResource(R.drawable.mine_audio_start_img);
                            binding.audioWaves.setImageResource(R.drawable.audio_waves_stop);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        AppContext.instance().logEvent(AppsFlyerEvent.Me);
        inputMethodManager = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        viewModel.uc.localReportEvent.observe(this , unused->{
            try {
                BillingClientLifecycle billingClientLifecycle = ((AppContext) mActivity.getApplication()).getBillingClientLifecycle();
                billingClientLifecycle.queryPurchasesAsyncToast(BillingClient.SkuType.INAPP);
                billingClientLifecycle.queryPurchasesAsyncToast(BillingClient.SkuType.SUBS);
            }catch (Exception ignored) {

            }
        });

        viewModel.uc.dialogUserBindEvent.observe(this, unused -> {
            if(viewModel.userInfoEntity.get()!=null){
                int userId = viewModel.userInfoEntity.get().getId();
                String value = ConfigManager.getInstance().getAppRepository().readKeyValue(KEY_USER_BIND_PHONE_HINT+userId);
                if(StringUtils.isEmpty(value)){
                    UserBehaviorDialog.getUserBindPhonesDialog(getContext(), () -> startFragment(CommunityAccountBindFragment.class.getCanonicalName())).show();
                    ConfigManager.getInstance().getAppRepository().putKeyValue(KEY_USER_BIND_PHONE_HINT+userId,KEY_USER_BIND_PHONE_HINT);
                }
            }
        });
        viewModel.uc.allowAudio.observe(this,aBoolean -> {
            binding.shAudio.setChecked(aBoolean);
        });
        viewModel.uc.allowVideo.observe(this,aBoolean -> {
            binding.shVideo.setChecked(aBoolean);
        });
        viewModel.uc.removeAudioAlert.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                MMAlertDialog.AlertAudioRemove(getContext(), false, new MMAlertDialog.DilodAlertInterface() {
                    @Override
                    public void confirm(DialogInterface dialog, int which, int sel_Index) {
                        dialog.dismiss();
                        viewModel.removeSound();
                    }

                    @Override
                    public void cancel(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        viewModel.uc.loadBrowseNumber.observe(this, new Observer<BrowseNumberEntity>() {
            @Override
            public void onChanged(BrowseNumberEntity browseNumberEntity) {
                if (!ObjectUtils.isEmpty(viewModel.userInfoEntity.get()) && !ObjectUtils.isEmpty(browseNumberEntity)) {
                    if (viewModel.userInfoEntity.get().getSex() == 0) {
                        if (ObjectUtils.isEmpty(browseNumberEntity.getFansNumber()) || browseNumberEntity.getFansNumber().intValue() < 1) {
                            binding.traceNum.setVisibility(View.INVISIBLE);
                        } else {
                            int fensi = browseNumberEntity.getFansNumber().intValue();
                            String total = fensi > 99 ? "+99" : "+"+fensi;
                            binding.traceNum.setText(total);
                            binding.traceNum.setVisibility(View.VISIBLE);
                            binding.traceNum.setAlpha(0.1f);
                            binding.traceNum.animate()
                                    .alpha(1.0f)
                                    .setDuration(2000)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                        }
                                    })
                                    .start();
                        }
                    } else if (viewModel.userInfoEntity.get().getSex() == 1) {
                        if (ObjectUtils.isEmpty(browseNumberEntity.getBrowseNumber()) || browseNumberEntity.getBrowseNumber().intValue() < 1) {
                            binding.traceNum.setVisibility(View.INVISIBLE);
                        } else {
                            int fensi = browseNumberEntity.getBrowseNumber().intValue();
                            String total = fensi > 99 ? "+99" : "+"+fensi;
                            binding.traceNum.setText(total);
                            binding.traceNum.setVisibility(View.VISIBLE);
                            binding.traceNum.setAlpha(0.1f);
                            binding.traceNum.animate()
                                    .alpha(1.0f)
                                    .setDuration(2000)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                        }
                                    })
                                    .start();
                        }
                    }

                } else {
                    binding.traceNum.setVisibility(View.INVISIBLE);
                }

            }
        });
        SoftKeyBoardListener.setListener(mActivity, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                SoftKeyboardShow = true;
            }

            @Override
            public void keyBoardHide(int height) {
                SoftKeyboardShow = false;
            }
        });
        viewModel.uc.clickAvatar.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                chooseAvatar();
            }
        });
        viewModel.uc.clickMyEvaluate.observe(this, evaluateEntities -> {
            List<EvaluateObjEntity> list = null;
            if (Injection.provideDemoRepository().readUserData().getSex() == 1) {
                list = Injection.provideDemoRepository().readMaleEvaluateConfig();
            } else {
                list = Injection.provideDemoRepository().readFemaleEvaluateConfig();
            }
            if (list == null) {
                list = new ArrayList<>();
            }
            List<EvaluateItemEntity> items = new ArrayList<>();
            for (EvaluateObjEntity configEntity : list) {
                EvaluateItemEntity evaluateItemEntity = new EvaluateItemEntity(configEntity.getId(), configEntity.getName());
                items.add(evaluateItemEntity);
                for (EvaluateEntity evaluateEntity : evaluateEntities) {
                    if (configEntity.getId() == evaluateEntity.getTagId()) {
                        evaluateItemEntity.setNumber(evaluateEntity.getNumber());
                    }
                }
            }
            MyEvaluateDialog dialog = new MyEvaluateDialog(TYPE_MYSELF, items);
            dialog.show(getChildFragmentManager(), MyEvaluateDialog.class.getCanonicalName());
        });
        viewModel.uc.clickPrivacy.observe(this, aVoid -> {
            String[] items = new String[]{getString(R.string.playfun_public_recommended), getString(R.string.playfun_pay_to_unlock), getString(R.string.playfun_need_verify_look)};
            int selectIndex = viewModel.userInfoEntity.get().getAlbumType() - 1;
            if (selectIndex < 0) {
                selectIndex = 0;
            }
            new BottomSheet.Builder(mActivity)
                    .setType(BottomSheet.BOTTOM_SHEET_TYPE_SINGLE_CHOOSE)
                    .setTitle(getString(R.string.playfun_mine_album_privacy))
                    .setDatas(items)
                    .setSelectIndex(selectIndex)
                    .setOnItemSelectedListener((bottomSheet, position) -> {
                        bottomSheet.dismiss();
                        if (position == 0) {
                            AppContext.instance().logEvent(AppsFlyerEvent.All_Recommended);
                            viewModel.setAlbumPrivacy(1, null);
                        } else if (position == 1) {
                            if (viewModel.userInfoEntity.get().getSex() == 0 && viewModel.userInfoEntity.get().getIsVip() != 1) {
                                MVDialog.getInstance(MineFragment.this.getContext())
                                        .setTitele(getStringByResId(R.string.playfun_goddess_unlock_payalbum))
                                        .setConfirmText(getStringByResId(R.string.playfun_goddess_certification))
                                        .setConfirmOnlick(new MVDialog.ConfirmOnclick() {
                                            @Override
                                            public void confirm(MVDialog dialog) {
                                                dialog.dismiss();
                                                startFragment(CertificationFemaleFragment.class.getCanonicalName());
                                            }
                                        })
                                        .chooseType(MVDialog.TypeEnum.CENTER)
                                        .show();
                                return;
                            }
                            //"非认证女士需要付费解锁才能查看你的相册，费用由你定；认证女士可以消耗一次查看机会解锁你的相册。";
                            String msg = String.format(getString(R.string.playfun_mine_contrnt_one),
                                    viewModel.userInfoEntity.get().getSex() == 1 ? getString(R.string.playfun_non_certified_lady) : getString(R.string.playfun_non_certified_man),
                                    viewModel.userInfoEntity.get().getSex() == 1 ? getString(R.string.playfun_certified_lady) : getString(R.string.playfun_certified_man));
                            MVDialog.getInstance(MineFragment.this.getContext())
                                    .setContent(msg)
                                    .setConfirmText(getString(R.string.playfun_carry_on))
                                    .setConfirmOnlick(new MVDialog.ConfirmOnclick() {
                                        @Override
                                        public void confirm(MVDialog dialog) {
                                            dialog.dismiss();
                                            MVDialog.getInstance(MineFragment.this.getContext())
                                                    .setTitele(getString(R.string.playfun_set_view_amount))
                                                    .setConfirmMoneyOnclick(new MVDialog.ConfirmMoneyOnclick() {
                                                        @Override
                                                        public void confirm(MVDialog dialog, String money) {
                                                            if (!StringUtils.isEmpty(money)) {
                                                                dialog.dismiss();
                                                                AppContext.instance().logEvent(AppsFlyerEvent.Paid_me);
                                                                viewModel.setAlbumPrivacy(2, Integer.parseInt(money));
                                                            } else {
                                                                ToastUtils.showShort(R.string.playfun_please_enter_amount);
                                                            }
                                                        }
                                                    }).chooseType(MVDialog.TypeEnum.SET_MONEY)
                                                    .setOnDismiss(new MVDialog.OnDismissListener() {
                                                        @Override
                                                        public void onDismiss(Dialog dialog) {
                                                            hideSoftKeyboard();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    })
                                    .chooseType(MVDialog.TypeEnum.CENTER)
                                    .show();
                        } else if (position == 2) {
                            String msg = String.format(getString(R.string.playfun_mine_contrnt_two), viewModel.userInfoEntity.get().getSex() == 1 ? getString(R.string.playfun_lady) : getString(R.string.playfun_man));
                            MVDialog.getInstance(MineFragment.this.getContext())
                                    .setContent(msg)
                                    .chooseType(MVDialog.TypeEnum.CENTER)
                                    .setConfirmOnlick(new MVDialog.ConfirmOnclick() {
                                        @Override
                                        public void confirm(MVDialog dialog) {
                                            dialog.dismiss();
                                            AppContext.instance().logEvent(AppsFlyerEvent.Ask_me);
                                            viewModel.setAlbumPrivacy(3, null);
                                        }
                                    })
                                    .chooseType(MVDialog.TypeEnum.CENTER)
                                    .show();
                        }
                    }).setCancelButton(getString(R.string.playfun_cancel), new BottomSheet.CancelClickListener() {
                @Override
                public void onCancelClick(BottomSheet bottomSheet) {
                    bottomSheet.dismiss();
                    AppContext.instance().logEvent(AppsFlyerEvent.cancel_me);
                    hideSoftInput();
                }
            }).build().show();
        });
        viewModel.uc.clickSetRedPackagePhoto.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                String[] items = new String[]{getString(R.string.playfun_mine_set_redpackage_photo), getString(R.string.playfun_setting_red_video)};
                new BottomSheet.Builder(mActivity)
                        .setType(BottomSheet.BOTTOM_SHEET_TYPE_NORMAL)
                        .setDatas(items)
                        .setOnItemSelectedListener(new BottomSheet.ItemSelectedListener() {
                            @Override
                            public void onItemSelected(BottomSheet bottomSheet, int position) {
                                bottomSheet.dismiss();
                                if (viewModel.userInfoEntity.get().getIsVip() != 1) {
                                    MVDialog.getInstance(MineFragment.this.getContext())
                                            .setTitele(getStringByResId(R.string.playfun_goddess_unlock_redpackage_photo))
                                            .setConfirmText(getStringByResId(R.string.playfun_goddess_certification))
                                            .setConfirmOnlick(new MVDialog.ConfirmOnclick() {
                                                @Override
                                                public void confirm(MVDialog dialog) {
                                                    dialog.dismiss();
                                                    startFragment(CertificationFemaleFragment.class.getCanonicalName());
                                                }
                                            })
                                            .chooseType(MVDialog.TypeEnum.CENTER)
                                            .show();
                                    return;
                                }
                                if (position == 0) {
                                    viewModel.start(SetRedPackagePhotoFragment.class.getCanonicalName());
                                } else if (position == 1) {
                                    viewModel.start(SetRedPackageVideoFragment.class.getCanonicalName());
                                }
                            }
                        }).setCancelButton(getString(R.string.playfun_cancel), new BottomSheet.CancelClickListener() {
                    @Override
                    public void onCancelClick(BottomSheet bottomSheet) {
                        bottomSheet.dismiss();
                    }
                }).build().show();
            }
        });
        viewModel.uc.clickRecoverBurn.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                MVDialog.getInstance(MineFragment.this.getContext())
                        .setContent(getString(R.string.playfun_dialog_recover_burn_content))
                        .setConfirmOnlick(new MVDialog.ConfirmOnclick() {
                            @Override
                            public void confirm(MVDialog dialog) {
                                dialog.dismiss();
                                viewModel.burnReset();
                            }
                        })
                        .chooseType(MVDialog.TypeEnum.CENTER)
                        .show();
            }
        });
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        binding.refreshLayout.setEnableLoadMore(false);
        binding.recyclerView.setNestedScrollingEnabled(false);
    }

    private void chooseAvatar() {
        PictureSelectorUtil.selectImageAndCrop(mActivity, true, 1, 1, new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(List<LocalMedia> result) {
                viewModel.userInfoEntity.get().setAvatar(result.get(0).getCutPath());
                viewModel.saveAvatar(result.get(0).getCutPath());
            }

            @Override
            public void onCancel() {
            }
        });
    }

    private JumpingSpan[] buildWavingSpans(SpannableStringBuilder sbb, TextView tv) {
        JumpingSpan[] spans;
        int loopDuration = 1300;
        int startPos = 0;//textview字体的开始位置
        int endPos = tv.getText().length();//结束位置
        int waveCharDelay = loopDuration / (3 * (endPos - startPos));//每个字体延迟的时间
        spans = new JumpingSpan[endPos - startPos];
        for (int pos = startPos; pos < endPos; pos++) {//设置每个字体的jumpingspan
            JumpingSpan jumpingBean =
                    new JumpingSpan(tv, loopDuration, pos - startPos, waveCharDelay, 0.65f);
            sbb.setSpan(jumpingBean, pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spans[pos - startPos] = jumpingBean;
        }
        return spans;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (AudioPlayer.getInstance().isPlaying()) {
            AudioPlayer.getInstance().stopPlay();
        }
    }
}
