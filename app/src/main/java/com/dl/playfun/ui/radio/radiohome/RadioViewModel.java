package com.dl.playfun.ui.radio.radiohome;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.app.AppsFlyerEvent;
import com.dl.playfun.data.AppRepository;
import com.dl.playfun.data.source.http.exception.RequestException;
import com.dl.playfun.data.source.http.observer.BaseObserver;
import com.dl.playfun.data.source.http.response.BaseDataResponse;
import com.dl.playfun.data.source.http.response.BaseResponse;
import com.dl.playfun.entity.AdBannerEntity;
import com.dl.playfun.entity.AdItemEntity;
import com.dl.playfun.entity.AdUserBannerEntity;
import com.dl.playfun.entity.AdUserItemEntity;
import com.dl.playfun.entity.BroadcastEntity;
import com.dl.playfun.entity.BroadcastListEntity;
import com.dl.playfun.entity.CallingInviteInfo;
import com.dl.playfun.entity.ConfigItemEntity;
import com.dl.playfun.entity.UserDataEntity;
import com.dl.playfun.event.BadioEvent;
import com.dl.playfun.event.CoinPusherRoomEvent;
import com.dl.playfun.event.LikeChangeEvent;
import com.dl.playfun.event.RadioadetailEvent;
import com.dl.playfun.event.TaskListEvent;
import com.dl.playfun.event.TaskMainTabEvent;
import com.dl.playfun.event.TaskTypeStatusEvent;
import com.dl.playfun.event.ZoomInPictureEvent;
import com.dl.playfun.kl.Utils;
import com.dl.playfun.manager.ConfigManager;
import com.dl.playfun.ui.mine.broadcast.mytrends.TrendItemViewModel;
import com.dl.playfun.ui.mine.wallet.diamond.recharge.DiamondRechargeActivity;
import com.dl.playfun.ui.radio.issuanceprogram.IssuanceProgramFragment;
import com.dl.playfun.ui.radio.radiohome.item.RadioItemBannerVideoViewModel;
import com.dl.playfun.ui.task.webview.FukuokaViewFragment;
import com.dl.playfun.utils.ApiUitl;
import com.dl.playfun.viewmodel.BaseRefreshViewModel;
import com.google.gson.Gson;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

/**
 * @author wulei
 */
public class RadioViewModel extends BaseRefreshViewModel<AppRepository> {
    public static final String RadioRecycleType_New = "new";
    public static final String RadioRecycleType_trace = "emptyTrace";
    public ObservableField<UserDataEntity> userDataEntity = new ObservableField<>(new UserDataEntity());
    public ObservableField<String> area = new ObservableField<>();
    public int userId;
    public String avatar;
    public UIChangeObservable radioUC = new UIChangeObservable();
    public Integer type = 1;
    public Integer cityId = null;
    public Integer gameId = null;
    public Integer sexId = null;
    public Integer IsCollect = 1;
    public boolean CollectFlag = false;
    public Integer certification = null;
    public boolean collectReLoad = false;
    //最后依次点击音怕播放item下标
    public Integer lastClickAudioPlayer = -1;

    //新增广告轮播类型
    public ObservableField<List<AdItemEntity>> itemBannerEntity = new ObservableField<>();
    public ObservableBoolean itemBannerShow = new ObservableBoolean(false);

    //位置选择文字
    public ObservableField<String> regionTitle = new ObservableField<>(StringUtils.getString(R.string.playfun_tab_female_1));
    public ObservableField<String> tarckingTitle = new ObservableField<>(StringUtils.getString(R.string.playfun_radio_selected_zuiz));

    public BindingRecyclerViewAdapter<RadioItemBannerVideoViewModel> adapterAdUser = new BindingRecyclerViewAdapter<>();
    public ObservableList<RadioItemBannerVideoViewModel> radioItemsAdUser = new ObservableArrayList<>();
    public ItemBinding<RadioItemBannerVideoViewModel> radioItemAdUserBinding = ItemBinding.of(BR.viewModel, R.layout.item_radio_banner_video);

