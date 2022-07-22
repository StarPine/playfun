package com.dl.playfun.widget.coinrechargesheet;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetailsParams;
import com.blankj.utilcode.util.ObjectUtils;
import com.dl.playfun.R;
import com.dl.playfun.api.AppGameConfig;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.app.BillingClientLifecycle;
import com.dl.playfun.data.source.http.exception.RequestException;
import com.dl.playfun.data.source.http.observer.BaseObserver;
import com.dl.playfun.data.source.http.response.BaseDataResponse;
import com.dl.playfun.data.source.http.response.BaseResponse;
import com.dl.playfun.entity.CreateOrderEntity;
import com.dl.playfun.entity.GameCoinBuy;
import com.dl.playfun.entity.GameCoinWalletEntity;
import com.dl.playfun.event.MyCardPayResultEvent;
import com.dl.playfun.event.UMengCustomEvent;
import com.dl.playfun.manager.ConfigManager;
import com.dl.playfun.ui.base.BaseDialog;
import com.dl.playfun.ui.dialog.PayMethodDialog;
import com.dl.playfun.ui.dialog.adapter.GameCoinTopupAdapter;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * Author: 彭石林
 * Time: 2022/4/26 13:05
 * Description: This is CoinExchargeItegralPayDialog
 */
public class CoinExchargeItegralPayDialog extends BaseDialog implements View.OnClickListener, GameCoinTopupAdapter.GameCoinTopupAdapterListener {

    public static final String TAG = "CoinRechargeSheetView";

    private final AppCompatActivity mActivity;
    private final Handler handler = new Handler();
    private View mPopView;
    private RecyclerView recyclerView;
    private TextView tvBalance;
    private ImageView ivRefresh;
    private ViewGroup loadingView;
    private GameCoinTopupAdapter adapter;
    private List<GameCoinBuy> mGoodsList;
    private String orderNumber = null;
    private CoinExchargeItegralPayDialog.CoinRechargeSheetViewListener coinRechargeSheetViewListener;
    private Disposable mSubscription;
    private GameCoinBuy sel_goodsEntity;
    private ImageView imgGameCoin;

    private BillingClientLifecycle billingClientLifecycle;

    public CoinExchargeItegralPayDialog(@NonNull Context context, AppCompatActivity activity) {
        super(context);
        this.mActivity = activity;
        init(activity);
        this.billingClientLifecycle = ((AppContext)activity.getApplication()).getBillingClientLifecycle();
    }

    public CoinExchargeItegralPayDialog.CoinRechargeSheetViewListener getCoinRechargeSheetViewListener() {
        return this.coinRechargeSheetViewListener;
    }

    public void setCoinRechargeSheetViewListener(CoinExchargeItegralPayDialog.CoinRechargeSheetViewListener coinRechargeSheetViewListener) {
        this.coinRechargeSheetViewListener = coinRechargeSheetViewListener;
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (this.mPopView == null) {
            this.mPopView = inflater.inflate(R.layout.view_game_coin_topup_sheet, (ViewGroup)null);
        }

        this.imgGameCoin = (ImageView)this.mPopView.findViewById(R.id.icon_game_coin);
        AppGameConfig appGameConfig = ConfigManager.getInstance().getAppRepository().readGameConfigSetting();
        if (!ObjectUtils.isEmpty(appGameConfig) && appGameConfig.getGamePlayCoinSmallImg() != 0) {
            this.imgGameCoin.setImageResource(appGameConfig.getGamePlayCoinSmallImg());
        }

        this.recyclerView = (RecyclerView)this.mPopView.findViewById(R.id.recycler_view);
        this.tvBalance = (TextView)this.mPopView.findViewById(R.id.tv_balance);
        this.ivRefresh = (ImageView)this.mPopView.findViewById(R.id.iv_refresh);
        this.loadingView = (ViewGroup)this.mPopView.findViewById(R.id.rl_loading);
        this.ivRefresh.setOnClickListener(this);
        this.adapter = new GameCoinTopupAdapter(this.recyclerView);
        this.adapter.setCoinRechargeAdapterListener(this);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.recyclerView.setAdapter(this.adapter);
        this.mSubscription = RxBus.getDefault().toObservable(MyCardPayResultEvent.class).subscribe((event) -> {
            if (event.getStatus() != 1) {
                if (event.getStatus() == 3) {
                    if (this.coinRechargeSheetViewListener != null) {
                        this.coinRechargeSheetViewListener.onPayFailed(this, event.getErrorMsg());
                    } else {
                        this.dismiss();
                        ToastUtils.showShort(R.string.playfun_pay_success);
                        this.loadBalance();
                        this.dismiss();
                    }
                } else if (event.getStatus() == 2) {
                    ToastUtils.showShort(R.string.playfun_pay_cancel);
                }
            }

        });
        this.billingClientLifecycle.PAYMENT_SUCCESS.observe(this, new Observer<Purchase>() {
            @Override
            public void onChanged(Purchase purchase) {
                Log.e("BillingClientLifecycle","dialog支付购买成功："+purchase.toString());
            }
        });
        this.billingClientLifecycle.PAYMENT_FAIL.observe(this, new Observer<Purchase>() {
            @Override
            public void onChanged(Purchase purchase) {
                Log.e("BillingClientLifecycle","dialog支付购买失败："+purchase.toString());
            }
        });
        this.loadBalance();
        this.loadGoods();
    }
    public void dismiss() {
        if (this.mSubscription != null) {
            this.mSubscription.dispose();
        }

        Log.e("CoinRechargeSheetView", "dismiss view destory google connection");
        if (this.mActivity != null && !this.mActivity.isFinishing()) {
            Window dialogWindow = this.mActivity.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = -1;
            lp.height = -1;
            lp.alpha = 1.0F;
            dialogWindow.setAttributes(lp);
        }

        try {
            if (this.mActivity != null && this.mActivity.isFinishing()) {
                this.endGooglePlayConnect();
            }
        } catch (Exception var3) {
            this.endGooglePlayConnect();
        }

        this.coinRechargeSheetViewListener = null;
        try {
            if(isShowing()){
                super.dismiss();
            }
        }catch (Exception e) {

        }
    }

