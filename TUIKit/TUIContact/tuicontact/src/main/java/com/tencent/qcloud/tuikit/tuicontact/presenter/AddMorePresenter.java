package com.tencent.qcloud.tuikit.tuicontact.presenter;

import android.text.TextUtils;
import android.util.Pair;

import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuicontact.R;
import com.tencent.qcloud.tuikit.tuicontact.bean.FriendApplicationBean;
import com.tencent.qcloud.tuikit.tuicontact.model.ContactProvider;
import com.tencent.qcloud.tuikit.tuicontact.ui.interfaces.IAddMoreActivity;

public class AddMorePresenter {
    private static final String TAG = AddMorePresenter.class.getSimpleName();

    private final ContactProvider provider;

    private IAddMoreActivity addMoreActivity;
    public AddMorePresenter() {
        provider = new ContactProvider();
    }

    public void setAddMoreActivity(IAddMoreActivity addMoreActivity) {
        this.addMoreActivity = addMoreActivity;
    }

    /**
     * 添加好友
     * @param userId 用户 id
     * @param addWording 验证消息
     */
    public void addFriend(String userId, String addWording) {

        provider.addFriend(userId, addWording, new IUIKitCallback<Pair<Integer, String>>() {
            @Override
            public void onSuccess(Pair<Integer, String> data) {
                switch (data.first) {
                    case FriendApplicationBean.ERR_SUCC:
                        ToastUtil.toastShortMessage(addMoreActivity.getString(R.string.success));
                        break;
                    case FriendApplicationBean.ERR_SVR_FRIENDSHIP_INVALID_PARAMETERS:
                        if (TextUtils.equals(data.second, "Err_SNS_FriendAdd_Friend_Exist")) {
                            ToastUtil.toastShortMessage(addMoreActivity.getString(R.string.have_be_friend));
                            break;
                        }
                    case FriendApplicationBean.ERR_SVR_FRIENDSHIP_COUNT_LIMIT:
                        ToastUtil.toastShortMessage(addMoreActivity.getString(R.string.friend_limit));
                        break;
                    case FriendApplicationBean.ERR_SVR_FRIENDSHIP_PEER_FRIEND_LIMIT:
                        ToastUtil.toastShortMessage(addMoreActivity.getString(R.string.other_friend_limit));
                        break;
                    case FriendApplicationBean.ERR_SVR_FRIENDSHIP_IN_SELF_BLACKLIST:
                        ToastUtil.toastShortMessage(addMoreActivity.getString(R.string.in_blacklist));
                        break;
                    case FriendApplicationBean.ERR_SVR_FRIENDSHIP_ALLOW_TYPE_DENY_ANY:
                        ToastUtil.toastShortMessage(addMoreActivity.getString(R.string.forbid_add_friend));
                        break;
                    case FriendApplicationBean.ERR_SVR_FRIENDSHIP_IN_PEER_BLACKLIST:
                        ToastUtil.toastShortMessage(addMoreActivity.getString(R.string.set_in_blacklist));
                        break;
                    case FriendApplicationBean.ERR_SVR_FRIENDSHIP_ALLOW_TYPE_NEED_CONFIRM:
                        ToastUtil.toastShortMessage(addMoreActivity.getString(R.string.wait_agree_friend));
                        break;
                    default:
                        ToastUtil.toastLongMessage(data.first + " " + data.second);
                        break;
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastShortMessage("Error code = " + errCode + ", desc = " + errMsg);
            }
        });
    }

    public void joinGroup(String groupId, String addWording) {
        provider.joinGroup(groupId, addWording, new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                ToastUtil.toastShortMessage(addMoreActivity.getString(R.string.send_request));
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastShortMessage("Error code = " + errCode + ", desc = " + errMsg);
            }
        });
    }
}
