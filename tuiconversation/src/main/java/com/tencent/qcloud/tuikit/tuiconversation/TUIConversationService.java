package com.tencent.qcloud.tuikit.tuiconversation;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMConversationListener;
import com.tencent.imsdk.v2.V2TIMFriendInfo;
import com.tencent.imsdk.v2.V2TIMFriendshipListener;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuikit.tuichat.interfaces.C2CChatEventListener;
import com.tencent.qcloud.tuikit.tuiconversation.bean.ConversationInfo;
import com.tencent.qcloud.tuicore.ServiceInitializer;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuikit.tuiconversation.interfaces.ConversationEventListener;
import com.tencent.qcloud.tuikit.tuiconversation.util.ConversationUtils;
import com.tencent.qcloud.tuikit.tuiconversation.util.TUIConversationLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TUIConversationService extends ServiceInitializer  implements ITUIConversationService {
    public static final String TAG = TUIConversationService.class.getSimpleName();
    private static TUIConversationService instance;

    public static TUIConversationService getInstance() {
        return instance;
    }


    private WeakReference<ConversationEventListener> conversationEventListener;
    private WeakReference<ConversationEventListener> conversationFriendEventListener;

    @Override
    public void init(Context context) {
        instance = this;
        initService();
        initEvent();
        initIMListener();
    }

    private void initService() {
        TUICore.registerService(TUIConstants.TUIConversation.SERVICE_NAME, this);
    }

    private void initEvent() {
        // 退群通知
        TUICore.registerEvent(TUIConstants.TUIGroup.EVENT_GROUP, TUIConstants.TUIGroup.EVENT_SUB_KEY_EXIT_GROUP, this);
        // 群成员被踢通知
        TUICore.registerEvent(TUIConstants.TUIGroup.EVENT_GROUP, TUIConstants.TUIGroup.EVENT_SUB_KEY_MEMBER_KICKED_GROUP, this);
        // 群解散通知
        TUICore.registerEvent(TUIConstants.TUIGroup.EVENT_GROUP, TUIConstants.TUIGroup.EVENT_SUB_KEY_GROUP_DISMISS, this);
        // 群被回收通知
        TUICore.registerEvent(TUIConstants.TUIGroup.EVENT_GROUP, TUIConstants.TUIGroup.EVENT_SUB_KEY_GROUP_RECYCLE, this);
        // 好友备注修改通知
        TUICore.registerEvent(TUIConstants.TUIContact.EVENT_FRIEND_INFO_CHANGED, TUIConstants.TUIContact.EVENT_SUB_KEY_FRIEND_REMARK_CHANGED, this);
        // 清空群消息通知
        TUICore.registerEvent(TUIConstants.TUIGroup.EVENT_GROUP, TUIConstants.TUIGroup.EVENT_SUB_KEY_CLEAR_MESSAGE, this);
    }

    @Override
    public Object onCall(String method, Map<String, Object> param) {
        Bundle result = new Bundle();

        ConversationEventListener conversationEventListener = getInstance().getConversationEventListener();
        ConversationEventListener conversationFriendEventListener = getInstance().getConversationFriendEventListener();
        if (conversationEventListener == null) {
            TUIConversationLog.e(TAG, "execute " + method + " failed , conversationEvent listener is null");
            return result;
        }
        if (TextUtils.equals(TUIConstants.TUIConversation.METHOD_IS_TOP_CONVERSATION, method)) {
            String chatId = (String) param.get(TUIConstants.TUIConversation.CHAT_ID);
            if (!TextUtils.isEmpty(chatId)) {
                boolean isTop = conversationEventListener.isTopConversation(chatId);
                result.putBoolean(TUIConstants.TUIConversation.IS_TOP, isTop);
            }
        } else if (TextUtils.equals(TUIConstants.TUIConversation.METHOD_SET_TOP_CONVERSATION, method)) {
            String chatId = (String) param.get(TUIConstants.TUIConversation.CHAT_ID);
            boolean isTop = (boolean) param.get(TUIConstants.TUIConversation.IS_SET_TOP);
            if (!TextUtils.isEmpty(chatId)) {
                conversationEventListener.setConversationTop(chatId, isTop, new IUIKitCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                    }
                });
            }
        } else if (TextUtils.equals(TUIConstants.TUIConversation.METHOD_GET_TOTAL_UNREAD_COUNT, method)) {
            return conversationEventListener.getUnreadTotal();
        } else if (TextUtils.equals(TUIConstants.TUIConversation.METHOD_UPDATE_TOTAL_UNREAD_COUNT, method)) {
            HashMap<String, Object> unreadMap = new HashMap<>();
            long totalUnread = conversationEventListener.getUnreadTotal();
            unreadMap.put(TUIConstants.TUIConversation.TOTAL_UNREAD_COUNT, totalUnread);
            TUICore.notifyEvent(TUIConstants.TUIConversation.EVENT_UNREAD, TUIConstants.TUIConversation.EVENT_SUB_KEY_UNREAD_CHANGED, unreadMap);
        } else if (TextUtils.equals(TUIConstants.TUIConversation.METHOD_DELETE_CONVERSATION, method)) {
            String conversationId = (String) param.get(TUIConstants.TUIConversation.CONVERSATION_ID);
            conversationEventListener.deleteConversation(conversationId);
            if(conversationFriendEventListener!=null){
                conversationFriendEventListener.deleteConversation(conversationId);
            }
        }
        return result;
    }

    @Override
    public void onNotifyEvent(String key, String subKey, Map<String, Object> param) {
        if (TextUtils.equals(key, TUIConstants.TUIGroup.EVENT_GROUP)) {
            if (TextUtils.equals(subKey, TUIConstants.TUIGroup.EVENT_SUB_KEY_EXIT_GROUP)
                    || TextUtils.equals(subKey, TUIConstants.TUIGroup.EVENT_SUB_KEY_GROUP_DISMISS)
                    || TextUtils.equals(subKey, TUIConstants.TUIGroup.EVENT_SUB_KEY_GROUP_RECYCLE)) {
                ConversationEventListener eventListener = getConversationEventListener();
                ConversationEventListener conversationFriendEventListener = getConversationFriendEventListener();
                String groupId = null;
                if (param != null) {
                    groupId = (String) getOrDefault(param.get(TUIConstants.TUIGroup.GROUP_ID), "");
                }
                if(conversationFriendEventListener != null){
                    conversationFriendEventListener.deleteConversation(groupId,true);
                }
                if (eventListener != null) {
                    eventListener.deleteConversation(groupId, true);
                }
            } else if (TextUtils.equals(subKey, TUIConstants.TUIGroup.EVENT_SUB_KEY_MEMBER_KICKED_GROUP)) {
                if (param == null) {
                    return;
                }
                String groupId = (String) getOrDefault(param.get(TUIConstants.TUIGroup.GROUP_ID), "");
                ArrayList<String> memberList = (ArrayList<String>) param.get(TUIConstants.TUIGroup.GROUP_MEMBER_ID_LIST);
                if (TextUtils.isEmpty(groupId) || memberList == null || memberList.isEmpty()) {
                    return;
                }
                for (String id : memberList) {
                    if (TextUtils.equals(id, TUILogin.getLoginUser())) {
                        ConversationEventListener eventListener = getConversationEventListener();
                        ConversationEventListener conversationFriendEventListener = getConversationFriendEventListener();
                        if (eventListener != null) {
                            eventListener.deleteConversation(groupId, true);
                        }
                        if (conversationFriendEventListener != null) {
                            conversationFriendEventListener.deleteConversation(groupId, true);
                        }
                        break;
                    }
                }
            } else if (TextUtils.equals(subKey, TUIConstants.TUIGroup.EVENT_SUB_KEY_CLEAR_MESSAGE)) {
                String groupId = (String) getOrDefault(param.get(TUIConstants.TUIGroup.GROUP_ID), "");
                ConversationEventListener eventListener = getConversationEventListener();
                ConversationEventListener conversationFriendEventListener = getConversationFriendEventListener();
                if (conversationFriendEventListener != null) {
                    conversationFriendEventListener.clearConversationMessage(groupId, true);
                }
                if (eventListener != null) {
                    eventListener.clearConversationMessage(groupId, true);
                }
            }
        } else if (key.equals(TUIConstants.TUIContact.EVENT_FRIEND_INFO_CHANGED)) {
            if (subKey.equals(TUIConstants.TUIContact.EVENT_SUB_KEY_FRIEND_REMARK_CHANGED)) {
                if (param == null || param.isEmpty()) {
                    return;
                }
                ConversationEventListener conversationEventListener = getInstance().getConversationEventListener();
                if (conversationEventListener == null) {
                    return;
                }
                String id = (String) param.get(TUIConstants.TUIContact.FRIEND_ID);
                String remark = (String) param.get(TUIConstants.TUIContact.FRIEND_REMARK);
                conversationEventListener.onFriendRemarkChanged(id ,remark);
                ConversationEventListener conversationFriendEventListener = getConversationFriendEventListener();
                if(conversationFriendEventListener!=null){
                    conversationFriendEventListener.onFriendRemarkChanged(id ,remark);
                }
            }
        }
    }

    private Object getOrDefault(Object value, Object defaultValue) {
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    private void initIMListener() {
        V2TIMManager.getConversationManager().addConversationListener(new V2TIMConversationListener() {
            @Override
            public void onSyncServerStart() {
            }

            @Override
            public void onSyncServerFinish() {
            }

            @Override
            public void onSyncServerFailed() {
            }

            @Override
            public void onNewConversation(List<V2TIMConversation> conversationList) {
                ConversationEventListener conversationEventListener = getInstance().getConversationEventListener();
                if (conversationEventListener != null) {
                    List<ConversationInfo> conversationInfoList = ConversationUtils.convertV2TIMConversationList(conversationList);
                    conversationEventListener.onNewConversation(conversationInfoList);
                    ConversationEventListener conversationFriendEventListener = getConversationFriendEventListener();
                    if(conversationFriendEventListener!=null){
                        conversationFriendEventListener.onNewConversation(conversationInfoList);
                    }
                }

            }

            @Override
            public void onConversationChanged(List<V2TIMConversation> conversationList) {
                ConversationEventListener conversationEventListener = getInstance().getConversationEventListener();
                if (conversationEventListener != null) {
                    List<ConversationInfo> conversationInfoList = ConversationUtils.convertV2TIMConversationList(conversationList);
                    conversationEventListener.onConversationChanged(conversationInfoList);
                    ConversationEventListener conversationFriendEventListener = getConversationFriendEventListener();
                    if(conversationFriendEventListener!=null){
                        conversationFriendEventListener.onNewConversation(conversationInfoList);
                    }
                }
            }

            @Override
            public void onTotalUnreadMessageCountChanged(long totalUnreadCount) {
                ConversationEventListener conversationEventListener = getInstance().getConversationEventListener();
                if (conversationEventListener != null) {
                    conversationEventListener.updateTotalUnreadMessageCount(totalUnreadCount);
                    ConversationEventListener conversationFriendEventListener = getConversationFriendEventListener();
                    if(conversationFriendEventListener!=null){
                        conversationFriendEventListener.updateTotalUnreadMessageCount(totalUnreadCount);
                    }
                }
                HashMap<String, Object> param = new HashMap<>();
                param.put(TUIConstants.TUIConversation.TOTAL_UNREAD_COUNT, totalUnreadCount);
                TUICore.notifyEvent(TUIConstants.TUIConversation.EVENT_UNREAD, TUIConstants.TUIConversation.EVENT_SUB_KEY_UNREAD_CHANGED, param);
            }
        });
        //添加关系链监听器
        V2TIMManager.getFriendshipManager().addFriendListener(new V2TIMFriendshipListener() {
            /**
             * @Desc TODO(好友新增通知)
             * @author 彭石林
             * @parame [users]
             * @Date 2022/8/15
             */
            @Override
            public void onFriendListAdded(List<V2TIMFriendInfo> v2TIMFriendInfos) {
                Log.e("当前注册添加好友通知",v2TIMFriendInfos.size()+"=================="+v2TIMFriendInfos);
                if(!v2TIMFriendInfos.isEmpty()){
                    final List<String> urlList = new ArrayList<>();
                    for (V2TIMFriendInfo v2TIMFriendInfo : v2TIMFriendInfos){
                        String userId = TUIConstants.TUIConversation.CONVERSATION_C2C_PREFIX + v2TIMFriendInfo.getUserID() ;
                        urlList.add(userId);
                    }
                    ConversationEventListener eventListener = getConversationEventListener();
                    ConversationEventListener conversationFriendEventListener = getConversationFriendEventListener();
                    if(eventListener!=null){
                        eventListener.newConversationListEvent(urlList);
                    }
                    if(conversationFriendEventListener!=null){
                        conversationFriendEventListener.newConversationListEvent(urlList);
                    }
                }
            }

            /**
             * @Desc (
             * 好友删除通知 ， ， 两种情况会收到这个回调 ：
             *自己删除好友 （ 单向和双向删除都会收到回调 ）
             *好友把自己删除 （ 双向删除会收到 ）)
             * @author 彭石林
             * @parame [userList]
             * @Date 2022/8/15
             */
            @Override
            public void onFriendListDeleted(List<String> userList) {
                Log.e("当前注册删除好友通知","=================="+userList);
                if(!userList.isEmpty()){
                    List<String> removeInfoList = new ArrayList<>();
                    for (String infoData : userList){
                        String userId = TUIConstants.TUIConversation.CONVERSATION_C2C_PREFIX + infoData ;
                        removeInfoList.add(userId);
                    }
                    ConversationEventListener eventListener = getConversationEventListener();
                    ConversationEventListener conversationFriendEventListener = getConversationFriendEventListener();
                    if(eventListener!=null){
                        eventListener.newConversationListEvent(removeInfoList);
                    }
                    if(conversationFriendEventListener!=null){
                        conversationFriendEventListener.newConversationListEvent(removeInfoList);
                    }
                }
            }
            /**
             * @Desc(
             * 黑名单删除通知
             * )
             * @author 彭石林
             * @parame [userList]
             * @return void
             * @Date 2022/8/15
             */
            @Override
            public void onBlackListDeleted(List<String> userList) {

            }
        });
    }

    public void setConversationEventListener(ConversationEventListener conversationManagerKit) {
        this.conversationEventListener = new WeakReference<>(conversationManagerKit);
    }

    public void setConversationFriendEventListener(ConversationEventListener conversationManagerKitFriend){
        this.conversationFriendEventListener = new WeakReference<>(conversationManagerKitFriend);
    }

    public ConversationEventListener getConversationEventListener() {
        if (conversationEventListener != null) {
            return conversationEventListener.get();
        }
        return null;
    }

    public ConversationEventListener getConversationFriendEventListener() {
        if (conversationFriendEventListener != null) {
            return conversationFriendEventListener.get();
        }
        return null;
    }

}