    public BindingRecyclerViewAdapter<MultiItemViewModel> adapter = new BindingRecyclerViewAdapter<>();
    public ObservableList<MultiItemViewModel> radioItems = new ObservableArrayList<>();
    public ItemBinding<MultiItemViewModel> radioItemBinding = ItemBinding.of(new OnItemBind<MultiItemViewModel>() {
        @Override
        public void onItemBind(ItemBinding itemBinding, int position, MultiItemViewModel item) {
            //通过item的类型, 动态设置Item加载的布局
            String itemType = (String) item.getItemType();
            if (RadioRecycleType_New.equals(itemType)) {
                //设置new
                itemBinding.set(BR.viewModel, R.layout.item_trend);
            } else if (RadioRecycleType_trace.equals(itemType)) {
                //设置看追踪列表为空
                itemBinding.set(BR.viewModel, R.layout.item_radio_trace_empty);
            }
        }
    });
    /**
     * 发布按钮的点击事件
     */
    public BindingCommand publishOnClickCommand = new BindingCommand(() -> {
        start(IssuanceProgramFragment.class.getCanonicalName());
    });
    //选择城市
    public BindingCommand regionOnClickCommand = new BindingCommand(() -> radioUC.clickRegion.call());
    //选择追踪的人栏目
    public BindingCommand clickTackingClickCommand = new BindingCommand(() -> radioUC.clickTacking.call());