    public void endGooglePlayConnect() {
        if (this.handler != null) {
            this.handler.removeCallbacksAndMessages((Object)null);
        }

    }

    public void show() {
        if (this.billingClientLifecycle == null) {
            this.billingClientLifecycle = ((AppContext)mActivity.getApplication()).getBillingClientLifecycle();
        }

        //设置背景透明,去四个角
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(mPopView);
        //设置宽度充满屏幕
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        super.show();
    }

    private void loadBalance() {
        ConfigManager.getInstance().getAppRepository().getUserAccountPageInfo().doOnSubscribe(this).compose(RxUtils.schedulersTransformer()).compose(RxUtils.exceptionTransformer()).subscribe(new BaseObserver<BaseDataResponse<GameCoinWalletEntity>>() {
            public void onSuccess(BaseDataResponse<GameCoinWalletEntity> response) {
                GameCoinWalletEntity gameCoinWalletEntity = response.getData();
                if(gameCoinWalletEntity!=null){
                    tvBalance.setText(String.valueOf(gameCoinWalletEntity.getTotalAppCoins()));
                }
            }

            public void onError(RequestException e) {
                ToastUtils.showShort(e.getMessage());
            }

            public void onComplete() {
                super.onComplete();
            }
        });
    }

    private void loadGoods() {
        ConfigManager.getInstance().getAppRepository().buyGameCoins().doOnSubscribe(this).compose(RxUtils.schedulersTransformer()).compose(RxUtils.exceptionTransformer()).subscribe(new BaseObserver<BaseDataResponse<List<GameCoinBuy>>>() {
            public void onSuccess(BaseDataResponse<List<GameCoinBuy>> response) {
                CoinExchargeItegralPayDialog.this.loadingView.setVisibility(View.GONE);
                CoinExchargeItegralPayDialog.this.mGoodsList = (List)response.getData();
                if (CoinExchargeItegralPayDialog.this.mGoodsList != null) {
                    CoinExchargeItegralPayDialog.this.adapter.setData(CoinExchargeItegralPayDialog.this.mGoodsList);
                    CoinExchargeItegralPayDialog.this.recyclerView.postDelayed(() -> {
                        CoinExchargeItegralPayDialog.this.recyclerView.smoothScrollToPosition(1);
                    }, 500L);
                }

            }

            public void onComplete() {
            }
        });
    }

    public void createOrder(GameCoinBuy goodsEntity) {
        this.loadingView.setVisibility(View.VISIBLE);
        ConfigManager.getInstance().getAppRepository().createOrder(goodsEntity.getId(), 1, 2, (Integer)null).doOnSubscribe(this).compose(RxUtils.schedulersTransformer()).compose(RxUtils.exceptionTransformer()).subscribe(new BaseObserver<BaseDataResponse<CreateOrderEntity>>() {
            public void onSuccess(BaseDataResponse<CreateOrderEntity> response) {
                CoinExchargeItegralPayDialog.this.loadingView.setVisibility(View.GONE);
                CoinExchargeItegralPayDialog.this.orderNumber = ((CreateOrderEntity)response.getData()).getOrderNumber();
                PayMethodDialog dialog = new PayMethodDialog(String.valueOf(((CreateOrderEntity)response.getData()).getMoney()));
                dialog.setPayMethodDialogListener(new PayMethodDialog.PayMethodDialogListener() {
                    public void onConfirmClick(PayMethodDialog dialog, int payMethod) {
                        dialog.dismiss();
                        if (payMethod != 1002 && payMethod == 1001) {
                            List<GameCoinBuy> goodsEntityList = new ArrayList();
                            goodsEntityList.add(goodsEntity);
                            CoinExchargeItegralPayDialog.this.querySkuList(goodsEntityList);
                        }

                    }

                    public void onCancelClick(PayMethodDialog dialog) {
                        dialog.dismiss();
                    }
                });
                List<GameCoinBuy> goodsEntityList = new ArrayList();
                goodsEntityList.add(goodsEntity);
                CoinExchargeItegralPayDialog.this.querySkuList(goodsEntityList);
            }

            public void onError(RequestException e) {
                super.onError(e);
                CoinExchargeItegralPayDialog.this.loadingView.setVisibility(View.GONE);
            }
        });
    }

