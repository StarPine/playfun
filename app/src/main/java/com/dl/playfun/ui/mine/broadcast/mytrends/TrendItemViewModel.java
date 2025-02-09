package com.dl.playfun.ui.mine.broadcast.mytrends;

import static com.dl.playfun.ui.mine.broadcast.mytrends.HeadItemViewModel.Type_New;
import static com.dl.playfun.ui.radio.radiohome.RadioViewModel.RadioRecycleType_New;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.entity.BaseUserBeanEntity;
import com.dl.playfun.entity.BroadcastBeanEntity;
import com.dl.playfun.entity.BroadcastEntity;
import com.dl.playfun.entity.CommentEntity;
import com.dl.playfun.entity.GiveUserBeanEntity;
import com.dl.playfun.entity.NewsEntity;
import com.dl.playfun.event.ZoomInPictureEvent;
import com.dl.playfun.manager.ConfigManager;
import com.dl.playfun.ui.mine.broadcast.myall.MyAllBroadcastViewModel;
import com.dl.playfun.ui.mine.broadcast.mytrends.trenddetail.TrendDetailFragment;
import com.dl.playfun.ui.mine.broadcast.mytrends.trenddetail.TrendDetailViewModel;
import com.dl.playfun.ui.radio.radiohome.RadioViewModel;
import com.dl.playfun.ui.userdetail.detail.UserDetailFragment;
import com.dl.playfun.ui.userdetail.userdynamic.UserDynamicViewModel;
import com.dl.playfun.utils.ExceptionReportUtils;
import com.dl.playfun.utils.ListUtils;
import com.dl.playfun.viewmodel.BaseViewModel;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * @author wulei
 */
public class TrendItemViewModel extends MultiItemViewModel<BaseViewModel> {
    //    public boolean isVip;
//    public int sex;
    public int userId;
    public String avatar;
    public boolean isSelf = false;
    private String gameChannel;

