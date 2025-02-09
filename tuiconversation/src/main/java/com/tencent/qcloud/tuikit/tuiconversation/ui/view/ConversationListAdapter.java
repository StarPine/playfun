package com.tencent.qcloud.tuikit.tuiconversation.ui.view;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMSDKListener;
import com.tencent.imsdk.v2.V2TIMUserStatus;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuicore.util.ScreenUtil;
import com.tencent.qcloud.tuikit.tuiconversation.R;
import com.tencent.qcloud.tuikit.tuiconversation.TUIConversationService;
import com.tencent.qcloud.tuikit.tuiconversation.bean.ConversationInfo;
import com.tencent.qcloud.tuikit.tuiconversation.ui.interfaces.IConversationListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationListAdapter extends RecyclerView.Adapter implements IConversationListAdapter {

    public static final int ITEM_TYPE_HEADER_SEARCH = 101;
    public static final int ITEM_TYPE_FOOTER_LOADING = -99;
    public static final int HEADER_COUNT = 1;
    public static final int FOOTER_COUNT = 1;
    public static final int SELECT_COUNT = 1;
    public static final int SELECT_LABEL_COUNT = 1;

    private boolean mHasShowUnreadDot = true;
    private int mItemAvatarRadius = ScreenUtil.getPxByDp(5);
    private int mTopTextSize;
    private int mBottomTextSize;
    private int mDateTextSize;
    protected List<ConversationInfo> mDataSource = new ArrayList<>();
    private Map<String, ConversationInfo> conversationInfoMap;
    private ArrayList<String> subscribe;
    private V2TIMSDKListener listener;
    private ConversationListLayout.OnItemClickListener mOnItemClickListener;
    private ConversationListLayout.OnItemLongClickListener mOnItemLongClickListener;

    //彭石林新增
    public ConversationListLayout.OnItemAvatarClickListener mOnItemAvatarClickListener;
    public ConversationListLayout.BanConversationDelListener mBanConversationDelListener;
    private ConversationListLayout mRecycleView;

    //消息转发
    private final HashMap<String, Boolean> mSelectedPositions = new HashMap<>();
    private boolean isShowMultiSelectCheckBox = false;
    private boolean isForwardFragment = false;

    private boolean mIsLoading = false;

    // 长按高亮显示
    private boolean isClick = false;
    private int currentPosition = -1;

    private View searchView;
    public ConversationListAdapter() {

    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public void setCurrentPosition(int currentPosition , boolean isClick) {
        this.currentPosition = currentPosition;
        this.isClick = isClick;
    }

    public void setShowMultiSelectCheckBox(boolean show) {
        isShowMultiSelectCheckBox = show;

        if (!isShowMultiSelectCheckBox) {
            mSelectedPositions.clear();
        }
    }

    public void setForwardFragment(boolean forwardFragment) {
        isForwardFragment = forwardFragment;
    }

    public void setSearchView(View searchView) {
        this.searchView = searchView;
    }

    //设置给定位置条目的选择状态
    public void setItemChecked(String conversationId, boolean isChecked) {
        mSelectedPositions.put(conversationId, isChecked);
    }

    //根据位置判断条目是否选中
    private boolean isItemChecked(String id) {
        if (mSelectedPositions.size() <= 0) {
            return false;
        }

        if (mSelectedPositions.containsKey(id)) {
            return mSelectedPositions.get(id);
        } else {
            return false;
        }
    }

    // 获得选中条目的结果
    public List<ConversationInfo> getSelectedItem() {
        if (mSelectedPositions.size() == 0) {
            return null;
        }
        List<ConversationInfo> selectList = new ArrayList<>();
        for (int i = 0; i < getItemCount() - 1; i++) {
            ConversationInfo conversationInfo = getItem(i);
            if (conversationInfo == null) {
                continue;
            }
            if (isItemChecked(conversationInfo.getConversationId())) {
                selectList.add(conversationInfo);
            }
        }
        return selectList;
    }

    public void setSelectConversations(List<ConversationInfo> dataSource){
        if(dataSource == null || dataSource.size() == 0){
            mSelectedPositions.clear();
            notifyDataSetChanged();
            return;
        }

        mSelectedPositions.clear();
        for (int i = 0; i < dataSource.size(); i++) {
            for (int j =0; j<mDataSource.size(); j++){
                if (TextUtils.equals(dataSource.get(i).getConversationId(), mDataSource.get(j).getConversationId())){
                    setItemChecked(mDataSource.get(j).getConversationId(), true);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void setOnItemClickListener(ConversationListLayout.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(ConversationListLayout.OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public void setOnItemAvatarClickListener(ConversationListLayout.OnItemAvatarClickListener listener) {
        this.mOnItemAvatarClickListener = listener;
    }

    public void setBanConversationDelListener(ConversationListLayout.BanConversationDelListener listener) {
        this.mBanConversationDelListener = listener;
    }

    public void banConversationDel(){
        if (mBanConversationDelListener != null)
            mBanConversationDelListener.banConversationDel();
    }

    @Override
    public void onDataSourceChanged(List<ConversationInfo> dataSource) {
        if (listener != null) {
            V2TIMManager.getInstance().removeIMSDKListener(listener);
        }
        if (subscribe != null && !subscribe.isEmpty()) {
            V2TIMManager.getInstance().unsubscribeUserStatus(subscribe, null);
        }
        this.mDataSource = dataSource;
        conversationInfoMap = new HashMap<>();
        subscribe = new ArrayList<>();
        for (int i = 0; i < dataSource.size(); i++) {
            ConversationInfo info = getItem(i);
            if (info != null) {
                String id = info.getId();
                conversationInfoMap.put(id, info);
                if (subscribe.size() < 100) {
                    subscribe.add(info.getId());
                }
            }
        }
        V2TIMManager.getInstance().getUserStatus(new ArrayList<>(conversationInfoMap.keySet()), new V2TIMValueCallback<List<V2TIMUserStatus>>() {
            @Override
            public void onSuccess(List<V2TIMUserStatus> v2TIMUserStatuses) {
                Log.d("ONLINE_STATE", "STATE GET =============");
                for (int i = 0; i < v2TIMUserStatuses.size(); i++) {
                    V2TIMUserStatus status = v2TIMUserStatuses.get(i);
                    int newType = status.getStatusType();
                    ConversationInfo info = conversationInfoMap.get(status.getUserID());
                    if (info != null) {
                        if (info.getState() != newType) {
                            info.setState(newType);
                            Log.d("ONLINE_STATE", "User[" + status.getUserID() + "] Status turn to " + newType);
                            onItemChanged(mDataSource.indexOf(info));
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        V2TIMManager.getInstance().subscribeUserStatus(subscribe, new V2TIMCallback() {
            @Override
            public void onSuccess() {
                Log.d("ONLINE_STATE", "SUBSCRIBE SUCCESS");
            }

            @Override
            public void onError(int i, String s) {
                Log.d("ONLINE_STATE", "SUBSCRIBE ERROR -> " + s);
            }
        });
        listener = new V2TIMSDKListener() {
            @Override
            public void onUserStatusChanged(List<V2TIMUserStatus> userStatusList) {
                Log.d("ONLINE_STATE", "STATE CHANGE =============");
                super.onUserStatusChanged(userStatusList);
                for (int i = 0; i < userStatusList.size(); i++) {
                    V2TIMUserStatus status = userStatusList.get(i);
                    int newType = status.getStatusType();
                    ConversationInfo info = conversationInfoMap.get(status.getUserID());
                    if (info != null) {
                        if (info.getState() != newType) {
                            info.setState(newType);
                            Log.d("ONLINE_STATE", "User[" + status.getUserID() + "] Status turn to " + newType);
                            onItemChanged(mDataSource.indexOf(info));
                        }
                    }
                }
            }
        };
        V2TIMManager.getInstance().addIMSDKListener(listener);

    }

    public List<ConversationInfo> getDataSource(){
        return mDataSource;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ConversationBaseHolder holder = null;
        // 创建不同的 ViewHolder
        View view;
        // 根据ViewType来创建条目
        if (viewType == ITEM_TYPE_HEADER_SEARCH) {
            // 如果 searchView 不显示，添加一个隐藏的 view，防止 recyclerview 自动滑到最底部
            if (searchView == null) {
                searchView = new View(parent.getContext());
            }
            return new HeaderViewHolder(searchView);
        }else if (viewType == ConversationInfo.TYPE_CUSTOM) {
            view = inflater.inflate(R.layout.conversation_custom_adapter, parent, false);
            holder = new ConversationCustomHolder(view);
        } else if (viewType == ITEM_TYPE_FOOTER_LOADING) {
            view = inflater.inflate(R.layout.loading_progress_bar, parent, false);
            return new FooterViewHolder(view);
        } else if (viewType == ConversationInfo.TYPE_FORWAR_SELECT) {
            view = inflater.inflate(R.layout.conversation_forward_select_adapter, parent, false);
            return new ForwardSelectHolder(view);
        }  else if (viewType == ConversationInfo.TYPE_RECENT_LABEL) {
            view = inflater.inflate(R.layout.conversation_forward_label_adapter, parent, false);
            return new ForwardLabelHolder(view);
        } else {
            view = inflater.inflate(R.layout.conversation_list_item_layout, parent, false);
            holder = new ConversationCommonHolder(parent.getContext(),view);
            ((ConversationCommonHolder) holder).setForwardMode(isForwardFragment);
        }
        holder.setAdapter(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ConversationInfo conversationInfo = getItem(position);
        ConversationBaseHolder baseHolder = null;
        if (conversationInfo != null) {
            baseHolder = (ConversationBaseHolder) holder;
        }

        switch (getItemViewType(position)) {
            case ConversationInfo.TYPE_CUSTOM:
            case ConversationInfo.TYPE_RECENT_LABEL:
                break;
            case ITEM_TYPE_FOOTER_LOADING: {
                if (holder instanceof FooterViewHolder) {
                    ((ConversationBaseHolder) holder).layoutViews(null, position);
                }
                break;
            }
            case ConversationInfo.TYPE_FORWAR_SELECT : {
                ForwardSelectHolder selectHolder = (ForwardSelectHolder) holder;
                selectHolder.refreshTitle(!isShowMultiSelectCheckBox);
                setOnClickListener(holder, position, conversationInfo);
                break;
            }
            default:
                setOnClickListener(holder, position, conversationInfo);
        }
        if (baseHolder != null) {
            baseHolder.layoutViews(conversationInfo, position);
            setCheckBoxStatus(position, conversationInfo, baseHolder);
        }

        if (getCurrentPosition() == position && isClick()){
            if (baseHolder != null) {
                baseHolder.itemView.setBackgroundResource(R.color.conversation_item_clicked_color);
            }
        } else {
            if (conversationInfo == null) {
                return;
            }
            if (conversationInfo.isTop() && !isForwardFragment) {
                baseHolder.itemView.setBackgroundColor(baseHolder.rootView.getResources().getColor(R.color.conversation_item_top_color));
            } else {
                baseHolder.itemView.setBackgroundColor(Color.WHITE);
            }
        }
        ((ConversationCommonHolder) holder).conversationIconView.setOnlineState(getItem(position).getState() == V2TIMUserStatus.V2TIM_USER_STATUS_ONLINE);
    }

    private void setOnClickListener(RecyclerView.ViewHolder holder, int position, ConversationInfo conversationInfo) {
        if (getItemViewType(position) == ITEM_TYPE_HEADER_SEARCH) {
            return;
        }
        //设置点击和长按事件
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(view, position, conversationInfo);
                }
            });
        }
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnItemLongClickListener.OnItemLongClick(view, position, conversationInfo);
//                    setCurrentPosition(position, true);
//                    notifyItemChanged(position);
                    return true;
                }
            });
        }
    }

    // 设置多选框的选中状态和点击事件
    private void setCheckBoxStatus(final int position, final ConversationInfo conversationInfo, ConversationBaseHolder baseHolder) {
        if (!(baseHolder instanceof ConversationCommonHolder) || ((ConversationCommonHolder) baseHolder).multiSelectCheckBox == null) {
            return;
        }
        String conversationId = conversationInfo.getConversationId();
        ConversationCommonHolder commonHolder = (ConversationCommonHolder) baseHolder;
        if (!isShowMultiSelectCheckBox) {
            commonHolder.multiSelectCheckBox.setVisibility(View.GONE);
        } else {
            commonHolder.multiSelectCheckBox.setVisibility(View.VISIBLE);

            //设置条目状态
            commonHolder.multiSelectCheckBox.setChecked(isItemChecked(conversationId));
            //checkBox的监听
            commonHolder.multiSelectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setItemChecked(conversationId, !isItemChecked(conversationId));
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, position, conversationInfo);
                    }
                }
            });

            //条目view的监听
            baseHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setItemChecked(conversationId, !isItemChecked(conversationId));
                    notifyItemChanged(position);
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, position, conversationInfo);
                    }
                }
            });
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        // ViewHolder 被回收时要清空头像 view 并且停止异步加载头像
        if (holder instanceof ConversationCommonHolder) {
            ((ConversationCommonHolder) holder).conversationIconView.clearImage();
        }
    }

    public ConversationInfo getItem(int position) {
        if (mDataSource.isEmpty() || position == getItemCount() - 1) {
            return null;
        }

        int dataPosition;
        if (isForwardFragment) {
            dataPosition = position - SELECT_COUNT - SELECT_LABEL_COUNT;
        } else {
            dataPosition = position - HEADER_COUNT;
        }
        if (dataPosition < mDataSource.size() && dataPosition >= 0) {
            return mDataSource.get(dataPosition);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        int listSize = mDataSource.size();
        if (isForwardFragment) {
            return listSize + SELECT_COUNT + SELECT_LABEL_COUNT + FOOTER_COUNT;
        }
        return listSize + HEADER_COUNT + FOOTER_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (isForwardFragment) {
            if (position == 0) {
                return ConversationInfo.TYPE_FORWAR_SELECT;
            } else if (position == 1) {
                return ConversationInfo.TYPE_RECENT_LABEL;
            }
        } else {
            if (position == 0) {
                return ITEM_TYPE_HEADER_SEARCH;
            }
        }
        if (position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER_LOADING;
        } else if (mDataSource != null) {
            ConversationInfo conversationInfo = getItem(position);
            if (conversationInfo != null) {
                return conversationInfo.getType();
            }
        }
        return ConversationInfo.TYPE_COMMON;
    }

    private int getItemIndexInAdapter(int index) {
        int itemIndex = index;
        if (isForwardFragment) {
            itemIndex = index + SELECT_LABEL_COUNT + SELECT_COUNT;
        } else {
            itemIndex = index + HEADER_COUNT;
        }
        return itemIndex;
    }

    @Override
    public void onItemRemoved(int position) {
        int itemIndex = getItemIndexInAdapter(position);
        notifyItemRemoved(itemIndex);
    }

    @Override
    public void onItemInserted(int position) {
        int itemIndex = getItemIndexInAdapter(position);
        notifyItemInserted(itemIndex);
    }

    @Override
    public void onItemChanged(int position) {
        int itemIndex = getItemIndexInAdapter(position);
        notifyItemChanged(itemIndex);
    }

    @Override
    public void onItemRangeChanged(int startPosition, int count) {
        int itemStartIndex = getItemIndexInAdapter(startPosition);
        notifyItemRangeChanged(itemStartIndex, count);
    }

    public void setItemTopTextSize(int size) {
        mTopTextSize = size;
    }

    public int getItemTopTextSize() {
        return mTopTextSize;
    }

    public void setItemBottomTextSize(int size) {
        mBottomTextSize = size;
    }

    public int getItemBottomTextSize() {
        return mBottomTextSize;
    }

    public void setItemDateTextSize(int size) {
        mDateTextSize = size;
    }

    public int getItemDateTextSize() {
        return mDateTextSize;
    }

    public void setItemAvatarRadius(int radius) {
        mItemAvatarRadius = radius;
    }

    public int getItemAvatarRadius() {
        return mItemAvatarRadius;
    }

    public void disableItemUnreadDot(boolean flag) {
        mHasShowUnreadDot = !flag;
    }

    public boolean hasItemUnreadDot() {
        return mHasShowUnreadDot;
    }

    @Override
    public void onLoadingStateChanged(boolean isLoading) {
        this.mIsLoading = isLoading;
        notifyItemChanged(getItemCount() - 1);
        //滚动到顶部
        //mRecycleView.scrollToTop();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecycleView = (ConversationListLayout) recyclerView;
    }

    @Override
    public void onViewNeedRefresh() {
        notifyDataSetChanged();
    }

    //header
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    //footer
    class FooterViewHolder extends ConversationBaseHolder {
        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void layoutViews(ConversationInfo conversationInfo, int position) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) rootView.getLayoutParams();
            if (mIsLoading) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                rootView.setVisibility(View.VISIBLE);
            } else {
                param.height = 0;
                param.width = 0;
                rootView.setVisibility(View.GONE);
            }
            rootView.setLayoutParams(param);
        }
    }

    static class ForwardLabelHolder extends ConversationBaseHolder {
        public ForwardLabelHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void layoutViews(ConversationInfo conversationInfo, int position) {

        }
    }

    static class ForwardSelectHolder extends ConversationBaseHolder {
        private final TextView titleView;
        public ForwardSelectHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.forward_title);
        }

        @Override
        public void layoutViews(ConversationInfo conversationInfo, int position) {

        }

        public void refreshTitle(boolean isCreateGroup){
            if (titleView == null)return;

            if (isCreateGroup){
                titleView.setText(TUIConversationService.getAppContext().getString(R.string.forward_select_new_chat));
            } else {
                titleView.setText(TUIConversationService.getAppContext().getString(R.string.forward_select_from_contact));
            }
        }
    }

}
