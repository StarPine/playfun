package com.tencent.qcloud.tuikit.tuichat.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tencent.custom.CustomIMTextEntity;
import com.tencent.custom.EvaluateItemEntity;
import com.tencent.custom.PhotoAlbumItemEntity;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.bean.ChatInfo;
import com.tencent.qcloud.tuikit.tuichat.bean.GroupInfo;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TextMessageBean;
import com.tencent.qcloud.tuikit.tuichat.presenter.GroupChatPresenter;
import com.tencent.qcloud.tuikit.tuichat.ui.interfaces.OnItemClickListener;
import com.tencent.qcloud.tuikit.tuichat.util.TUIChatLog;

public class TUIGroupChatFragment extends TUIBaseChatFragment {
    private static final String TAG = TUIGroupChatFragment.class.getSimpleName();

    private GroupChatPresenter presenter;
    private GroupInfo groupInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        TUIChatLog.i(TAG, "oncreate view " + this);

        baseView = super.onCreateView(inflater, container, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return baseView;
        }
        groupInfo = (GroupInfo) bundle.getSerializable(TUIChatConstants.CHAT_INFO);
        if (groupInfo == null) {
            return baseView;
        }

        initView();
        return baseView;
    }

    @Override
    protected void initView() {
        super.initView();
        chatView.setPresenter(presenter);
        presenter.setGroupInfo(groupInfo);
        chatView.setChatInfo(groupInfo);
        chatView.getMessageLayout().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onMessageLongClick(View view, int position, TUIMessageBean messageBean) {
                //因为adapter中第一条为加载条目，位置需减1
                chatView.getMessageLayout().showItemPopMenu(position - 1, messageBean, view);
            }

            @Override
            public void onUserIconClick(View view, int position, TUIMessageBean messageBean) {
                if (null == messageBean) {
                    return;
                }

                ChatInfo info = new ChatInfo();
                info.setId(messageBean.getSender());

                Bundle bundle = new Bundle();
                bundle.putString(TUIConstants.TUIChat.CHAT_ID, info.getId());
                TUICore.startActivity("FriendProfileActivity", bundle);

            }

            @Override
            public void onUserIconLongClick(View view, int position, TUIMessageBean messageBean) {
                String result_id = messageBean.getV2TIMMessage().getSender();
                String result_name = messageBean.getV2TIMMessage().getNickName();
                chatView.getInputLayout().addInputText(result_name, result_id);
            }

            @Override
            public void onReEditRevokeMessage(View view, int position, TUIMessageBean messageInfo) {
                if (messageInfo == null) {
                    return;
                }
                int messageType = messageInfo.getMsgType();
                if (messageType == V2TIMMessage.V2TIM_ELEM_TYPE_TEXT){
                    chatView.getInputLayout().appendText(messageInfo.getV2TIMMessage().getTextElem().getText());
                } else {
                    TUIChatLog.e(TAG, "error type: " + messageType);
                }
            }

            @Override
            public void onRecallClick(View view, int position, TUIMessageBean messageInfo) {

            }

            @Override
            public void onTextSelected(View view, int position, TUIMessageBean messageInfo) {
                if (messageInfo instanceof  TextMessageBean) {
                    TUIChatLog.d(TAG, "chatfragment onTextSelected selectedText = " + ((TextMessageBean) messageInfo).getSelectText());
                }
                chatView.getMessageLayout().setSelectedPosition(position);
                chatView.getMessageLayout().showItemPopMenu(position - 1, messageInfo, view);
            }

            @Override
            public void onToastVipText(TUIMessageBean messageInfo) {

            }

            @Override
            public void onTextReadUnlock(TextView textView, View view, TUIMessageBean messageInfo) {

            }

            @Override
            public void onTextTOWebView(TUIMessageBean messageInfo) {

            }

            @Override
            public void toUserHome() {

            }

            @Override
            public void openUserImage(PhotoAlbumItemEntity itemEntity) {

            }

            @Override
            public void onClickEvaluate(int position, TUIMessageBean messageInfo, EvaluateItemEntity evaluateItemEntity, boolean more) {

            }

            @Override
            public void onClickCustomText(int position, TUIMessageBean messageInfo, CustomIMTextEntity customIMTextEntity) {

            }

            @Override
            public void onClickDialogRechargeShow() {

            }

            @Override
            public void clickToUserMain() {

            }

            @Override
            public void onClickCustomText() {

            }
        });
    }

    public void setPresenter(GroupChatPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public GroupChatPresenter getPresenter() {
        return presenter;
    }

    @Override
    public ChatInfo getChatInfo() {
        return groupInfo;
    }
}