    public void paySuccessNotify(String packageName, String orderNumber, List<String> productId, String token, Integer event) {
        Log.e("in repolt pay notify", "==============1");

        try {
            this.loadingView.setVisibility(View.VISIBLE);
        } catch (Exception var7) {
        }

        ConfigManager.getInstance().getAppRepository().paySuccessNotify(packageName, orderNumber, productId, token, 1, event).doOnSubscribe(this).compose(RxUtils.schedulersTransformer()).compose(RxUtils.exceptionTransformer()).subscribe(new BaseObserver<BaseResponse>() {
            public void onSuccess(BaseResponse response) {
                Log.e("pay notify uccess", "===========");

                try {
                    CoinExchargeItegralPayDialog.this.loadingView.setVisibility(View.GONE);
                } catch (Exception var3) {
                }

                if (CoinExchargeItegralPayDialog.this.coinRechargeSheetViewListener != null) {
                    CoinExchargeItegralPayDialog.this.loadGoods();
                    CoinExchargeItegralPayDialog.this.loadBalance();
                    CoinExchargeItegralPayDialog.this.coinRechargeSheetViewListener.onPaySuccess(CoinExchargeItegralPayDialog.this, CoinExchargeItegralPayDialog.this.sel_goodsEntity);
                } else {
                    CoinExchargeItegralPayDialog.this.dismiss();
                    RxView.clicks(CoinExchargeItegralPayDialog.this.tvBalance).throttleFirst(5L, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {
                        public void accept(Object object) throws Exception {
                            ToastUtils.showShort(R.string.playfun_pay_success);
                        }
                    });
                    CoinExchargeItegralPayDialog.this.loadBalance();
                    CoinExchargeItegralPayDialog.this.dismiss();
                }

            }

            public void onError(RequestException e) {
                Log.e("pay notify error", "===========" + e.getMessage());
                CoinExchargeItegralPayDialog.this.loadingView.setVisibility(View.GONE);
                if (CoinExchargeItegralPayDialog.this.coinRechargeSheetViewListener != null) {
                    CoinExchargeItegralPayDialog.this.coinRechargeSheetViewListener.onPayFailed(CoinExchargeItegralPayDialog.this, e.getMessage());
                } else {
                    CoinExchargeItegralPayDialog.this.dismiss();
                }

            }

            public void onComplete() {
                Log.e("pay notify Complete", "===========");
                try {
                    CoinExchargeItegralPayDialog.this.loadingView.setVisibility(View.GONE);
                } catch (Exception var3) {
                }
            }
        });
    }

    public void onClick(View v) {
        if (v.getId() == R.id.iv_refresh) {
            this.loadBalance();
        }

    }

    public void onBuyClick(View view, int position) {
        RxBus.getDefault().post(new UMengCustomEvent("pay-recharge-token"));
        GameCoinBuy goodsEntity = (GameCoinBuy)this.mGoodsList.get(position);
        this.sel_goodsEntity = goodsEntity;
        this.createOrder(goodsEntity);
    }

    private void querySkuList(List<GameCoinBuy> goodsList) {
        if (goodsList != null && !goodsList.isEmpty()) {
            List<String> skus = new ArrayList();
            Iterator var3 = goodsList.iterator();

            while(var3.hasNext()) {
                GameCoinBuy datum = (GameCoinBuy)var3.next();
                skus.add(datum.getGoogleGoodsId());
            }

            this.loadingView.setVisibility(View.VISIBLE);
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skus).setType(BillingClient.SkuType.INAPP);
            billingClientLifecycle.querySkuDetailsAsync(params,mActivity);
        }
    }

    public interface CoinRechargeSheetViewListener {
        void onPaySuccess(CoinExchargeItegralPayDialog sheetView, GameCoinBuy goodsEntity);

        void onPayFailed(CoinExchargeItegralPayDialog sheetView, String msg);
    }
}
