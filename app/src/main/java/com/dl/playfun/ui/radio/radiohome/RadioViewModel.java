package com.dl.playfun.ui.radio.radiohome;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.app.AppsFlyerEvent;
import com.dl.playfun.data.AppRepository;
import com.dl.playfun.data.source.http.exception.RequestException;
import com.dl.playfun.data.source.http.observer.BaseObserver;
import com.dl.playfun.data.source.http.response.BaseDataResponse;
import com.dl.playfun.data.source.http.response.BaseResponse;
import com.dl.playfun.entity.AdItemEntity;
import com.dl.playfun.entity.BroadcastEntity;
import com.dl.playfun.entity.BroadcastListEntity;
import com.dl.playfun.entity.ConfigItemEntity;
import com.dl.playfun.entity.MessageTagEntity;
import com.dl.playfun.entity.RadioTwoFilterItemEntity;
import com.dl.playfun.entity.UserDataEntity;
import com.dl.playfun.event.BadioEvent;
import com.dl.playfun.event.LikeChangeEvent;
import com.dl.playfun.event.MainTabEvent;
import com.dl.playfun.event.RadioadetailEvent;
import com.dl.playfun.event.TaskListEvent;
import com.dl.playfun.event.TaskMainTabEvent;
import com.dl.playfun.event.TaskTypeStatusEvent;
import com.dl.playfun.event.ZoomInPictureEvent;
import com.dl.playfun.manager.ConfigManager;
import com.dl.playfun.ui.mine.broadcast.myprogram.ProgramItemViewModel;
import com.dl.playfun.ui.mine.broadcast.mytrends.TrendItemViewModel;
import com.dl.playfun.ui.mine.wallet.WalletFragment;
import com.dl.playfun.utils.FileUploadUtils;
import com.dl.playfun.utils.Utils;
import com.dl.playfun.viewmodel.BaseRefreshViewModel;
import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.dl.playfun.ui.radio.issuanceprogram.IssuanceProgramFragment;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
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
    //vip充值成功回调
    public boolean EventVipSuccess = false;
    public static Integer SignWinningDay = -1;
    public static final String RadioRecycleType_New = "new";
    public static final String RadioRecycleType_Topical = "topical";
    public static final String RadioRecycleType_trace = "emptyTrace";
    private static final String TAG = "签到领取会员";
    private final int consumeImmediately = 0;
    private final Integer pay_good_day = 7;
    //推荐用户弹窗
    //推荐用户弹窗
    public ObservableField<Boolean> isShowMessageTag = new ObservableField<>(false);
    public ObservableField<MessageTagEntity> messageTagEntity = new ObservableField<>();
    public ObservableField<String> countDownTimerUi = new ObservableField<>();
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
    public ObservableField<List<AdItemEntity>> adItemEntityObservableField = new ObservableField<>(new ArrayList<>());

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
            } else if (RadioRecycleType_Topical.equals(itemType)) {
//                设置topical
                itemBinding.set(BR.viewModel, R.layout.item_program);
            } else if (RadioRecycleType_trace.equals(itemType)) {
                //设置看追踪列表为空
                itemBinding.set(BR.viewModel, R.layout.item_radio_trace_empty);
            }
        }
    });
    public BindingCommand headerImageOnClick = new BindingCommand(() -> {
        //跳转到我的页面
        RxBus.getDefault().post(new MainTabEvent("mine"));
    });
    public BindingCommand moneyOnClick = new BindingCommand(() -> {
        //打开电子钱包
        start(WalletFragment.class.getCanonicalName());
    });


    /**
     * 发布按钮的点击事件
     */
    public BindingCommand publishOnClickCommand = new BindingCommand(() -> {
        radioUC.programSubject.call();
    });
    /*谷歌支付*/

    private Integer default_sex = null;
    private Disposable badioEvent;
    private Disposable radioadetailEvent;
    private Disposable UserUpdateVipEvent, taskTypeStatusEvent;
    private String orderNumber = null;
    private String google_goods_id = null;
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

    //跳转发布界面
    public BindingCommand toProgramVIew = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            start(IssuanceProgramFragment.class.getCanonicalName());
        }
    });


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
                        if (event.getRadioaType().equals(RadioRecycleType_Topical)) {
                            if (radioItems.get(i) instanceof ProgramItemViewModel) {
                                if (((ProgramItemViewModel) radioItems.get(i)).topicalListEntityObservableField.get().getId() == event.getId()) {
                                    switch (event.getType()) {//1:删除 2：评论关闭开启 3：报名成功 4：节目结束报名 5：评论  6：点赞
                                        case 1:
                                            radioItems.remove(i);
                                            break;
                                        case 2:
                                            ((ProgramItemViewModel) radioItems.get(i)).topicalListEntityObservableField.get().getBroadcast().setIsComment(event.isComment);
                                            break;
                                        case 3:
                                            ((ProgramItemViewModel) radioItems.get(i)).report();
                                            break;
                                        case 4:
                                            ((ProgramItemViewModel) radioItems.get(i)).topicalListEntityObservableField.get().setIsEnd(1);
                                            break;
                                        case 5:
                                            ((ProgramItemViewModel) radioItems.get(i)).addComment(event.getId(), event.content, event.toUserId, event.toUserName, model.readUserData().getNickname());
                                            break;
                                        case 6:
                                            ((ProgramItemViewModel) radioItems.get(i)).addGiveUser();
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        } else {
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
                    }
                });
        UserUpdateVipEvent = RxBus.getDefault().toObservable(com.dl.playfun.event.UserUpdateVipEvent.class)
                .subscribe(userUpdateVipEvent -> {
                    EventVipSuccess = true;
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

    private void recommendMsg(String userId, String num, long timeOut) {
        AppContext.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> maps = new HashMap<>();
                    maps.put("userId", userId);
                    maps.put("date", Utils.formatday.format(new Date()));
                    maps.put("num", num);
                    model.saveMessageTagUser(maps);
                    AppContext.sUiThreadHandler.removeCallbacks(this);
                } catch (Exception e) {
                    AppContext.sUiThreadHandler.removeCallbacks(this);
                }
            }
        }, timeOut);
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

    //初始化
    public void loadHttpData() {
        super.onEnterAnimationEnd();
        loadDatas(1);
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
        if (ObjectUtils.isEmpty(userDataEntity) || ObjectUtils.isEmpty(cityConfig) || ObjectUtils.isEmpty(userDataEntity.get().getCityId())) {
            return ;
        }
        for (int i = 0; i < cityConfig.size(); i++) {
            if (userDataEntity.get().getCityId() == cityConfig.get(i).getId()) {
                area.set(cityConfig.get(i).getName());
            }
        }
    }

    public void loadGameCity() {
        model.getGameCity()
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new BaseObserver<BaseDataResponse<List<RadioTwoFilterItemEntity>>>() {
                    @Override
                    public void onSuccess(BaseDataResponse<List<RadioTwoFilterItemEntity>> listBaseDataResponse) {
                        radioUC.getRadioTwoFilterItemEntity.setValue(listBaseDataResponse.getData());
                    }
                });
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
                                    } else {
//                                节目
                                        ProgramItemViewModel programItemViewModel = new ProgramItemViewModel(RadioViewModel.this, broadcastEntity);
                                        programItemViewModel.multiItemType(RadioRecycleType_Topical);
                                        radioItems.add(programItemViewModel);
                                    }
                                    if (position % 2 == 0) {
                                        if (listReal.size() > realIndex + 1) {
                                            BroadcastEntity broadcastEntityReal = listReal.get(realIndex);
                                            if (broadcastEntityReal.getNews() != null) {
                                                //动态
                                                TrendItemViewModel trendItemViewModelReal = new TrendItemViewModel(RadioViewModel.this, broadcastEntityReal);
                                                trendItemViewModelReal.multiItemType(RadioRecycleType_New);
                                                radioItems.add(trendItemViewModelReal);
                                            } else {
                                                // 节目
                                                ProgramItemViewModel programItemViewModelReal = new ProgramItemViewModel(RadioViewModel.this, broadcastEntityReal);
                                                programItemViewModelReal.multiItemType(RadioRecycleType_Topical);
                                                radioItems.add(programItemViewModelReal);
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
                                        } else {
                                            //节目
                                            ProgramItemViewModel programItemViewModel = new ProgramItemViewModel(RadioViewModel.this, broadcastEntity);
                                            programItemViewModel.multiItemType(RadioRecycleType_Topical);
                                            radioItems.add(programItemViewModel);
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
                                        } else {
                                            //节目
                                            ProgramItemViewModel programItemViewModel = new ProgramItemViewModel(RadioViewModel.this, broadcastEntity);
                                            programItemViewModel.multiItemType(RadioRecycleType_Topical);
                                            radioItems.add(programItemViewModel);
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

    //节目点赞
    public void topicalGive(int posion) {
        model.TopicalGive(((ProgramItemViewModel) radioItems.get(posion)).topicalListEntityObservableField.get().getId())
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ToastUtils.showShort(R.string.playfun_give_success);

                        ((ProgramItemViewModel) radioItems.get(posion)).addGiveUser();
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

    //节目评论
    public void topicalComment(Integer id, String content, Integer toUserId, String toUserName) {
        model.topicalComment(id, content, toUserId)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ToastUtils.showShort(R.string.playfun_comment_success);
                        for (int i = 0; i < radioItems.size(); i++) {
                            if (radioItems.get(i) instanceof ProgramItemViewModel) {
                                if (id == ((ProgramItemViewModel) radioItems.get(i)).topicalListEntityObservableField.get().getId()) {
                                    AppContext.instance().logEvent(AppsFlyerEvent.Message);
                                    ((ProgramItemViewModel) radioItems.get(i)).addComment(id, content, toUserId, toUserName, model.readUserData().getNickname());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(RequestException e) {
                        if (e.getCode() == 10016) {
                            ToastUtils.showShort(StringUtils.getString(R.string.playfun_comment_close));
                            for (int i = 0; i < radioItems.size(); i++) {
                                if (radioItems.get(i) instanceof ProgramItemViewModel) {
                                    if (id == ((ProgramItemViewModel) radioItems.get(i)).topicalListEntityObservableField.get().getId()) {
                                        AppContext.instance().logEvent(AppsFlyerEvent.Message);
                                        ((ProgramItemViewModel) radioItems.get(i)).topicalListEntityObservableField.get().getBroadcast().setIsComment(1);
                                    }
                                }
                            }
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

    //节目结束报名
    public void TopicalFinish(int posion) {
        model.TopicalFinish(((ProgramItemViewModel) radioItems.get(posion)).topicalListEntityObservableField.get().getId())
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ((ProgramItemViewModel) radioItems.get(posion)).topicalListEntityObservableField.get().setIsEnd(1);
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //我要报名
    public void report(int posion, String imags) {
        model.singUp(((ProgramItemViewModel) radioItems.get(posion)).topicalListEntityObservableField.get().getId(), imags)
                .doOnSubscribe(this)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribe(new BaseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        dismissHUD();
                        ToastUtils.showShort(R.string.playfun_sign_up_success);
                        ((ProgramItemViewModel) radioItems.get(posion)).report();
                        AppContext.instance().logEvent(AppsFlyerEvent.Apply);
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    public void imagUpload(String filePath, int posion) {
        Observable.just(filePath)
                .doOnSubscribe(this)
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(disposable -> showHUD())
                .subscribeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return FileUploadUtils.ossUploadFile("radio/", FileUploadUtils.FILE_TYPE_IMAGE, s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String fileKey) {
                        dismissHUD();
                        report(posion, fileKey);
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissHUD();
                        ToastUtils.showShort(R.string.playfun_upload_failed);
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //开启/关闭评论
    public void setComment(int posion, String type) {
        int broadcastId;
        int isComment;
        if (type.equals(RadioRecycleType_Topical)) {
            broadcastId = ((ProgramItemViewModel) radioItems.get(posion)).topicalListEntityObservableField.get().getBroadcast().getId();
            isComment = ((ProgramItemViewModel) radioItems.get(posion)).topicalListEntityObservableField.get().getBroadcast().getIsComment();
        } else {
            broadcastId = ((TrendItemViewModel) radioItems.get(posion)).newsEntityObservableField.get().getBroadcast().getId();
            isComment = ((TrendItemViewModel) radioItems.get(posion)).newsEntityObservableField.get().getBroadcast().getIsComment();
        }
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
                        if (type.equals(RadioRecycleType_Topical)) {
                            ToastUtils.showShort(((ProgramItemViewModel) radioItems.get(posion)).topicalListEntityObservableField.get().getBroadcast().getIsComment() == 1 ? StringUtils.getString(R.string.playfun_open_comment_success) : StringUtils.getString(R.string.playfun_close_success));
                            ((ProgramItemViewModel) radioItems.get(posion)).topicalListEntityObservableField.get().getBroadcast().setIsComment(
                                    ((ProgramItemViewModel) radioItems.get(posion)).topicalListEntityObservableField.get().getBroadcast().getIsComment() == 0 ? 1 : 0);
                        } else {
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
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onComplete() {
                        dismissHUD();
                    }
                });
    }

    //删除节目
    public void deleteTopical(int posion) {
        model.deleteTopical(((ProgramItemViewModel) radioItems.get(posion)).topicalListEntityObservableField.get().getId())
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
                        } catch (Exception e) {

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
        public SingleLiveEvent clickSignUp = new SingleLiveEvent<>();
        public SingleLiveEvent clickCheck = new SingleLiveEvent<>();
        public SingleLiveEvent programSubject = new SingleLiveEvent<>();
        public SingleLiveEvent clickImage = new SingleLiveEvent<>();
        public SingleLiveEvent<Boolean> loadLast = new SingleLiveEvent<>();
        //追踪的人消息列表清空
        public SingleLiveEvent<Boolean> emptyLayoutShow = new SingleLiveEvent<>();
        public SingleLiveEvent<String> zoomInp = new SingleLiveEvent<>();
        public SingleLiveEvent<List<RadioTwoFilterItemEntity>> getRadioTwoFilterItemEntity = new SingleLiveEvent<>();

    }
    /*=====谷歌支付核心代码=====*/


}