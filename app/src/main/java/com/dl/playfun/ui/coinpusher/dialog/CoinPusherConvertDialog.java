package com.dl.playfun.ui.coinpusher.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.dl.playfun.R;
import com.dl.playfun.data.source.http.exception.RequestException;
import com.dl.playfun.data.source.http.observer.BaseObserver;
import com.dl.playfun.data.source.http.response.BaseDataResponse;
import com.dl.playfun.databinding.DialogCoinpusherConverBinding;
import com.dl.playfun.entity.CoinPusherBalanceDataEntity;
import com.dl.playfun.entity.CoinPusherConverInfoEntity;
import com.dl.playfun.manager.ConfigManager;
import com.dl.playfun.ui.base.BaseDialog;
import com.dl.playfun.ui.coinpusher.dialog.adapter.CoinPusherCapsuleAdapter;
import com.dl.playfun.ui.coinpusher.dialog.adapter.CoinPusherConvertAdapter;

import me.goldze.mvvmhabit.binding.viewadapter.recyclerview.LayoutManagers;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * Author: 彭石林
 * Time: 2022/8/23 12:22
 * Description: 兑换购买弹窗
 */
public class CoinPusherConvertDialog  extends BaseDialog {

    private final Context mContext;
    private DialogCoinpusherConverBinding binding;
    //宝盒适配器
    private CoinPusherCapsuleAdapter coinPusherCapsuleAdapter;
    //金币兑换砖石
    private CoinPusherConvertAdapter coinPusherConvertAdapter;

    private ItemConvertListener itemConvertListener;

    public ItemConvertListener getItemConvertListener() {
        return itemConvertListener;
    }

    public void setItemConvertListener(ItemConvertListener itemConvertListener) {
        this.itemConvertListener = itemConvertListener;
    }

    //当前用户金币余额
    private int totalMoney = 0;

    private int SEL_COIN_PUSHER_CAPSULE  = -1;

    public CoinPusherConvertDialog(Activity activity) {
        super(activity);
        this.mContext = activity;
        super.setMActivity(activity);
        initView();
        onClickListener();
        loadData();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_coinpusher_conver, null, false);
        //支持LiveData绑定xml，数据改变，UI自动会更新
        binding.setLifecycleOwner(this);
        //表格布局
        LayoutManagers.LayoutManagerFactory layoutManagerFactory = LayoutManagers.grid(2);
        binding.rcvCapsule.setLayoutManager(layoutManagerFactory.create(binding.rcvCapsule));
        coinPusherCapsuleAdapter = new CoinPusherCapsuleAdapter();
        binding.rcvCapsule.setAdapter(coinPusherCapsuleAdapter);