    public ObservableField<NewsEntity> newsEntityObservableField = new ObservableField<>();
    public ObservableField<Boolean> isShowComment = new ObservableField<>(false);
    //    public ObservableField<String> positonStr = new ObservableField<>();
    public ObservableField<Integer> pointPositon = new ObservableField<>(0);
    //ViewPager切换监听
    public BindingCommand<Integer> onPageSelectedCommand = new BindingCommand<>(new BindingConsumer<Integer>() {
        @Override
        public void call(Integer index) {
            try {
//                Log.i("debug", GsonUtils.toJson(newsEntityObservableField.get()));
                pointPositon.set(index);
//                positonStr.set(String.format("%s/%s", index + 1, newsEntityObservableField.get().getImages().size()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
    // 点赞
    public ObservableList<HeadItemViewModel> itemList = new ObservableArrayList<>();
    public ItemBinding<HeadItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_head);
    // 暂时无用
    public ObservableList<ImageItemViewModel> imageItemList = new ObservableArrayList<>();
    public ItemBinding<ImageItemViewModel> imageItemBinding = ItemBinding.of(BR.viewModel, R.layout.item_image);
    // 评论
    public ObservableList<CommentItemViewModel> commentItemList = new ObservableArrayList<>();
    public ItemBinding<CommentItemViewModel> commentItemBinding = ItemBinding.of(BR.viewModel, R.layout.item_comment);
    // 动态详细
    public BindingCommand detailClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            try {
                Bundle bundle = TrendDetailFragment.getStartBundle(newsEntityObservableField.get().getId());
                viewModel.start(TrendDetailFragment.class.getCanonicalName(), bundle);
                GSYVideoManager.releaseAllVideos();
            } catch (Exception e) {
                ExceptionReportUtils.report(e);
            }
        }
    });
    public BindingCommand imageClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            //放大图片
            if (!ListUtils.isEmpty(newsEntityObservableField.get().getImages())) {
                RxBus.getDefault().post(new ZoomInPictureEvent(newsEntityObservableField.get().getImages().get(0)));
            } else {
                RxBus.getDefault().post(new ZoomInPictureEvent(newsEntityObservableField.get().getUser().getAvatar()));
            }

        }
    });

    //更多的点击事件
    public BindingCommand moreClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            try {
                if (viewModel instanceof MyTrendsViewModel) {
                    int position = ((MyTrendsViewModel) viewModel).observableList.indexOf(TrendItemViewModel.this);
                    ((MyTrendsViewModel) viewModel).uc.clickMore.setValue(position);
                } else if (viewModel instanceof RadioViewModel) {
                    int position = ((RadioViewModel) viewModel).radioItems.indexOf(TrendItemViewModel.this);
                    Map<String, String> data = new HashMap<>();
                    data.put("position", String.valueOf(position));
                    data.put("type", RadioRecycleType_New);
                    data.put("broadcastId", String.valueOf(newsEntityObservableField.get().getBroadcast().getId()));
                    ((RadioViewModel) viewModel).radioUC.clickMore.setValue(data);
                } else if (viewModel instanceof UserDynamicViewModel) {
                    int position = ((UserDynamicViewModel) viewModel).observableList.indexOf(TrendItemViewModel.this);
                    Map<String, String> data = new HashMap<>();
                    data.put("position", String.valueOf(position));
                    data.put("type", RadioRecycleType_New);
                    data.put("broadcastId", String.valueOf(newsEntityObservableField.get().getBroadcast().getId()));
                    ((UserDynamicViewModel) viewModel).uc.clickMore.setValue(data);
                } else if (viewModel instanceof MyAllBroadcastViewModel) {
                    int position = ((MyAllBroadcastViewModel) viewModel).observableList.indexOf(TrendItemViewModel.this);
                    Map<String, String> data = new HashMap<>();
                    data.put("position", String.valueOf(position));
                    data.put("type", RadioRecycleType_New);
                    data.put("broadcastId", String.valueOf(newsEntityObservableField.get().getBroadcast().getId()));
                    ((MyAllBroadcastViewModel) viewModel).uc.clickMore.setValue(data);
                }
            } catch (Exception e) {
                ExceptionReportUtils.report(e);
            }
        }
    });
    //点赞点击事件
    public BindingCommand likeClick = new BindingCommand(() -> {
        try {
            if (viewModel instanceof MyTrendsViewModel) {
                int position = ((MyTrendsViewModel) viewModel).observableList.indexOf(TrendItemViewModel.this);
                ((MyTrendsViewModel) viewModel).uc.clickLike.setValue(position);
            } else if (viewModel instanceof RadioViewModel) {
                int position = ((RadioViewModel) viewModel).radioItems.indexOf(TrendItemViewModel.this);
                Map<String, String> data = new HashMap<>();
                data.put("position", String.valueOf(position));
                data.put("type", RadioRecycleType_New);
                ((RadioViewModel) viewModel).radioUC.clickLike.setValue(data);
            } else if (viewModel instanceof UserDynamicViewModel) {
                int position = ((UserDynamicViewModel) viewModel).observableList.indexOf(TrendItemViewModel.this);
                Map<String, String> data = new HashMap<>();
                data.put("position", String.valueOf(position));
                data.put("type", RadioRecycleType_New);
                ((UserDynamicViewModel) viewModel).uc.clickLike.setValue(data);
            } else if (viewModel instanceof MyAllBroadcastViewModel) {
                int position = ((MyAllBroadcastViewModel) viewModel).observableList.indexOf(TrendItemViewModel.this);
                Map<String, String> data = new HashMap<>();
                data.put("position", String.valueOf(position));
                data.put("type", RadioRecycleType_New);
                ((MyAllBroadcastViewModel) viewModel).uc.clickLike.setValue(data);
            }
        } catch (Exception e) {
            ExceptionReportUtils.report(e);
        }
    });
    //评论点击事件
    public BindingCommand commentClick = new BindingCommand(() -> {
        try {
            if (newsEntityObservableField.get().getBroadcast().getIsComment() == 1) {
                ToastUtils.showShort(R.string.playfun_comment_close);
                return;
            }
            if (viewModel instanceof MyTrendsViewModel) {
                if (((MyTrendsViewModel) viewModel).userId == newsEntityObservableField.get().getUser().getId()) {
                    ToastUtils.showShort(R.string.playfun_self_ont_comment_broadcast);
                } else {
                    Map<String, String> data = new HashMap<>();
                    data.put("id", String.valueOf(newsEntityObservableField.get().getId()));
                    data.put("toUseriD", null);
                    data.put("toUserName", null);
                    ((MyTrendsViewModel) viewModel).uc.clickComment.setValue(data);
                }
            } else if (viewModel instanceof RadioViewModel) {
                if (((RadioViewModel) viewModel).userId == newsEntityObservableField.get().getUser().getId()) {
                    ToastUtils.showShort(R.string.playfun_self_ont_comment_broadcast);
                } else {
                    Map<String, String> data = new HashMap<>();
                    data.put("id", String.valueOf(newsEntityObservableField.get().getId()));
                    data.put("toUseriD", null);
                    data.put("type", RadioRecycleType_New);
                    data.put("toUserName", null);
                    ((RadioViewModel) viewModel).radioUC.clickComment.setValue(data);
                }
            } else if (viewModel instanceof UserDynamicViewModel) {
                if (((UserDynamicViewModel) viewModel).userId == newsEntityObservableField.get().getUser().getId()) {
                    ToastUtils.showShort(R.string.playfun_self_ont_comment_broadcast);
                } else {
                    Map<String, String> data = new HashMap<>();
                    data.put("id", String.valueOf(newsEntityObservableField.get().getId()));
                    data.put("toUseriD", null);
                    data.put("type", RadioRecycleType_New);
                    data.put("toUserName", null);
                    ((UserDynamicViewModel) viewModel).uc.clickComment.setValue(data);
                }
            } else if (viewModel instanceof MyAllBroadcastViewModel) {
                if (((MyAllBroadcastViewModel) viewModel).userId == newsEntityObservableField.get().getUser().getId()) {
                    ToastUtils.showShort(R.string.playfun_self_ont_comment_broadcast);
                } else {
                    Map<String, String> data = new HashMap<>();
                    data.put("id", String.valueOf(newsEntityObservableField.get().getId()));
                    data.put("toUseriD", null);
                    data.put("type", RadioRecycleType_New);
                    data.put("toUserName", null);
                    ((MyAllBroadcastViewModel) viewModel).uc.clickComment.setValue(data);
                }
            }
        } catch (Exception e) {
            ExceptionReportUtils.report(e);
        }
    });
    public BindingCommand avatarClick = new BindingCommand(() -> {
        try {
            if (String.valueOf(newsEntityObservableField.get().getUser().getId()).equals(ConfigManager.getInstance().getUserId())) {
                return;
            }
            Bundle bundle = UserDetailFragment.getStartBundle(newsEntityObservableField.get().getUser().getId());
            viewModel.start(UserDetailFragment.class.getCanonicalName(), bundle);
            GSYVideoManager.releaseAllVideos();
        } catch (Exception e) {
            ExceptionReportUtils.report(e);
        }
    });

    public TrendItemViewModel(@NonNull BaseViewModel viewModel, NewsEntity newsEntity) {
        super(viewModel);
        this.newsEntityObservableField.set(newsEntity);
        if (viewModel instanceof MyTrendsViewModel) {
            isSelf = true;
        }
        init();
    }

    public TrendItemViewModel(@NonNull BaseViewModel viewModel, BroadcastEntity broadcastEntity) {
        super(viewModel);
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setId(broadcastEntity.getNews().getId());
        newsEntity.setCreatedAt(broadcastEntity.getCreatedAt());
        newsEntity.setContent(broadcastEntity.getNews().getContent());
        newsEntity.setIsGive(broadcastEntity.getNews().getIsGive());
        newsEntity.setComment(broadcastEntity.getNews().getComment());
        newsEntity.setImages(broadcastEntity.getNews().getImages());
        newsEntity.setUserId(broadcastEntity.getUserId());
        newsEntity.setVideo(broadcastEntity.getNews().getVideo());
        newsEntity.setNewsType(broadcastEntity.getNews().getNewsType());
        newsEntity.setCommentNumber(broadcastEntity.getNews().getCommentNumber());
        //自己IMid  对方IM id
        newsEntity.setImUserId(broadcastEntity.getImUserId());
        newsEntity.setImToUserId(broadcastEntity.getImToUserId());
        BaseUserBeanEntity userBean = new BaseUserBeanEntity();
        userBean.setAvatar(broadcastEntity.getAvatar());
        userBean.setId(broadcastEntity.getUserId());
        userBean.setIsVip(broadcastEntity.getIsVip());
        userBean.setNickname(broadcastEntity.getNickname());
        userBean.setSex(broadcastEntity.getSex());
        userBean.setCertification(broadcastEntity.getCertification());
        newsEntity.setUser(userBean);

        if (broadcastEntity.getNews().getGive_user() != null) {
            List<GiveUserBeanEntity> giveUserBeanList = new ArrayList<>();
            for (int i = 0; i < broadcastEntity.getNews().getGive_user().size(); i++) {
                giveUserBeanList.add(new GiveUserBeanEntity(broadcastEntity.getNews().getGive_user().get(i).getIdX(), broadcastEntity.getNews().getGive_user().get(i).getAvatar()));
            }
            newsEntity.setGive_user(giveUserBeanList);
        }

        BroadcastBeanEntity broadcastBean = new BroadcastBeanEntity();
        broadcastBean.setId(broadcastEntity.getId());
        broadcastBean.setIsComment(broadcastEntity.getIsComment());
        broadcastBean.setGiveCount(broadcastEntity.getGiveCount());
        newsEntity.setBroadcast(broadcastBean);

        newsEntityObservableField.set(newsEntity);
        gameChannel = broadcastEntity.getGameChannel();
        init();
    }


    public String gameUrl(){
        return ConfigManager.getInstance().getGameUrl(gameChannel);
    }

    public int isRealManVisible() {
        if (newsEntityObservableField.get().getUser().getIsVip() != 1) {
            if (newsEntityObservableField.get().getUser().getCertification() == 1) {
                return View.VISIBLE;
            } else {
                return View.GONE;
            }
        }else {
            return View.GONE;
        }
    }

    private void init() {
        if (viewModel instanceof MyTrendsViewModel) {
//            isVip = ((MyTrendsViewModel) viewModel).isVip;
//            sex = ((MyTrendsViewModel) viewModel).sex;
            userId = ((MyTrendsViewModel) viewModel).userId;
            avatar = ((MyTrendsViewModel) viewModel).avatar;
        } else if (viewModel instanceof RadioViewModel) {
//            isVip = ((RadioViewModel) viewModel).isVip;
//            sex = ((RadioViewModel) viewModel).sex;
            userId = ((RadioViewModel) viewModel).userId;
        } else if (viewModel instanceof MyAllBroadcastViewModel) {
//            isVip = ((RadioViewModel) viewModel).isVip;
//            sex = ((RadioViewModel) viewModel).sex;
            userId = ((MyAllBroadcastViewModel) viewModel).userId;
        }


        if (newsEntityObservableField.get().getGive_user() != null) {
            for (int i = 0; i < newsEntityObservableField.get().getGive_user().size(); i++) {
                if (i < 14) {
                    HeadItemViewModel item = new HeadItemViewModel(viewModel, newsEntityObservableField.get().getGive_user().get(i).getAvatar(),
                            newsEntityObservableField.get().getGive_user().get(i).getId(),
                            newsEntityObservableField.get().getGive_user().size() - 14,
                            newsEntityObservableField.get().getGive_user().get(i).getSex(),
                            Type_New, newsEntityObservableField.get().getId()
                    );
                    itemList.add(item);
                }
            }
        }


        if (newsEntityObservableField.get().getComment() != null) {
            for (int i = 0; i < newsEntityObservableField.get().getComment().size(); i++) {
                if (i < 5) {
                    CommentItemViewModel commentItemViewModel = new CommentItemViewModel(viewModel, newsEntityObservableField.get().getComment().get(i),
                            newsEntityObservableField.get().getId(), RadioRecycleType_New, newsEntityObservableField.get().getUser().getId() == userId, false);
                    commentItemList.add(commentItemViewModel);
                }
            }
            if (newsEntityObservableField.get().getComment().size() > 5) {
                CommentItemViewModel commentItemViewModel = new CommentItemViewModel(viewModel, newsEntityObservableField.get().getComment().get(0),
                        newsEntityObservableField.get().getId(), RadioRecycleType_New,
                        newsEntityObservableField.get().getUser().getId() == userId, true);
                commentItemList.add(commentItemViewModel);
            }
        }

        if (!ListUtils.isEmpty(newsEntityObservableField.get().getComment()) && viewModel instanceof TrendDetailViewModel) {
            isShowComment.set(true);
        } else {
            isShowComment.set(false);
        }

    }

    public void addGiveUser() {
        if (newsEntityObservableField.get().getGive_user() == null) {
            newsEntityObservableField.get().setGive_user(new ArrayList<>());
        }
        GiveUserBeanEntity giveUserBeanEntity = new GiveUserBeanEntity(userId, avatar);
        newsEntityObservableField.get().getGive_user().add(giveUserBeanEntity);
        newsEntityObservableField.get().setGiveSize(newsEntityObservableField.get().getGive_user().size());
        newsEntityObservableField.get().setIsGive(1);
        newsEntityObservableField.get().getBroadcast().setGiveCount(newsEntityObservableField.get().getBroadcast().getGiveCount() + 1);
        HeadItemViewModel item = new HeadItemViewModel(viewModel, avatar, userId,
                ConfigManager.getInstance().getAppRepository().readUserData().getSex(),
                newsEntityObservableField.get().getGiveCount() - 14,
                Type_New, newsEntityObservableField.get().getId()
        );
        itemList.add(item);

    }

    public void addComment(Integer id, String content, Integer toUserId, String toUserName, String userName) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setContent(content);
        commentEntity.setId(id);
        commentEntity.setUserId(userId);
        CommentEntity.UserBean userBean = new CommentEntity.UserBean();
        userBean.setId(userId);
        userBean.setNickname(userName);
        commentEntity.setUser(userBean);
        if (toUserName != null) {
            CommentEntity.TouserBean touserBean = new CommentEntity.TouserBean();
            touserBean.setId(toUserId);
            touserBean.setNickname(toUserName);
            commentEntity.setTouser(touserBean);
        }
        if (newsEntityObservableField.get().getComment() == null) {
            List<CommentEntity> commentEntities = new ArrayList<>();
            commentEntities.add(commentEntity);
            newsEntityObservableField.get().setComment(commentEntities);
        } else {
            newsEntityObservableField.get().getComment().add(commentEntity);
        }
        if (newsEntityObservableField.get().getComment().size() > 5) {
            if (newsEntityObservableField.get().getComment().size() < 7) {
                CommentItemViewModel commentItemViewModel = new CommentItemViewModel(viewModel, newsEntityObservableField.get().getComment().get(0),
                        newsEntityObservableField.get().getId(), RadioRecycleType_New,
                        newsEntityObservableField.get().getUser().getId() == userId, true);
                commentItemList.add(commentItemViewModel);
            }
        } else {
            CommentItemViewModel commentItemViewModel = new CommentItemViewModel(viewModel, commentEntity, newsEntityObservableField.get().getId(),
                    RadioRecycleType_New, newsEntityObservableField.get().getUser().getId() == userId, false);
            commentItemList.add(commentItemViewModel);
        }
        if (ListUtils.isEmpty(newsEntityObservableField.get().getComment())) {
            isShowComment.set(false);
        } else {
            isShowComment.set(true);
        }
    }

    public Integer getPosition() {
        int position = 0;
        if (viewModel instanceof MyTrendsViewModel) {
            position = ((MyTrendsViewModel) viewModel).observableList.indexOf(TrendItemViewModel.this);
        } else if (viewModel instanceof RadioViewModel) {
            position = ((RadioViewModel) viewModel).radioItems.indexOf(TrendItemViewModel.this);
        } else if (viewModel instanceof UserDynamicViewModel) {
            position = ((UserDynamicViewModel) viewModel).observableList.indexOf(TrendItemViewModel.this);
        } else if (viewModel instanceof MyAllBroadcastViewModel) {
            position = ((MyAllBroadcastViewModel) viewModel).observableList.indexOf(TrendItemViewModel.this);
        }
        return position;
    }

    public Drawable onLineColor(BroadcastEntity broadcastEntity){
        if (broadcastEntity == null)return null;
        if (broadcastEntity.getCallingStatus() == 0){
            if (broadcastEntity.getIsOnline() == 1) {
                return AppContext.instance().getResources().getDrawable(R.drawable.mine_radius3);
            }
        }else {
            return AppContext.instance().getResources().getDrawable(R.drawable.mine_radius2);
        }
        return AppContext.instance().getResources().getDrawable(R.drawable.mine_radius2);
    }

}