    //banner点击
    public BindingCommand<Integer> onBannerClickCommand = new BindingCommand<>(index -> {
        try {
            AdItemEntity adItemEntity = itemBannerEntity.get().get(index);
            int typeAct = adItemEntity.getType();
            if(typeAct!=0){
                if (typeAct ==4){
                    //弹出推币机
                    RxBus.getDefault().post(new CoinPusherRoomEvent());
                } else if (typeAct == 5) {
                    startActivity(DiamondRechargeActivity.class);
                }
            }else if(adItemEntity.getLink() != null){
                Bundle bundle = new Bundle();
                bundle.putString("link", adItemEntity.getLink());
                start(FukuokaViewFragment.class.getCanonicalName(), bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
    //item点击切换
    public void itemClickChangeIdx (int position){
        radioUC.clickBannerIdx.postValue(position);
    }
    //item拨打视频电话
    public void itemClickCallVideo(AdUserItemEntity adUserItemEntity){
        //逻辑判断。有可能挤掉账号 没有下线。但是本地已经清空
        UserDataEntity userDataEntity = model.readUserData();
        if(userDataEntity!=null && adUserItemEntity!=null && adUserItemEntity.getToImId()!=null){
            //视频拨打
            getCallingInvitedInfo(2, model.readUserData().getImUserId(), adUserItemEntity.getToImId());
        }

    }
    //音频播放
    public void itemClickPlayAudio(int position){
        if(lastClickAudioPlayer ==-1){
            lastClickAudioPlayer = position;
        }else{
            if(lastClickAudioPlayer!=position){
                radioItemsAdUser.get(lastClickAudioPlayer).isPlaying.set(false);
                lastClickAudioPlayer = position;
            }
        }
    }

    private Integer default_sex = null;
    private Disposable badioEvent;
    private Disposable radioadetailEvent;
    private Disposable taskTypeStatusEvent;
    private Disposable likeChangeEventDisposable, zoomInPictureEvent;
    private boolean isFirstComment = false;
    private boolean isFirstLike = false;

    public RadioViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
        initUserDate();
    }

    public void initUserDate() {
        userId = model.readUserData().getId();
        avatar = model.readUserData().getAvatar();
        certification = model.readUserData().getCertification();
    }

    @Override
    public void registerRxBus() {
        super.registerRxBus();
        //动态改变
        likeChangeEventDisposable = RxBus.getDefault().toObservable(LikeChangeEvent.class)
                .subscribe(event -> {
                    if (event != null) {
                        collectReLoad = true;
                    }
                });
        zoomInPictureEvent = RxBus.getDefault().toObservable(ZoomInPictureEvent.class)
                .subscribe(event -> {
                    radioUC.zoomInp.setValue(event.getDrawable());
                });
        badioEvent = RxBus.getDefault().toObservable(BadioEvent.class)
                .subscribe(event -> {
                    currentPage = 1;
                    getBroadcast(1);
                });
        radioadetailEvent = RxBus.getDefault().toObservable(RadioadetailEvent.class)
                .subscribe(event -> {
                    for (int i = 0; i < radioItems.size(); i++) {
                        if (radioItems.get(i) instanceof TrendItemViewModel) {
                            if (((TrendItemViewModel) radioItems.get(i)).newsEntityObservableField.get().getId() == event.getId()) {
                                switch (event.getType()) {//1:删除 2：评论关闭开启 3：报名 4：节目结束报名 5：评论  6：点赞
                                    case 1:
                                        radioItems.remove(i);
                                        break;
                                    case 2:
                                        ((TrendItemViewModel) radioItems.get(i)).newsEntityObservableField.get().getBroadcast().setIsComment(event.isComment);
                                        break;
                                    case 5:
                                        ((TrendItemViewModel) radioItems.get(i)).addComment(event.getId(), event.content, event.toUserId, event.toUserName, model.readUserData().getNickname());
                                        break;
                                    case 6:
                                        ((TrendItemViewModel) radioItems.get(i)).addGiveUser();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                });
        taskTypeStatusEvent = RxBus.getDefault().toObservable(TaskTypeStatusEvent.class)
                .subscribe(taskTypeStatusEvent -> {
                    isFirstComment = taskTypeStatusEvent.getDayCommentNews() == 0;
                    isFirstLike = taskTypeStatusEvent.getDayGiveNews() == 0;
                });
    }

    /**
     * @return void
     * @Desc TODO(跳转任务中心页面)
     * @author 彭石林
     * @parame []
     * @Date 2021/9/30
     */
    public void toTaskCenter() {
        RxBus.getDefault().post(new TaskMainTabEvent(false, true));
        //start(TaskCenterFragment.class.getCanonicalName());
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(likeChangeEventDisposable);
        RxSubscriptions.remove(badioEvent);
        RxSubscriptions.remove(radioadetailEvent);
        RxSubscriptions.remove(taskTypeStatusEvent);
        RxSubscriptions.remove(zoomInPictureEvent);
    }

    public void setType(Integer type) {
        this.type = type;
        CollectFlag = false;
        getBroadcast(1);
    }

    public void setCityId(Integer gameId, Integer cityId) {
        this.cityId = cityId;
        this.gameId = gameId;
        CollectFlag = false;
        getBroadcast(1);
    }

    public void setSexId(Integer sexId) {
        this.sexId = sexId;
        this.IsCollect = 0;
        CollectFlag = false;
        getBroadcast(1);
    }

    public void setIsCollect(Integer collect) {
        this.IsCollect = collect;
        this.sexId = null;
        CollectFlag = false;
        getBroadcast(1);
    }

    @Override
    public void loadDatas(int page) {
        if(page == 1) {
            getAdUserBanner();
            getAdListBanner();
        }
        try {
            if (IsCollect == null) {
                if (default_sex == null) {
                    default_sex = 2;
                    IsCollect = 1;
                    sexId = null;
                }
            }
        } catch (Exception e) {

        }
        //用户新触发了追踪事件。并且离开页面前。页面保持选项在查看追踪的人m
        if (collectReLoad && CollectFlag) {
            setIsCollect(1);
        } else {
            if (page == 1 && CollectFlag && IsCollect == 0) {
                sexId = null;
                CollectFlag = false;
                IsCollect = 1;
            }
            getBroadcast(page);
        }
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();

    }

    @Override
    public void onResume() {
        super.onResume();
        userDataEntity.set(model.readUserData());
        getArea();
    }

    public void getArea() {//地区
        List<ConfigItemEntity> cityConfig = model.readCityConfig();
        if (ObjectUtils.isEmpty(userDataEntity) || ObjectUtils.isEmpty(cityConfig) || ObjectUtils.isEmpty(userDataEntity.get()) || ObjectUtils.isEmpty(userDataEntity.get().getCityId())) {
            return ;
        }
        for (int i = 0; i < cityConfig.size(); i++) {
            if (userDataEntity.get().getCityId() == cityConfig.get(i).getId()) {
                area.set(cityConfig.get(i).getName());
            }
        }
    }

    /**
     * 电台首页
     */
    private void getBroadcast(int page) {
        try {
            GSYVideoManager.releaseAllVideos();
        } catch (Exception e) {

        }
        model.getBroadcastHome(sexId, cityId, gameId, null, IsCollect, type, page)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new BaseObserver<BaseDataResponse<BroadcastListEntity>>() {
                    @Override
                    public void onSuccess(BaseDataResponse<BroadcastListEntity> response) {
                        if (!CollectFlag) {
                            if (page == 1) {
                                radioItems.clear();
                            }
                        }
                        if (response.getData() != null) {
                            //真人集合
                            int realIndex = 0;
                            List<BroadcastEntity> listReal = response.getData().getRealData();
                            //机器人集合
                            List<BroadcastEntity> listUntrue = response.getData().getUntrueData();
                            //是否有追踪的人
                            Integer collectListEmpty = response.getData().getIsCollect();
                            //开始遍历次数
                            int position = 0;
                            if (IsCollect == 1 && page == 1 && CollectFlag) {
                                radioItems.clear();
                                CollectFlag = true;
                                IsCollect = 0;
                                if (ConfigManager.getInstance() != null && ConfigManager.getInstance().isMale()) {
                                    sexId = 0;
                                } else {
                                    sexId = 1;
                                }
                                String emptyText = null;
                                if (radioItems == null || radioItems.size() == 0) {
                                    if (collectListEmpty != null && collectListEmpty >= 1) {//有追踪的人
                                        emptyText = StringUtils.getString(R.string.playfun_radio_list_trace_empty2);
                                    } else {//没有追踪的人
                                        emptyText = StringUtils.getString(R.string.playfun_radio_list_trace_empty);
                                    }
                                } else {
                                    emptyText = StringUtils.getString(R.string.playfun_radio_list_trace_empty2);
                                }
                                // 动态
                                RadioTraceEmptyItemViewModel trendItemViewModel = new RadioTraceEmptyItemViewModel(RadioViewModel.this, emptyText);
                                trendItemViewModel.multiItemType(RadioRecycleType_trace);
                                radioItems.add(trendItemViewModel);
                            }
                            //机器人不为空
                            if (!ObjectUtils.isEmpty(listUntrue) && listUntrue.size() > 0) {
                                for (BroadcastEntity broadcastEntity : listUntrue) {
                                    position++;
                                    if (broadcastEntity.getNews() != null) {
//                                动态
                                        TrendItemViewModel trendItemViewModel = new TrendItemViewModel(RadioViewModel.this, broadcastEntity);
                                        trendItemViewModel.multiItemType(RadioRecycleType_New);
                                        radioItems.add(trendItemViewModel);
                                    }
                                    if (position % 2 == 0) {
                                        if (listReal.size() > realIndex + 1) {
                                            BroadcastEntity broadcastEntityReal = listReal.get(realIndex);
                                            if (broadcastEntityReal.getNews() != null) {
                                                //动态
                                                TrendItemViewModel trendItemViewModelReal = new TrendItemViewModel(RadioViewModel.this, broadcastEntityReal);
                                                trendItemViewModelReal.multiItemType(RadioRecycleType_New);
                                                radioItems.add(trendItemViewModelReal);
                                            }
                                            realIndex++;
                                        }
                                    }
                                }
                                if (realIndex == 0 && listReal.size() > 1) {
                                    for (BroadcastEntity broadcastEntity : listReal) {
                                        if (broadcastEntity.getNews() != null) {
                                            // 动态
                                            TrendItemViewModel trendItemViewModel = new TrendItemViewModel(RadioViewModel.this, broadcastEntity);
                                            trendItemViewModel.multiItemType(RadioRecycleType_New);
                                            radioItems.add(trendItemViewModel);
                                        }
                                    }
                                }
                            } else {
                                //真人集合不为空
                                if (!ObjectUtils.isEmpty(listReal) && listReal.size() > 0) {
                                    for (BroadcastEntity broadcastEntity : listReal) {
                                        if (broadcastEntity.getNews() != null) {
                                            // 动态
                                            TrendItemViewModel trendItemViewModel = new TrendItemViewModel(RadioViewModel.this, broadcastEntity);
                                            trendItemViewModel.multiItemType(RadioRecycleType_New);
                                            radioItems.add(trendItemViewModel);
                                        }
                                    }
                                } else {
                                    if (IsCollect == 1 && !CollectFlag) {
                                        CollectFlag = true;
                                        IsCollect = 0;
                                        if (ConfigManager.getInstance() != null && ConfigManager.getInstance().isMale()) {
                                            sexId = 0;
                                        } else {
                                            sexId = 1;
                                        }
                                        String emptyText = null;
                                        if (radioItems == null || radioItems.size() == 0) {
                                            if (collectListEmpty != null && collectListEmpty >= 1) {//有追踪的人
                                                emptyText = StringUtils.getString(R.string.playfun_radio_list_trace_empty2);
                                            } else {//没有追踪的人
                                                emptyText = StringUtils.getString(R.string.playfun_radio_list_trace_empty);
                                            }
                                        } else {
                                            emptyText = StringUtils.getString(R.string.playfun_radio_list_trace_empty2);
                                        }
                                        // 动态
                                        RadioTraceEmptyItemViewModel trendItemViewModel = new RadioTraceEmptyItemViewModel(RadioViewModel.this, emptyText);
                                        trendItemViewModel.multiItemType(RadioRecycleType_trace);
                                        radioItems.add(trendItemViewModel);
                                        getBroadcast(1);
                                    }
                                }
                            }
                            if (IsCollect == 1 && !CollectFlag) {
                                int listRealSize = ObjectUtils.isEmpty(listReal) ? 0 : listReal.size();
                                int listUntrueSize = ObjectUtils.isEmpty(listUntrue) ? 0 : listUntrue.size();

                                if ((listRealSize + listUntrueSize) < 2) {
                                    CollectFlag = true;
                                    IsCollect = 0;
                                    if (ConfigManager.getInstance() != null && ConfigManager.getInstance().isMale()) {
                                        sexId = 0;
                                    } else {
                                        sexId = 1;
                                    }
                                    String emptyText = null;
                                    if (radioItems == null || radioItems.size() == 0) {
                                        if (collectListEmpty != null && collectListEmpty >= 1) {//有追踪的人
                                            emptyText = StringUtils.getString(R.string.playfun_radio_list_trace_empty2);
                                        } else {//没有追踪的人
                                            emptyText = StringUtils.getString(R.string.playfun_radio_list_trace_empty);
                                        }
                                    } else {
                                        emptyText = StringUtils.getString(R.string.playfun_radio_list_trace_empty2);
                                    }
                                    // 动态
                                    RadioTraceEmptyItemViewModel trendItemViewModel = new RadioTraceEmptyItemViewModel(RadioViewModel.this, emptyText);
                                    trendItemViewModel.multiItemType(RadioRecycleType_trace);
                                    radioItems.add(trendItemViewModel);
                                    getBroadcast(1);
                                }
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                        stopRefreshOrLoadMore();
                        collectReLoad = false;
                    }
                });
    }
    //获取用户广告列表
    public void getAdUserBanner(){
        model.getUserAdList(1)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseDataResponse<AdUserBannerEntity>>(){
                    @Override
                    public void onSuccess(BaseDataResponse<AdUserBannerEntity> listBaseDataResponse) {
                        AdUserBannerEntity adUserBanner = listBaseDataResponse.getData();
                        if(adUserBanner!=null){
                            List<AdUserItemEntity> listData = adUserBanner.getDataList();
                            if(ObjectUtils.isNotEmpty(listData)){
                                ObservableList<RadioItemBannerVideoViewModel> listReal = new ObservableArrayList<>();
                                for (AdUserItemEntity adUserItemEntity : listData) {
                                    RadioItemBannerVideoViewModel radioItemBannerVideoViewModel = new RadioItemBannerVideoViewModel(RadioViewModel.this,adUserItemEntity);
                                    listReal.add(radioItemBannerVideoViewModel);
                                }
                                if(!listReal.isEmpty()){
                                    radioUC.startBannerEvent.call();
                                    if(radioItemsAdUser.size()>0){
                                        adapterAdUser.setItems(listReal);
                                        adapterAdUser.notifyItemRangeChanged(0,adapterAdUser.getItemCount()-1);
                                    }else{
                                        radioItemsAdUser.addAll(listReal);
                                    }

                                }
                            }
                        }
                    }
                    @Override
                    public void onError(RequestException e) {
                        super.onError(e);
                        Log.e("获取用户广告列表接口","异常原因："+e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //获取广告列表
    public void getAdListBanner(){
        model.getRadioAdBannerList(2)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseDataResponse<AdBannerEntity>>(){
                    @Override
                    public void onSuccess(BaseDataResponse<AdBannerEntity> adBannerEntityDataResponse) {
                        AdBannerEntity adBannerEntity = adBannerEntityDataResponse.getData();
                        if(adBannerEntity!=null){
                            List<AdItemEntity> listData = adBannerEntity.getDataList();
                            if(listData!=null && !listData.isEmpty()){
                                itemBannerEntity.set(listData);
                                itemBannerShow.set(true);
                            }else{
                                itemBannerShow.set(false);
                            }
                        }else{
                            itemBannerShow.set(false);
                        }
                    }
                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }

                });
    }

    //动态点赞
    public void newsGive(int posion) {
        model.newsGive(((TrendItemViewModel) radioItems.get(posion)).newsEntityObservableField.get().getId())
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ToastUtils.showShort(R.string.playfun_give_success);
                        if (isFirstLike) {
                            RxBus.getDefault().post(new TaskListEvent());
                        }
                        ((TrendItemViewModel) radioItems.get(posion)).addGiveUser();
                        AppContext.instance().logEvent(AppsFlyerEvent.Like);
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }
    //动态评论
    public void newsComment(Integer id, String content, Integer toUserId, String toUserName) {
        model.newsComment(id, content, toUserId)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ToastUtils.showShort(R.string.playfun_comment_success);
                        if (isFirstComment) {//每天第一次留言
                            RxBus.getDefault().post(new TaskListEvent());
                        }
                        for (int i = 0; i < radioItems.size(); i++) {
                            if (radioItems.get(i) instanceof TrendItemViewModel) {
                                if (id == ((TrendItemViewModel) radioItems.get(i)).newsEntityObservableField.get().getId()) {
                                    AppContext.instance().logEvent(AppsFlyerEvent.Message);
                                    ((TrendItemViewModel) radioItems.get(i)).addComment(id, content, toUserId, toUserName, model.readUserData().getNickname());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(RequestException e) {
                        if (e.getCode() == 10016) {
                            ToastUtils.showShort(StringUtils.getString(R.string.playfun_comment_close));
                            for (int i = 0; i < radioItems.size(); i++) {
                                if (radioItems.get(i) instanceof TrendItemViewModel) {
                                    if (id == ((TrendItemViewModel) radioItems.get(i)).newsEntityObservableField.get().getId()) {
                                        AppContext.instance().logEvent(AppsFlyerEvent.Message);
                                        ((TrendItemViewModel) radioItems.get(i)).newsEntityObservableField.get().getBroadcast().setIsComment(1);
                                    }
                                }
                            }
                        } else {
                            if (e.getMessage() != null) {
                                ToastUtils.showShort(e.getMessage());
                            } else {
                                ToastUtils.showShort(R.string.error_http_internal_server_error);
                            }

                            super.onError(e);
                        }
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //开启/关闭评论
    public void setComment(int posion, String type) {
        int broadcastId = ((TrendItemViewModel) radioItems.get(posion)).newsEntityObservableField.get().getBroadcast().getId();
        int isComment = ((TrendItemViewModel) radioItems.get(posion)).newsEntityObservableField.get().getBroadcast().getIsComment();
        model.setComment(broadcastId,
                isComment == 0 ? 1 : 0)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        if (type.equals(RadioRecycleType_New)){
                            ToastUtils.showShort(((TrendItemViewModel) radioItems.get(posion)).newsEntityObservableField.get().getBroadcast().getIsComment() == 1 ? StringUtils.getString(R.string.playfun_open_comment_success) : StringUtils.getString(R.string.playfun_close_success));
                            ((TrendItemViewModel) radioItems.get(posion)).newsEntityObservableField.get().getBroadcast().setIsComment(
                                    ((TrendItemViewModel) radioItems.get(posion)).newsEntityObservableField.get().getBroadcast().getIsComment() == 0 ? 1 : 0);
                        }
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //删除动态
    public void deleteNews(int posion) {
        model.deleteNews(((TrendItemViewModel) radioItems.get(posion)).newsEntityObservableField.get().getId())
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        radioItems.remove(posion);
                        try {
                            GSYVideoManager.releaseAllVideos();
                        } catch (Exception ignored) {

                        }
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }
    //屏蔽不喜欢的动态
    public void broadcastDisLike(int position){
        Map<String,Object> mapData = new HashMap<>();
        mapData.put("id",((TrendItemViewModel)radioItems.get(position)).newsEntityObservableField.get().getBroadcast().getId());
        model.broadcastDisLike(ApiUitl.getBody(GsonUtils.toJson(mapData)))
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        radioItems.remove(position);
                        try {
                            GSYVideoManager.releaseAllVideos();
                        } catch (Exception ignored) {

                        }
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });

    }

    //拨打语音、视频
    public void getCallingInvitedInfo(int callingType, String IMUserId, String toIMUserId) {
        if(callingType==1){
            //男女点击拨打语音
            AppContext.instance().logEvent(ConfigManager.getInstance().isMale() ? AppsFlyerEvent.call_voice_male : AppsFlyerEvent.call_voice_female);
        }else{
            //男女点击拨打视频
            AppContext.instance().logEvent(ConfigManager.getInstance().isMale() ? AppsFlyerEvent.call_video_male : AppsFlyerEvent.call_video_female);
        }
        model.callingInviteInfo(callingType, IMUserId, toIMUserId, 0)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseDataResponse<CallingInviteInfo>>() {
                    @Override
                    public void onSuccess(BaseDataResponse<CallingInviteInfo> callingInviteInfoBaseDataResponse) {
                        if (callingInviteInfoBaseDataResponse.getCode() == 2) {//對方忙線中
                            ToastUtils.showShort(R.string.custom_message_other_busy);
                            return;
                        }
                        if (callingInviteInfoBaseDataResponse.getCode() == 22001) {//游戏中
                            Toast.makeText(AppContext.instance(), R.string.playfun_in_game, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        CallingInviteInfo callingInviteInfo = callingInviteInfoBaseDataResponse.getData();
                        if (callingInviteInfo != null) {
                            Utils.tryStartCallSomeone(callingType, toIMUserId, callingInviteInfo.getRoomId());
                        }
                    }

                    @Override
                    public void onError(RequestException e) {
                        super.onError(e);
                        if (e != null) {
                            if (e.getCode() == 21001) {
                                radioUC.sendDialogViewEvent.call();
                            }
                        }
                    }


                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }





    public class UIChangeObservable {
        public SingleLiveEvent clickMore = new SingleLiveEvent<>();
        public SingleLiveEvent clickLike = new SingleLiveEvent<>();
        public SingleLiveEvent clickComment = new SingleLiveEvent<>();
        public SingleLiveEvent clickImage = new SingleLiveEvent<>();
        //追踪的人消息列表清空
        public SingleLiveEvent<Boolean> emptyLayoutShow = new SingleLiveEvent<>();
        public SingleLiveEvent<String> zoomInp = new SingleLiveEvent<>();
        //选择位置
        public SingleLiveEvent<Void> clickRegion = new SingleLiveEvent<>();
        //选择查询条目；追踪的人、男、女
        public SingleLiveEvent<Void> clickTacking = new SingleLiveEvent<>();
        //对方忙线
        public SingleLiveEvent otherBusy = new SingleLiveEvent<>();
        //钻石不足。唤起充值
        public SingleLiveEvent<Void> sendDialogViewEvent = new SingleLiveEvent<>();
        //点击切换banner
        public SingleLiveEvent<Integer> clickBannerIdx = new SingleLiveEvent<>();
        //开始播放banner
        public SingleLiveEvent<Void> startBannerEvent = new SingleLiveEvent<>();
    }

}