        binding.rcvConvert.setLayoutManager(layoutManagerFactory.create(binding.rcvConvert));
        coinPusherConvertAdapter = new CoinPusherConvertAdapter();
        binding.rcvConvert.setAdapter(coinPusherConvertAdapter);

    }
    //点击事件初始化
    private void onClickListener(){
        //关闭当前弹窗
        binding.imgClose.setOnClickListener(v-> dismiss());
        binding.tvTabCapsule.setOnClickListener(v->{
            if(binding.llCapsuleLayout.getVisibility() == View.GONE){
                binding.tvTabConver.setTextColor(ColorUtils.getColor(R.color.play_chat_gray_3));
                binding.tvTabCapsule.setTextColor(ColorUtils.getColor(R.color.black));
                binding.llCapsuleLayout.setVisibility(View.VISIBLE);
                binding.llConverLayout.setVisibility(View.GONE);
            }
        });
        binding.tvTabConver.setOnClickListener(v->{
            if(binding.llConverLayout.getVisibility() == View.GONE){
                binding.tvTabCapsule.setTextColor(ColorUtils.getColor(R.color.play_chat_gray_3));
                binding.tvTabConver.setTextColor(ColorUtils.getColor(R.color.black));
                binding.llConverLayout.setVisibility(View.VISIBLE);
                binding.llCapsuleLayout.setVisibility(View.GONE);
            }
        });

        coinPusherCapsuleAdapter.setOnItemClickListener(position -> {
            if(SEL_COIN_PUSHER_CAPSULE!=position){
                coinPusherCapsuleAdapter.setDefaultSelect(position);
                SEL_COIN_PUSHER_CAPSULE = position;
            }
            //选择购买宝盒明细弹窗
            CoinPusherConvertCapsuleDialog pusherConvertCapsuleDialog = new CoinPusherConvertCapsuleDialog(getMActivity(),coinPusherCapsuleAdapter.getItemData(SEL_COIN_PUSHER_CAPSULE).getItem());
            pusherConvertCapsuleDialog.setItemConvertListener(coinPusherDataEntity -> {
                ToastUtils.showShort(R.string.playfun_coinpusher_text_5);
                //购买成功数据相加
                totalMoney = coinPusherDataEntity.getTotalGold();
                tvTotalMoneyRefresh();
                coinPusherConvertAdapter.setMaxValuerSelect(totalMoney);
                if(getItemConvertListener()!=null){
                    getItemConvertListener().convertSuccess(coinPusherDataEntity);
                }
                pusherConvertCapsuleDialog.dismiss();
            });
            pusherConvertCapsuleDialog.show();
        });
        coinPusherConvertAdapter.setOnItemClickListener(position -> {
                if(SEL_COIN_PUSHER_CAPSULE!=position){
                    coinPusherConvertAdapter.setDefaultSelect(position);
                    SEL_COIN_PUSHER_CAPSULE = position;
                    if(totalMoney >= coinPusherConvertAdapter.getItemData(position).getValue()){
                        binding.tvSubConvert.setTextColor(ColorUtils.getColor(R.color.black));
                        binding.tvSubConvert.setEnabled(true);
                    }else{
                        binding.tvSubConvert.setEnabled(false);
                        binding.tvSubConvert.setTextColor(ColorUtils.getColor(R.color.gray_light));
                    }
                }
        });
        //购买按钮
        binding.tvSubConvert.setOnClickListener(v -> convertCoinPusherDiamonds(coinPusherConvertAdapter.getItemData(SEL_COIN_PUSHER_CAPSULE).getId()));
    }
    //监听dialog隐藏
    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    public void show() {
        //设置背景透明,去四个角
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(binding.getRoot());
        //设置宽度充满屏幕
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM); //可设置dialog的位置
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        super.show();
    }
    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void loadData(){
        ConfigManager.getInstance().getAppRepository().qryCoinPusherConverList()
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHud())
                .subscribe(new BaseObserver<BaseDataResponse<CoinPusherConverInfoEntity>>() {
                    @Override
                    public void onSuccess(BaseDataResponse<CoinPusherConverInfoEntity> converInfoEntityResponse) {
                        CoinPusherConverInfoEntity coinPusherConvertInfo = converInfoEntityResponse.getData();
                        if(ObjectUtils.isNotEmpty(coinPusherConvertInfo)){
                            //给宝盒列表数据-展示
                            if(ObjectUtils.isNotEmpty(coinPusherConvertInfo.getGoldCoinList())){
                                coinPusherCapsuleAdapter.setItemData(coinPusherConvertInfo.getGoldCoinList());
                                totalMoney = coinPusherConvertInfo.getTotalGold();
                                binding.tvCapsuleHint.setText(coinPusherConvertInfo.getGoldTips());
                                tvTotalMoneyRefresh();
                            }
                            //金币兑换砖石
                            if(ObjectUtils.isNotEmpty(coinPusherConvertInfo.getDiamondsList())){
                                coinPusherConvertAdapter.setItemData(coinPusherConvertInfo.getDiamondsList(),totalMoney);
                            }
                        }
                    }
                    @Override
                    public void onComplete() {
                        dismissHud();
                    }
                });

    }

    @Override
    public void showHud() {
        super.showHud();
    }

    @Override
    public void dismissHud() {
        super.dismissHud();
    }

    private void tvTotalMoneyRefresh(){
        String val = totalMoney > 99999 ? totalMoney+"+" : totalMoney+"";
        String format = String.format(StringUtils.getString(R.string.playfun_coinpusher_text_4),val);
        binding.tvConverDetail.setText(Html.fromHtml(format));
    }
    //金币兑换砖石礼包
    private void convertCoinPusherDiamonds(final Integer id){
        ConfigManager.getInstance().getAppRepository().convertCoinPusherDiamonds(id)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHud())
                .subscribe(new BaseObserver<BaseDataResponse<CoinPusherBalanceDataEntity>>(){

                    @Override
                    public void onSuccess(BaseDataResponse<CoinPusherBalanceDataEntity> coinPusherDataEntityResponse) {
                        CoinPusherBalanceDataEntity coinPusherDataEntity = coinPusherDataEntityResponse.getData();
                        if(ObjectUtils.isNotEmpty(coinPusherDataEntity)){
                            totalMoney = coinPusherDataEntity.getTotalGold();
                            coinPusherConvertAdapter.setMaxValuerSelect(totalMoney);
                            tvTotalMoneyRefresh();
                        }
                    }

                    @Override
                    public void onError(RequestException e) {
                        super.onError(e);
                    }
                    @Override
                    public void onComplete() {
                        dismissHud();
                    }
                });
    }

    public interface ItemConvertListener{
        void convertSuccess(CoinPusherBalanceDataEntity coinPusherDataEntity);
        default void buyError() {

        }
    }
}
