package com.dl.playfun.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.dl.playfun.app.ElkLogEventReport;
import com.dl.playfun.data.AppRepository;
import com.dl.playfun.data.source.http.exception.RequestException;
import com.dl.playfun.data.source.http.observer.BaseObserver;
import com.dl.playfun.data.source.http.response.BaseResponse;
import com.dl.playfun.entity.ParkItemEntity;
import com.dl.playfun.event.AccostEvent;
import com.dl.playfun.event.LikeChangeEvent;
import com.dl.playfun.event.TaskListEvent;
import com.dl.playfun.event.TaskTypeStatusEvent;
import com.dl.playfun.event.UserRemarkChangeEvent;
import com.dl.playfun.ui.home.HomeMainViewModel;
import com.dl.playfun.utils.ToastCenterUtils;
import com.dl.playfun.viewmodel.BaseRefreshViewModel;

import java.util.Objects;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * @author wulei
 */
public abstract class BaseParkViewModel<T extends AppRepository> extends BaseRefreshViewModel<T> {

    public BindingRecyclerViewAdapter<BaseParkItemViewModel> adapter = new BindingRecyclerViewAdapter<>();
    public ObservableList<BaseParkItemViewModel> observableList = new ObservableArrayList<>();


    public static final String ItemPark = "item_park";
    public static final String ItemParkBanner = "item_park_banner";

    public ItemBinding<BaseParkItemViewModel> itemBinding = ItemBinding.of((itemBinding, position, item) ->{
        if(item.getItemType()==null){
            itemBinding.set(BR.viewModel, R.layout.item_park);
        }else{
            String itemType = String.valueOf(item.getItemType());
            if (itemType.equals(ItemPark)) {
                //正常子item
                itemBinding.set(BR.viewModel, R.layout.item_park);
            } else if (itemType.equals(ItemParkBanner)) {
                //广告图
                itemBinding.set(BR.viewModel, R.layout.item_park_banner);
            }
        }
    });
    private Disposable mSubscription2;
    private Disposable mLikeSubscription;
    private Disposable taskTypeStatusEvent;
    private Disposable mAccostSubscription;
    private boolean accostThree = false;
    private boolean dayAccost = false;

    public BaseParkViewModel(@NonNull Application application, T model) {
        super(application, model);
    }

    @Override
    public void registerRxBus() {
        super.registerRxBus();

        mSubscription2 = RxBus.getDefault().toObservable(UserRemarkChangeEvent.class)
                .subscribe(event -> {
                    for (BaseParkItemViewModel itemViewModel : observableList) {
                        ParkItemEntity parkItemEntity =  itemViewModel.itemEntity.get();
                        if (parkItemEntity!=null && parkItemEntity.getId()  == event.getUserId()) {
                            itemViewModel.itemEntity.get().setNickname(event.getRemarkName());
                            break;
                        }
                    }
                });
        mLikeSubscription = RxBus.getDefault().toObservable(LikeChangeEvent.class)
                .subscribe(event -> {
                    if (event.getFrom() == null || event.getFrom() != this) {
                        for (BaseParkItemViewModel itemViewModel : observableList) {
                            ParkItemEntity parkItemEntity =  itemViewModel.itemEntity.get();
                            if (parkItemEntity!=null && parkItemEntity.getId() == event.getUserId()) {
                                itemViewModel.itemEntity.get().setCollect(event.isLike());
                                break;
                            }
                        }
                    }
                });
        taskTypeStatusEvent = RxBus.getDefault().toObservable(TaskTypeStatusEvent.class)
                .subscribe(taskTypeStatusEvent -> {
                    accostThree = taskTypeStatusEvent.getAccostThree() == 0;
                    dayAccost = taskTypeStatusEvent.getDayAccost() == 0;
                });
        mAccostSubscription = RxBus.getDefault().toObservable(AccostEvent.class)
                .subscribe(accostEvent -> {
                    int position = accostEvent.position;
                    int itemCount = adapter.getItemCount();
                    if (position < 0 || position > itemCount -1){
                        return;
                    }
                    adapter.getAdapterItem(position).itemEntity.get().setIsAccost(1);
                    adapter.notifyItemChanged(position);
                });

        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription2);
        RxSubscriptions.add(mLikeSubscription);
        RxSubscriptions.add(taskTypeStatusEvent);
        RxSubscriptions.add(mAccostSubscription);

    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        //将订阅者从管理站中移除
        RxSubscriptions.remove(mSubscription2);
        RxSubscriptions.remove(mLikeSubscription);
        RxSubscriptions.remove(taskTypeStatusEvent);
        RxSubscriptions.remove(mAccostSubscription);
    }

    public void addLike(int position) {
        ParkItemEntity parkItemEntity = observableList.get(position).itemEntity.get();
        model.addCollect(parkItemEntity.getId())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        parkItemEntity.setCollect(true);
                        RxBus.getDefault().post(new LikeChangeEvent(BaseParkViewModel.this, observableList.get(position).itemEntity.get().getId(), true));
                    }

                    @Override
                    public void onError(RequestException e) {
                        super.onError(e);
                        parkItemEntity.setCollect(false);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dismissHUD();
                    }
                });
    }

    public void delLike(int position) {
        ParkItemEntity parkItemEntity = observableList.get(position).itemEntity.get();
        model.deleteCollect(parkItemEntity.getId())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        parkItemEntity.setCollect(false);
                        RxBus.getDefault().post(new LikeChangeEvent(BaseParkViewModel.this, observableList.get(position).itemEntity.get().getId(), false));
                    }

                    @Override
                    public void onError(RequestException e) {
                        super.onError(e);
                        parkItemEntity.setCollect(false);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dismissHUD();
                    }
                });
    }

    //搭讪
    public void putAccostFirst(int position,int accostSource) {
        ParkItemEntity parkItemEntity = observableList.get(position).itemEntity.get();

        model.putAccostFirst(parkItemEntity.getId())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ElkLogEventReport.reportAccostModule.reportAccostView(accostSource,parkItemEntity.getId(),parkItemEntity.getSex(),null);
                        ToastUtils.showShort(R.string.playfun_text_accost_success1);
                        parkItemEntity.setCollect(false);
                        Objects.requireNonNull(adapter.getAdapterItem(position).itemEntity.get()).setIsAccost(1);
                        adapter.getAdapterItem(position).accountCollect.set(true);
                        adapter.notifyItemChanged(position);
                        AccostFirstSuccess(parkItemEntity, position);
                        //刷新任务列表状态
                        if (accostThree || dayAccost)RxBus.getDefault().post(new TaskListEvent());
                    }

                    @Override
                    public void onError(RequestException e) {
                        super.onError(e);
                        ElkLogEventReport.reportAccostModule.reportAccostView(accostSource,parkItemEntity.getId(),parkItemEntity.getSex(),e.getMessage()+e.getCode());
                        if(e.getCode()!=null && e.getCode().intValue()==21001 ){//钻石余额不足
                            ToastCenterUtils.showToast(R.string.playfun_dialog_exchange_integral_total_text3);
                            AccostFirstSuccess(null, position);
                        }
                        parkItemEntity.setCollect(false);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dismissHUD();
                    }
                });
    }

    public abstract void AccostFirstSuccess(ParkItemEntity itemEntity, int position);

}
