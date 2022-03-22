package com.dl.playfun.ui.mine.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.StringUtils;
import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.dl.playfun.app.AppConfig;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.app.AppViewModelFactory;
import com.dl.playfun.app.AppsFlyerEvent;
import com.dl.playfun.databinding.FragmentWalletBinding;
import com.dl.playfun.entity.CoinExchangePriceInfo;
import com.dl.playfun.entity.GameCoinBuy;
import com.dl.playfun.ui.base.BaseToolbarFragment;
import com.dl.playfun.ui.certification.certificationfemale.CertificationFemaleFragment;
import com.dl.playfun.ui.certification.certificationmale.CertificationMaleFragment;
import com.dl.playfun.widget.coinrechargesheet.GameCoinExchargeSheetView;
import com.dl.playfun.widget.coinrechargesheet.GameCoinTopupSheetView;
import com.dl.playfun.widget.dialog.MVDialog;

import me.goldze.mvvmhabit.utils.ToastUtils;
import me.jessyan.autosize.internal.CustomAdapt;

/**
 * @author wulei
 */
public class WalletFragment extends BaseToolbarFragment<FragmentWalletBinding, WalletViewModel> implements CustomAdapt,View.OnClickListener{

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_wallet;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public WalletViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(mActivity.getApplication());
        return ViewModelProviders.of(this, factory).get(WalletViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        viewModel.getUserAccount();
        binding.btnExchangeGameCoin.setOnClickListener(this);
        binding.btnGameCoinTopup.setOnClickListener(this);
        viewModel.certification.observe(this,event -> {
            MVDialog.getInstance(WalletFragment.this.getContext())
                    .setTitele(getString(R.string.playfun_fragment_certification_tip))
                    .setContent(getString(R.string.playfun_fragment_certification_content))
                    .setConfirmText(getString(R.string.playfun_task_fragment_task_new11))
                    .chooseType(MVDialog.TypeEnum.CENTER)
                    .setConfirmOnlick(new MVDialog.ConfirmOnclick() {
                        @Override
                        public void confirm(MVDialog dialog) {
                            if (AppContext.instance().appRepository.readUserData().getSex() == AppConfig.MALE) {
                                viewModel.start(CertificationMaleFragment.class.getCanonicalName());
                                return;
                            } else if (AppContext.instance().appRepository.readUserData().getSex() == AppConfig.FEMALE) {
                                viewModel.start(CertificationFemaleFragment.class.getCanonicalName());
                                return;
                            }
                            com.blankj.utilcode.util.ToastUtils.showShort(R.string.playfun_sex_unknown);
                            dialog.dismiss();
                        }
                    })
                    .chooseType(MVDialog.TypeEnum.CENTER)
                    .show();
        });
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_exchange_game_coin) {
            AppContext.instance().logEvent(AppsFlyerEvent.Top_up);
            GameCoinExchargeSheetView coinRechargeSheetView = new GameCoinExchargeSheetView(mActivity);
            coinRechargeSheetView.show();
            coinRechargeSheetView.setCoinRechargeSheetViewListener(new GameCoinExchargeSheetView.CoinRechargeSheetViewListener() {
                @Override
                public void onPaySuccess(GameCoinExchargeSheetView sheetView, CoinExchangePriceInfo sel_goodsEntity) {
                    sheetView.dismiss();
                    viewModel.getUserAccount();
                }

                @Override
                public void onPayFailed(GameCoinExchargeSheetView sheetView, String msg) {
                    sheetView.dismiss();
                    ToastUtils.showShort(msg);
                    AppContext.instance().logEvent(AppsFlyerEvent.Failed_to_top_up);
                }
            });
        }else if(R.id.btn_game_coin_topup == v.getId()){
            GameCoinTopupSheetView coinRechargeSheetView = new GameCoinTopupSheetView(mActivity);
            coinRechargeSheetView.show();
            coinRechargeSheetView.setCoinRechargeSheetViewListener(new GameCoinTopupSheetView.CoinRechargeSheetViewListener() {
                @Override
                public void onPaySuccess(GameCoinTopupSheetView sheetView, GameCoinBuy sel_goodsEntity) {
                    sheetView.endGooglePlayConnect();
                    sheetView.dismiss();
                    MVDialog.getInstance(WalletFragment.this.getContext())
                            .setTitele(StringUtils.getString(R.string.playfun_recharge_coin_success))
                            .setConfirmText(StringUtils.getString(R.string.playfun_confirm))
                            .setConfirmOnlick(dialog -> {
                                dialog.dismiss();
                                viewModel.getUserAccount();
                            })
                            .chooseType(MVDialog.TypeEnum.CENTER)
                            .show();
                }

                @Override
                public void onPayFailed(GameCoinTopupSheetView sheetView, String msg) {
                    sheetView.dismiss();
                    ToastUtils.showShort(msg);
                    AppContext.instance().logEvent(AppsFlyerEvent.Failed_to_top_up);
                }
            });
        }


    }

    @Override
    public boolean isBaseOnWidth() {
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 360;
    }
}
