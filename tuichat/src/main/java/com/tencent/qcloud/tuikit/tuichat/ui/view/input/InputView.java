package com.tencent.qcloud.tuikit.tuichat.ui.view.input;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tencent.qcloud.tuicore.Status;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.BackgroundTasks;
import com.tencent.qcloud.tuicore.util.FileUtil;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.TUIChatService;
import com.tencent.qcloud.tuikit.tuichat.bean.InputMoreActionUnit;
import com.tencent.qcloud.tuikit.tuichat.bean.ReplyPreviewBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.FileMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.message.TUIMessageBean;
import com.tencent.qcloud.tuikit.tuichat.interfaces.CustomizeMessageListener;
import com.tencent.qcloud.tuikit.tuichat.presenter.ChatPresenter;
import com.tencent.qcloud.tuikit.tuichat.component.AudioPlayer;
import com.tencent.qcloud.tuikit.tuichat.component.face.Emoji;
import com.tencent.qcloud.tuikit.tuichat.component.face.FaceManager;
import com.tencent.qcloud.tuikit.tuichat.ui.view.input.face.FaceFragment;
import com.tencent.qcloud.tuikit.tuichat.component.camera.CameraActivity;
import com.tencent.qcloud.tuikit.tuichat.component.camera.view.JCameraView;
import com.tencent.qcloud.tuikit.tuichat.ui.interfaces.IChatLayout;
import com.tencent.qcloud.tuikit.tuichat.ui.view.input.inputmore.InputMoreFragment;
import com.tencent.qcloud.tuikit.tuichat.util.ChatMessageBuilder;
import com.tencent.qcloud.tuikit.tuichat.bean.ChatInfo;
import com.tencent.qcloud.tuikit.tuichat.bean.DraftInfo;
import com.tencent.qcloud.tuikit.tuichat.util.ChatMessageParser;
import com.tencent.qcloud.tuikit.tuichat.util.PermissionHelper;
import com.tencent.qcloud.tuikit.tuichat.util.TUIChatLog;
import com.tencent.qcloud.tuikit.tuichat.util.TUIChatUtils;
import com.tencent.qcloud.tuicore.TUICore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 聊天界面，底部发送图片、拍照、摄像、文件面板
 */

public class InputView extends LinearLayout implements View.OnClickListener, TextWatcher {
    private static final String TAG = InputView.class.getSimpleName();

    private static final int STATE_NONE_INPUT = -1;
    private static final int STATE_SOFT_INPUT = 0;
    private static final int STATE_VOICE_INPUT = 1;
    private static final int STATE_FACE_INPUT = 2;
    private static final int STATE_ACTION_INPUT = 3;

    /**
     * 消息发送按钮
     */
    protected Button mSendTextButton;
    protected ImageView phoneVideoBtn;
    protected ImageView giftBtn;
    protected ImageView callPhoneVideoBtn;
    protected ImageView callPhoneAudioBtn;

    // 音视频通话成员数限制
    protected static final int CALL_MEMBER_LIMIT = 8;

    /**
     * 语音/文字切换输入控件
     */
    protected ImageView mAudioInputSwitchButton;
    protected boolean mAudioInputDisable;

    /**
     * 表情按钮
     */
    protected ImageView mEmojiInputButton;
    protected boolean mEmojiInputDisable;

    /**
     * 更多按钮
     */
    protected Object mMoreInputEvent;
    protected boolean mMoreInputDisable;

    /**
     * 语音长按按钮
     */
    protected Button mSendAudioButton;

    /**
     * 文本输入框
     */
    protected TIMMentionEditText mTextInput;

    protected AppCompatActivity mActivity;
    protected View mInputMoreLayout;
    //    protected ShortcutArea mShortcutArea;
    protected View mInputMoreView;
    protected ChatInfo mChatInfo;
    protected List<InputMoreActionUnit> mInputMoreActionList = new ArrayList<>();
    protected List<InputMoreActionUnit> mInputMoreCustomActionList = new ArrayList<>();
    private AlertDialog mPermissionDialog;
    private boolean mSendPhotoDisable;
    private boolean mCaptureDisable;
    private boolean mVideoRecordDisable;
    private boolean mSendFileDisable;

    private FaceFragment mFaceFragment;
    private ChatInputHandler mChatInputHandler;
    private MessageHandler mMessageHandler;
    private FragmentManager mFragmentManager;
    private InputMoreFragment mInputMoreFragment;
    private IChatLayout mChatLayout;
    private boolean mSendEnable;
    private boolean mAudioCancel;
    private int mCurrentState;
    private int mLastMsgLineCount;
    private float mStartRecordY;
    private String mInputContent;
    private OnStartActivityListener mStartActivityListener;

    private Map<String,String> atUserInfoMap = new HashMap<>();
    private String displayInputString;

    private ChatPresenter presenter;

    private boolean isReplyModel = false;
    private View replyLayout;
    private LinearLayout ll_input;
    private LinearLayout ll_super;
    private TextView replyTv;
    private ImageView replyCloseBtn;
    private ReplyPreviewBean replyPreviewBean;
    private boolean isShowCustomFace = true;
    private boolean isListenerCustomizeMessage = false;//是否已监听

    private SendOnClickCallback sendOnClickCallbacks;

    public InputView(Context context) {
        super(context);
        initViews();
    }

    public InputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public InputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    public void  setSendOnClickCallbacks(SendOnClickCallback sendOnClickCallbacks){
        this.sendOnClickCallbacks = sendOnClickCallbacks;
    }

    public void setPresenter(ChatPresenter presenter) {
        this.presenter = presenter;
        if (!isListenerCustomizeMessage){
            isListenerCustomizeMessage = true;
            presenter.setCustomizeMessageListener(new CustomizeMessageListener() {
                @Override
                public void blacklist(int status) {

                    sendOnClickCallbacks.sendBlackStatus(status);
                }
            });
        }
    }

    private void initViews() {
        mActivity = (AppCompatActivity) getContext();
        inflate(mActivity, R.layout.chat_input_layout, this);
//        mShortcutArea = findViewById(R.id.shortcut_area);
        mInputMoreView = findViewById(R.id.more_groups);
        mSendAudioButton = findViewById(R.id.chat_voice_input);
        mAudioInputSwitchButton = findViewById(R.id.voice_input_switch);
        mEmojiInputButton = findViewById(R.id.face_btn);
        mSendTextButton = findViewById(R.id.send_btn);
        mTextInput = findViewById(R.id.chat_message_input);
        ll_input = findViewById(R.id.ll_input);
        ll_super = findViewById(R.id.ll_super);
        replyLayout = findViewById(R.id.reply_preview_bar);
        replyTv = findViewById(R.id.reply_text);
//        replyCloseBtn = findViewById(R.id.reply_close_btn);
        // 子类实现所有的事件处理

        int iconSize = getResources().getDimensionPixelSize(R.dimen.chat_input_icon_size);
        ViewGroup.LayoutParams layoutParams = mEmojiInputButton.getLayoutParams();
        layoutParams.width = iconSize;
        layoutParams.height = iconSize;
        mEmojiInputButton.setLayoutParams(layoutParams);

        phoneVideoBtn = findViewById(R.id.phone_video_btn);
        giftBtn = findViewById(R.id.gift_btn);
        callPhoneAudioBtn = findViewById(R.id.call_phone_audio_btn);
        callPhoneVideoBtn = findViewById(R.id.call_phone_video_btn);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void init() {
        ll_super.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (sendOnClickCallbacks != null) {
                    sendOnClickCallbacks.onChangedVideoLayout(ll_super.getMeasuredHeight());
                }
            }
        });

        //DL add
        phoneVideoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(sendOnClickCallbacks!=null){
                    sendOnClickCallbacks.onClickPhoneVideo();
                }
            }
        });
        giftBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(sendOnClickCallbacks!=null){
                    sendOnClickCallbacks.onClickGift();
                }
            }
        });
        callPhoneVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendOnClickCallbacks != null) {
                    sendOnClickCallbacks.onClickCallVideo();
                }
            }
        });
        callPhoneAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendOnClickCallbacks != null) {
                    sendOnClickCallbacks.onClickCallAudio();
                }
            }
        });

        mAudioInputSwitchButton.setOnClickListener(this);
        mEmojiInputButton.setOnClickListener(this);
        mSendTextButton.setOnClickListener(this);
        mTextInput.addTextChangedListener(this);
        mTextInput.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (presenter != null) {
                        presenter.clearMessageAndReLoad();
                    }
                    showSoftInput();
                }
                return false;
            }
        });

        mTextInput.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (isReplyModel && TextUtils.isEmpty(mTextInput.getText().toString())) {
                        exitReply();
                    }
                }
                return false;
            }
        });

        mTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return false;
            }
        });

        mSendAudioButton.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (Status.mIsShowFloatWindow){
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        ToastUtils.showShort(R.string.audio_in_call);
                    }
                    return false;
                }
                TUIChatLog.i(TAG, "mSendAudioButton onTouch action:" + motionEvent.getAction());
                PermissionHelper.requestPermission(PermissionHelper.PERMISSION_MICROPHONE, new PermissionHelper.PermissionCallback() {
                    @Override
                    public void onGranted() {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mAudioCancel = true;
                                mStartRecordY = motionEvent.getY();
                                if (mChatInputHandler != null) {
                                    mChatInputHandler.onRecordStatusChanged(ChatInputHandler.RECORD_START);
                                }
                                mSendAudioButton.setText(TUIChatService.getAppContext().getString(R.string.release_end));
                                AudioPlayer.getInstance().startRecord(new AudioPlayer.Callback() {
                                    @Override
                                    public void onCompletion(Boolean success, Boolean isOutTime) {
                                        if (isOutTime){
                                            mAudioCancel = false;
                                        }
                                        recordComplete(success);
                                    }
                                });
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (mChatInputHandler != null) {
                                    mChatInputHandler.onTouchFlag(true);
                                }
                                if (motionEvent.getY() - mStartRecordY < -100) {
                                    mAudioCancel = true;
                                    if (mChatInputHandler != null) {
                                        mChatInputHandler.onRecordStatusChanged(ChatInputHandler.RECORD_CANCEL);
                                    }
                                } else {
                                    if (mAudioCancel) {
                                        if (mChatInputHandler != null) {
                                            mChatInputHandler.onRecordStatusChanged(ChatInputHandler.RECORD_START);
                                        }
                                    }
                                    mAudioCancel = false;
                                }
                                mSendAudioButton.setText(TUIChatService.getAppContext().getString(R.string.release_end));
                                break;
                            case MotionEvent.ACTION_CANCEL:
                            case MotionEvent.ACTION_UP:
                                if (mChatInputHandler != null) {
                                    mChatInputHandler.onTouchFlag(false);
                                }
                                mAudioCancel = motionEvent.getY() - mStartRecordY < -100;
                                if (mChatInputHandler != null) {
                                    mChatInputHandler.onRecordStatusChanged(ChatInputHandler.RECORD_STOP);
                                }
                                AudioPlayer.getInstance().stopRecord();
                                mSendAudioButton.setText(TUIChatService.getAppContext().getString(R.string.hold_say));
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onDenied() {
                        TUIChatLog.i(TAG, "audio record checkPermission failed");
                        if (sendOnClickCallbacks != null) {
                            sendOnClickCallbacks.applyAudioPermission();
                        }
                    }
                });
                return false;
            }
        });

        mTextInput.setOnMentionInputListener(new TIMMentionEditText.OnMentionInputListener() {
            @Override
            public void onMentionCharacterInput(String tag) {
                if(mChatLayout.getChatInfo()!=null){
                    if ((tag.equals(TIMMentionEditText.TIM_MENTION_TAG) || tag.equals(TIMMentionEditText.TIM_MENTION_TAG_FULL))
                            && TUIChatUtils.isGroupChat(mChatLayout.getChatInfo().getType())) {
                        if (mStartActivityListener != null) {
                            mStartActivityListener.onStartGroupMemberSelectActivity();
                        }
                    }
                }

            }
        });

//        replyCloseBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                exitReply();
//            }
//        });
    }

    public void addInputText(String name, String id){
        if (id == null || id.isEmpty()){
            return;
        }

        ArrayList<String> nameList = new ArrayList<String>() {
            {
                add(name);
            }
        };
        ArrayList<String> idList = new ArrayList<String>() {
            {
                add(id);
            }
        };

        updateAtUserInfoMap(nameList, idList);
        if (mTextInput != null) {
            Map<String, String> displayAtNameMap = new HashMap<>();
            displayAtNameMap.put(TIMMentionEditText.TIM_MENTION_TAG + displayInputString, id);
            mTextInput.setMentionMap(displayAtNameMap);
            int selectedIndex = mTextInput.getSelectionEnd();
            if (selectedIndex != -1) {
                String insertStr = TIMMentionEditText.TIM_MENTION_TAG + displayInputString;
                String text = mTextInput.getText().insert(selectedIndex, insertStr).toString();
                FaceManager.handlerEmojiText(mTextInput, text, true);
                mTextInput.setSelection(selectedIndex + insertStr.length());
            }
            showSoftInput();
        }
    }

    public void updateInputText(ArrayList<String> names, ArrayList<String> ids){
        if (ids == null || ids.isEmpty()){
            return;
        }

        updateAtUserInfoMap(names, ids);
        if (mTextInput != null) {
            Map<String, String> displayAtNameList = getDisplayAtNameMap(names, ids);
            mTextInput.setMentionMap(displayAtNameList);

            int selectedIndex = mTextInput.getSelectionEnd();
            if (selectedIndex != -1) {
                String text = mTextInput.getText().insert(selectedIndex, displayInputString).toString();
                FaceManager.handlerEmojiText(mTextInput, text, true);
                mTextInput.setSelection(selectedIndex + displayInputString.length());
            }
            // @ 之后要显示软键盘。Activity 没有 onResume 导致无法显示软键盘
            BackgroundTasks.getInstance().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSoftInput();
                }
            }, 200);
        }
    }

    private Map<String, String> getDisplayAtNameMap(List<String> names, List<String> ids) {
        Map<String, String> displayNameList = new HashMap<>();
        String mentionTag = TIMMentionEditText.TIM_MENTION_TAG;
        if (mTextInput != null) {
            Editable editable = mTextInput.getText();
            int selectionIndex = mTextInput.getSelectionEnd();
            if (editable != null && selectionIndex > 0) {
                String text = editable.toString();
                if (!TextUtils.isEmpty(text)) {
                    mentionTag = text.substring(selectionIndex - 1, selectionIndex);
                }
            }
        }

        for (int i = 0; i < ids.size(); i++) {
            if (i == 0) {
                if (TextUtils.isEmpty(names.get(0))) {
                    displayNameList.put(mentionTag + ids.get(0) + " ", ids.get(0));
                } else {
                    displayNameList.put(mentionTag + names.get(0) + " ", ids.get(0));
                }
                continue;
            }
            String str = TIMMentionEditText.TIM_MENTION_TAG;
            if (TextUtils.isEmpty(names.get(i))) {
                str += ids.get(i);
            } else {
                str += names.get(i);
            }
            str += " ";
            displayNameList.put(str, ids.get(i));
        }
        return displayNameList;
    }

    private void updateAtUserInfoMap(ArrayList<String> names, ArrayList<String> ids){
        displayInputString = "";

        for (int i = 0; i < ids.size(); i++) {
            atUserInfoMap.put(ids.get(i), names.get(i));

            //for display
            if (TextUtils.isEmpty(names.get(i))) {
                displayInputString += ids.get(i);
                displayInputString += " ";
                displayInputString += TIMMentionEditText.TIM_MENTION_TAG;
            } else {
                displayInputString += names.get(i);
                displayInputString += " ";
                displayInputString += TIMMentionEditText.TIM_MENTION_TAG;
            }
        }

        if(!displayInputString.isEmpty()) {
            displayInputString = displayInputString.substring(0, displayInputString.length() - 1);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTextInput.removeTextChangedListener(this);
        atUserInfoMap.clear();
    }

    protected void startSendPhoto() {
        TUIChatLog.i(TAG, "startSendPhoto");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        setOpenPhotoCallback();
        mInputMoreFragment.startActivityForResult(intent, InputMoreFragment.REQUEST_CODE_PHOTO);
    }

    private void setOpenPhotoCallback() {
        mInputMoreFragment.setCallback(new IUIKitCallback() {
            @Override
            public void onSuccess(Object data) {
                TUIChatLog.i(TAG, "onSuccess: " + data);
                if (data == null){
                    TUIChatLog.e(TAG, "data is null");
                    return;
                }

                String uri = data.toString();
                if (TextUtils.isEmpty(uri)){
                    TUIChatLog.e(TAG, "uri is empty");
                    return;
                }

                String fileName = FileUtil.getFileName(TUIChatService.getAppContext(), (Uri) data);
                String fileExtension = FileUtil.getFileExtensionFromUrl(fileName);
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                if (TextUtils.isEmpty(mimeType)) {
                    TUIChatLog.e(TAG, "mimeType is empty.");
                    return;
                }
                if (mimeType.contains("video")){
                    String videoPath = FileUtil.getPathFromUri((Uri) data);
                    TUIMessageBean msg = buildVideoMessage(videoPath);
                    if (msg == null){
                        ToastUtil.toastShortMessage(getResources().getString(R.string.send_failed_file_not_exists));
                        TUIChatLog.e(TAG, "start send video error data: " + data);
                    } else if (mMessageHandler != null) {
                        mMessageHandler.sendMessage(msg);
                        hideSoftInput();
                    }
                } else if (mimeType.contains("image")){
                    TUIMessageBean info = ChatMessageBuilder.buildImageMessage((Uri) data);
                    if (info == null) {
                        TUIChatLog.e(TAG, "start send image error data: " + data);
                        ToastUtil.toastShortMessage(getResources().getString(R.string.send_failed_file_not_exists));
                        return;
                    }
                    if (mMessageHandler != null) {
                        mMessageHandler.sendMessage(info);
                        hideSoftInput();
                    }
                } else {
                    TUIChatLog.e(TAG, "Send photo or video failed , invalid mimeType : " + mimeType);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIChatLog.i(TAG, "errCode: " + errCode);
                ToastUtil.toastLongMessage(errMsg);
            }
        });
    }

    private TUIMessageBean buildVideoMessage(String mUri) {
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            mmr.setDataSource(mUri);
            String sDuration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
            Bitmap bitmap = mmr.getFrameAtTime(0, android.media.MediaMetadataRetriever.OPTION_NEXT_SYNC);//缩略图

            if (bitmap == null){
                TUIChatLog.e(TAG, "buildVideoMessage() bitmap is null");
                return null;
            }

            String imgPath = FileUtil.saveBitmap("JCamera", bitmap);
            String videoPath = mUri;
            int imgWidth = bitmap.getWidth();
            int imgHeight = bitmap.getHeight();
            long duration = Long.valueOf(sDuration);
            TUIMessageBean msg = ChatMessageBuilder.buildVideoMessage(imgPath, videoPath, imgWidth, imgHeight, duration);

            return msg;
        } catch (Exception ex) {
            TUIChatLog.e(TAG, "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }

        return null;
    }

    protected void startCapture() {
        TUIChatLog.i(TAG, "startCapture");

        PermissionHelper.requestPermission(PermissionHelper.PERMISSION_CAMERA, new PermissionHelper.PermissionCallback() {
            @Override
            public void onGranted() {
                Intent captureIntent = new Intent(getContext(), CameraActivity.class);
                captureIntent.putExtra(TUIChatConstants.CAMERA_TYPE, JCameraView.BUTTON_STATE_ONLY_CAPTURE);
                CameraActivity.mCallBack = new IUIKitCallback() {
                    @Override
                    public void onSuccess(Object data) {
                        Uri contentUri = Uri.fromFile(new File(data.toString()));
                        TUIMessageBean msg = ChatMessageBuilder.buildImageMessage(contentUri);
                        if (mMessageHandler != null) {
                            mMessageHandler.sendMessage(msg);
                            hideSoftInput();
                        }
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {

                    }
                };
                setOpenPhotoCallback();
                mInputMoreFragment.startActivityForResult(captureIntent, InputMoreFragment.REQUEST_CODE_PHOTO);
            }

            @Override
            public void onDenied() {
                TUIChatLog.i(TAG, "startCapture checkPermission failed");
            }
        });
    }

    protected void startVideoRecord() {
        TUIChatLog.i(TAG, "startVideoRecord");

        PermissionHelper.requestPermission(PermissionHelper.PERMISSION_CAMERA, new PermissionHelper.PermissionCallback() {
            @Override
            public void onGranted() {
                PermissionHelper.requestPermission(PermissionHelper.PERMISSION_MICROPHONE, new PermissionHelper.PermissionCallback() {
                    @Override
                    public void onGranted() {
                        Intent captureIntent = new Intent(getContext(), CameraActivity.class);
                        captureIntent.putExtra(TUIChatConstants.CAMERA_TYPE, JCameraView.BUTTON_STATE_ONLY_RECORDER);
                        CameraActivity.mCallBack = new IUIKitCallback() {
                            @Override
                            public void onSuccess(Object data) {
                                Intent videoData = (Intent) data;
                                String imgPath = videoData.getStringExtra(TUIChatConstants.CAMERA_IMAGE_PATH);
                                String videoPath = videoData.getStringExtra(TUIChatConstants.CAMERA_VIDEO_PATH);
                                int imgWidth = videoData.getIntExtra(TUIChatConstants.IMAGE_WIDTH, 0);
                                int imgHeight = videoData.getIntExtra(TUIChatConstants.IMAGE_HEIGHT, 0);
                                long duration = videoData.getLongExtra(TUIChatConstants.VIDEO_TIME, 0);
                                TUIMessageBean msg = ChatMessageBuilder.buildVideoMessage(imgPath, videoPath, imgWidth, imgHeight, duration);
                                if (mMessageHandler != null) {
                                    mMessageHandler.sendMessage(msg);
                                    hideSoftInput();
                                }
                            }

                            @Override
                            public void onError(String module, int errCode, String errMsg) {

                            }
                        };
                        setOpenPhotoCallback();
                        mInputMoreFragment.startActivityForResult(captureIntent, InputMoreFragment.REQUEST_CODE_PHOTO);
                    }

                    @Override
                    public void onDenied() {
                        TUIChatLog.i(TAG, "startVideoRecord checkPermission failed");
                    }
                });
            }

            @Override
            public void onDenied() {
                TUIChatLog.i(TAG, "startVideoRecord checkPermission failed");
            }
        });

    }

    protected void startSendFile() {
        TUIChatLog.i(TAG, "startSendFile");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        mInputMoreFragment.setCallback(new IUIKitCallback() {
            @Override
            public void onSuccess(Object data) {
                TUIMessageBean info = ChatMessageBuilder.buildFileMessage((Uri) data);
                if (info == null) {
                    ToastUtil.toastShortMessage(getResources().getString(R.string.send_failed_file_not_exists));
                    return;
                }
                if (mMessageHandler != null) {
                    mMessageHandler.sendMessage(info);
                    hideSoftInput();
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastLongMessage(errMsg);
            }
        });
        mInputMoreFragment.startActivityForResult(intent, InputMoreFragment.REQUEST_CODE_FILE);
    }

    public void setChatInputHandler(ChatInputHandler handler) {
        this.mChatInputHandler = handler;
    }

    public MessageHandler getMessageHandler(){
        return this.mMessageHandler;
    }

    public void setMessageHandler(MessageHandler handler) {
        this.mMessageHandler = handler;
    }

    public void setStartActivityListener(OnStartActivityListener listener) {
        this.mStartActivityListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mChatLayout.getChatInfo() == null){
            ToastUtil.toastShortMessage(TUIChatService.getAppContext().getString(R.string.playfun_inputview_text1));
            return;
        }

        TUIChatLog.i(TAG, "onClick id:" + view.getId()
                + "|voice_input_switch:" + R.id.voice_input_switch
                + "|face_btn:" + R.id.face_btn
                + "|send_btn:" + R.id.send_btn
                + "|mCurrentState:" + mCurrentState
                + "|mSendEnable:" + mSendEnable
                + "|mMoreInputEvent:" + mMoreInputEvent);
        if (view.getId() == R.id.voice_input_switch) {
            if (mCurrentState == STATE_FACE_INPUT || mCurrentState == STATE_ACTION_INPUT) {
                mCurrentState = STATE_VOICE_INPUT;
                mInputMoreView.setVisibility(View.GONE);
            } else if (mCurrentState == STATE_SOFT_INPUT) {
                mCurrentState = STATE_VOICE_INPUT;
            } else {
                mCurrentState = STATE_SOFT_INPUT;
            }
            if (mCurrentState == STATE_VOICE_INPUT) {
                mSendAudioButton.setVisibility(VISIBLE);
                ll_input.setVisibility(GONE);
                mAudioInputSwitchButton.setImageResource(R.drawable.chat_input_keyboard);
                hideSoftInput();
            } else {
                mAudioInputSwitchButton.setImageResource(R.drawable.action_audio_selector);
                mSendAudioButton.setVisibility(GONE);
                ll_input.setVisibility(VISIBLE);
                showSoftInput();
            }
        } else if (view.getId() == R.id.face_btn) {
            mAudioInputSwitchButton.setImageResource(R.drawable.action_audio_selector);
            if (mCurrentState == STATE_VOICE_INPUT) {
                mCurrentState = STATE_NONE_INPUT;
                mSendAudioButton.setVisibility(GONE);
                ll_input.setVisibility(VISIBLE);
            }
            if (mCurrentState == STATE_FACE_INPUT) {
                mCurrentState = STATE_SOFT_INPUT;
                mInputMoreView.setVisibility(View.GONE);
                ll_input.setVisibility(VISIBLE);
//                showSoftInput();
            } else {
                mCurrentState = STATE_FACE_INPUT;
                showFaceViewGroup();
            }
        } else if (view.getId() == R.id.send_btn) {
            if (mSendEnable) {
                if (mMessageHandler != null) {
                    if (mChatLayout == null) {
                        mMessageHandler.sendMessage(ChatMessageBuilder.buildTextMessage(mTextInput.getText().toString().trim()));
                    } else {
                        if (isReplyModel && replyPreviewBean != null) {
                            if (mChatLayout.getChatInfo()!=null && TUIChatUtils.isGroupChat(mChatLayout.getChatInfo().getType()) && !mTextInput.getMentionIdList().isEmpty()) {
                                List<String> atUserList = new ArrayList<>(mTextInput.getMentionIdList());
                                mMessageHandler.sendMessage(ChatMessageBuilder.buildAtReplyMessage(mTextInput.getText().toString().trim(), atUserList, replyPreviewBean));
                            } else {
                                sendOnClickCallbacks.sendOnClickCallbackOk(mMessageHandler,ChatMessageBuilder.buildTextMessage(mTextInput.getText().toString().trim()));
//                                mMessageHandler.sendMessage(ChatMessageBuilder.buildReplyMessage(mTextInput.getText().toString().trim(), replyPreviewBean));
                            }
                            exitReply();
                        } else {
                            if (mChatLayout.getChatInfo()!=null && TUIChatUtils.isGroupChat(mChatLayout.getChatInfo().getType()) && !mTextInput.getMentionIdList().isEmpty()) {
                                //发送时通过获取输入框匹配上@的昵称list，去从map中获取ID list。
                                List<String> atUserList = new ArrayList<>(mTextInput.getMentionIdList());
                                if (atUserList.isEmpty()) {
                                    mMessageHandler.sendMessage(ChatMessageBuilder.buildTextMessage(mTextInput.getText().toString().trim()));
                                } else {
                                    mMessageHandler.sendMessage(ChatMessageBuilder.buildTextAtMessage(atUserList, mTextInput.getText().toString().trim()));
                                }
                            } else {
//                                mMessageHandler.sendMessage(ChatMessageBuilder.buildTextMessage(mTextInput.getText().toString().trim()));
                                sendOnClickCallbacks.sendOnClickCallbackOk(mMessageHandler,ChatMessageBuilder.buildTextMessage(mTextInput.getText().toString().trim()));
                            }
                        }
                    }
                }
                mTextInput.setText("");
            }
        }
    }

    public void showSoftInput() {
        TUIChatLog.i(TAG, "showSoftInput");
        hideInputMoreLayout();
        mAudioInputSwitchButton.setImageResource(R.drawable.action_audio_selector);
        mCurrentState = STATE_SOFT_INPUT;
        mSendAudioButton.setVisibility(GONE);
        ll_input.setVisibility(VISIBLE);

        mTextInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!isSoftInputShown()) {
            imm.toggleSoftInput(0, 0);
        }
        if (mChatInputHandler != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mChatInputHandler.onInputAreaClick();
                }
            }, 200);
        }
    }

    public void hideSoftInput() {
        TUIChatLog.i(TAG, "hideSoftInput");
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTextInput.getWindowToken(), 0);
        mTextInput.clearFocus();
        mInputMoreView.setVisibility(View.GONE);
    }

    public void onEmptyClick() {
        if (mCurrentState != STATE_VOICE_INPUT){
            hideSoftInput();
            mCurrentState = STATE_SOFT_INPUT;
        }
//        mAudioInputSwitchButton.setImageResource(R.drawable.action_audio_selector);
//        mSendAudioButton.setVisibility(GONE);
//        ll_input.setVisibility(VISIBLE);
    }

    private boolean isSoftInputShown() {
        View decorView = ((Activity) getContext()).getWindow().getDecorView();
        int screenHeight = decorView.getHeight();
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        return screenHeight - rect.bottom - getNavigateBarHeight() >= 0;
    }

    // 兼容有导航键的情况
    private int getNavigateBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager  = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    public void disableShowCustomFace(boolean disable) {
        isShowCustomFace = !disable;
    }

    private void showFaceViewGroup() {
        TUIChatLog.i(TAG, "showFaceViewGroup");
        if (mFragmentManager == null) {
            mFragmentManager = mActivity.getSupportFragmentManager();
        }
        if (mFaceFragment == null) {
            mFaceFragment = new FaceFragment();
        }
        hideSoftInput();
        mInputMoreView.setVisibility(View.VISIBLE);
        mTextInput.requestFocus();
        mFaceFragment.setShowCustomFace(isShowCustomFace);
        mFaceFragment.setListener(new FaceFragment.OnEmojiClickListener() {
            @Override
            public void onEmojiDelete() {
                int index = mTextInput.getSelectionStart();
                Editable editable = mTextInput.getText();
                boolean isFace = false;
                if (index <= 0) {
                    return;
                }
                if (editable.charAt(index - 1) == ']') {
                    for (int i = index - 2; i >= 0; i--) {
                        if (editable.charAt(i) == '[') {
                            String faceChar = editable.subSequence(i, index).toString();
                            if (FaceManager.isFaceChar(faceChar)) {
                                editable.delete(i, index);
                                isFace = true;
                            }
                            break;
                        }
                    }
                }
                if (!isFace) {
                    editable.delete(index - 1, index);
                }
            }

            @Override
            public void onEmojiClick(Emoji emoji) {
                if (emoji == null)return;
                int index = mTextInput.getSelectionStart();
                Editable editable = mTextInput.getText();
                editable.insert(index, emoji.getFilter());
                FaceManager.handlerEmojiText(mTextInput, editable.toString(), true);
            }

            @Override
            public void onCustomFaceClick(int groupIndex, Emoji emoji) {
                mMessageHandler.sendMessage(ChatMessageBuilder.buildFaceMessage(groupIndex, emoji.getFilter()));
            }
        });
        mFragmentManager.beginTransaction().replace(R.id.more_groups, mFaceFragment).commitAllowingStateLoss();
        if (mChatInputHandler != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mChatInputHandler.onInputAreaClick();
                }
            }, 100);
        }
    }

    private void showCustomInputMoreFragment() {
        TUIChatLog.i(TAG, "showCustomInputMoreFragment");
        if (mFragmentManager == null) {
            mFragmentManager = mActivity.getSupportFragmentManager();
        }
        BaseInputFragment fragment = (BaseInputFragment) mMoreInputEvent;
        hideSoftInput();
        mInputMoreView.setVisibility(View.VISIBLE);
        mFragmentManager.beginTransaction().replace(R.id.more_groups, fragment).commitAllowingStateLoss();
        if (mChatInputHandler != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mChatInputHandler.onInputAreaClick();
                }
            }, 100);
        }
    }

    private void showInputMoreLayout() {
        TUIChatLog.i(TAG, "showInputMoreLayout");
        if (mFragmentManager == null) {
            mFragmentManager = mActivity.getSupportFragmentManager();
        }
        if (mInputMoreFragment == null) {
            mInputMoreFragment = new InputMoreFragment();
        }

        assembleActions();
        mInputMoreFragment.setActions(mInputMoreActionList);
        hideSoftInput();
        mInputMoreView.setVisibility(View.VISIBLE);
        mFragmentManager.beginTransaction().replace(R.id.more_groups, mInputMoreFragment).commitAllowingStateLoss();
        if (mChatInputHandler != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mChatInputHandler.onInputAreaClick();
                }
            }, 100);
        }
    }

    private void hideInputMoreLayout() {
        mInputMoreView.setVisibility(View.GONE);
    }

    private void recordComplete(boolean success) {
        int duration = AudioPlayer.getInstance().getDuration();
        TUIChatLog.i(TAG, "recordComplete duration:" + duration);
        if (mChatInputHandler != null) {
            if (!success || duration == 0) {
                mChatInputHandler.onRecordStatusChanged(ChatInputHandler.RECORD_FAILED);
                return;
            }
            if (mAudioCancel) {
                mChatInputHandler.onRecordStatusChanged(ChatInputHandler.RECORD_CANCEL);
                return;
            }
            if (duration < 1000) {
                mChatInputHandler.onRecordStatusChanged(ChatInputHandler.RECORD_TOO_SHORT);
                return;
            }
            mChatInputHandler.onRecordStatusChanged(ChatInputHandler.RECORD_STOP);
        }

        if (mMessageHandler != null && success) {
            //发送拦截。
            sendOnClickCallbacks.sendOnClickAudioMessage(mMessageHandler,ChatMessageBuilder.buildAudioMessage(AudioPlayer.getInstance().getPath(), duration));
//            mMessageHandler.sendMessage(ChatMessageBuilder.buildAudioMessage(AudioPlayer.getInstance().getPath(), duration));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mInputContent = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString().trim())) {
            mSendEnable = false;
            showSendTextButton(View.GONE);
        } else {
            mSendEnable = true;
            showSendTextButton(View.VISIBLE);
            if (mTextInput.getLineCount() != mLastMsgLineCount) {
                mLastMsgLineCount = mTextInput.getLineCount();
                if (mChatInputHandler != null) {
                    mChatInputHandler.onInputAreaClick();
                }
            }
            if (!TextUtils.equals(mInputContent, mTextInput.getText().toString())) {
                FaceManager.handlerEmojiText(mTextInput, mTextInput.getText().toString(), true);
            }
        }

    }

    public void setDraft() {
        if (mChatInfo == null) {
            TUIChatLog.e(TAG, "set drafts error :  chatInfo is null");
            return;
        }
        if (mTextInput == null) {
            TUIChatLog.e(TAG, "set drafts error :  textInput is null");
            return;
        }

        String draftText = mTextInput.getText().toString();
        if (isReplyModel && replyPreviewBean != null) {
            Gson gson = new Gson();
            Map<String, String> draftMap = new HashMap<>();
            draftMap.put("content", draftText);
            draftMap.put("reply", gson.toJson(replyPreviewBean));
            draftText = gson.toJson(draftMap);
        }
        if (presenter != null) {
            presenter.setDraft(draftText);
        }
    }

    public void appendText(String text) {
        if (mChatInfo == null) {
            TUIChatLog.e(TAG, "appendText error :  chatInfo is null");
            return;
        }
        if (mTextInput == null) {
            TUIChatLog.e(TAG, "appendText error :  textInput is null");
            return;
        }
        String draftText = mTextInput.getText().toString();
        draftText += text;
        mTextInput.setText(draftText);
        mTextInput.setSelection(mTextInput.getText().length());
    }

    public void setChatInfo(ChatInfo chatInfo) {
        mChatInfo = chatInfo;
        if (chatInfo != null) {
            DraftInfo draftInfo = chatInfo.getDraft();
            if (draftInfo != null && !TextUtils.isEmpty(draftInfo.getDraftText()) && mTextInput != null) {
                Gson gson = new Gson();
                HashMap draftJsonMap;
                String content = draftInfo.getDraftText();
                try {
                    draftJsonMap = gson.fromJson(draftInfo.getDraftText(), HashMap.class);
                    if (draftJsonMap != null) {
                        content = (String) draftJsonMap.get("content");
                        String draftStr = (String) draftJsonMap.get("reply");
                        ReplyPreviewBean bean = gson.fromJson(draftStr, ReplyPreviewBean.class);
                        if (bean != null) {
                            showReplyPreview(bean);
                        }
                    }
                } catch (JsonSyntaxException e) {
                    TUIChatLog.e(TAG, " getCustomJsonMap error ");
                }

                mTextInput.setText(content);
                mTextInput.setSelection(mTextInput.getText().length());
            }
        }
    }

    //DL add 彭石林新增
    public void startAudioCall(){
        if (mChatInfo == null) {
            return;
        }
        HashMap<String, Object> param = new HashMap<>();
        param.put(TUIConstants.TUIChat.CHAT_ID, mChatInfo.getId());
        param.put(TUIConstants.TUIChat.CHAT_NAME, mChatInfo.getChatName());
        param.put(TUIConstants.TUIChat.CHAT_TYPE, mChatInfo.getType());
        param.put(TUIConstants.TUIChat.CONTEXT, getContext());
        Map<String, Object> audioCallExtension = TUICore.getExtensionInfo(TUIConstants.TUIChat.EXTENSION_INPUT_MORE_AUDIO_CALL, param);
        if (audioCallExtension != null) {
            int audioActionId = (Integer) audioCallExtension.get(TUIConstants.TUIChat.INPUT_MORE_ACTION_ID);
            onCustomActionClick(audioActionId);
        }
    }

    public void setChatLayout(IChatLayout chatLayout) {
        mChatLayout = chatLayout;
    }

    protected void assembleActions() {
        mInputMoreActionList.clear();
        InputMoreActionUnit actionUnit;
        if (!mSendPhotoDisable) {
            actionUnit = new InputMoreActionUnit() {
                @Override
                public void onAction(String chatInfoId, int chatType) {
                    startSendPhoto();
                }
            };
            actionUnit.setIconResId(R.drawable.ic_more_picture);
            actionUnit.setTitleId(R.string.pic);
            mInputMoreActionList.add(actionUnit);
        }

        if (!mCaptureDisable) {
            actionUnit = new InputMoreActionUnit() {
                @Override
                public void onAction(String chatInfoId, int chatType) {
                    startCapture();
                }
            };
            actionUnit.setIconResId(R.drawable.ic_more_camera);
            actionUnit.setTitleId(R.string.photo);
            mInputMoreActionList.add(actionUnit);
        }

        if (!mVideoRecordDisable) {
            actionUnit = new InputMoreActionUnit() {
                @Override
                public void onAction(String chatInfoId, int chatType) {
                    startVideoRecord();
                }
            };
            actionUnit.setIconResId(R.drawable.ic_more_video);
            actionUnit.setTitleId(R.string.video);
            mInputMoreActionList.add(actionUnit);
        }

        if (!mSendFileDisable) {
            actionUnit = new InputMoreActionUnit() {
                @Override
                public void onAction(String chatInfoId, int chatType) {
                    startSendFile();
                }
            };
            actionUnit.setIconResId(R.drawable.ic_more_file);
            actionUnit.setTitleId(R.string.file);
            mInputMoreActionList.add(actionUnit);
        }

        addActionsFromListeners();
        mInputMoreActionList.addAll(mInputMoreCustomActionList);
        // 按照优先级排序
        Collections.sort(mInputMoreActionList, new Comparator<InputMoreActionUnit>() {
            @Override
            public int compare(InputMoreActionUnit o1, InputMoreActionUnit o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
    }

    private void addActionsFromListeners() {
        if (mChatInfo == null) {
            return;
        }

        HashMap<String, Object> param = new HashMap<>();
        param.put(TUIConstants.TUIChat.CHAT_ID, mChatInfo.getId());
        param.put(TUIConstants.TUIChat.CHAT_NAME, mChatInfo.getChatName());
        param.put(TUIConstants.TUIChat.CHAT_TYPE, mChatInfo.getType());
        param.put(TUIConstants.TUIChat.CONTEXT, getContext());
        Map<String, Object> audioCallExtension = TUICore.getExtensionInfo(TUIConstants.TUIChat.EXTENSION_INPUT_MORE_AUDIO_CALL, param);
        if (audioCallExtension != null) {
            View audioView = (View) audioCallExtension.get(TUIConstants.TUIChat.INPUT_MORE_VIEW);
            int audioActionId = (Integer) audioCallExtension.get(TUIConstants.TUIChat.INPUT_MORE_ACTION_ID);
            InputMoreActionUnit audioUnit = new InputMoreActionUnit();
            audioUnit.setActionId(audioActionId);
            audioUnit.setUnitView(audioView);
            audioUnit.setPriority(2);
            audioUnit.setOnClickListener(audioUnit.new OnActionClickListener() {
                @Override
                public void onClick() {
                    PermissionHelper.requestPermission(PermissionHelper.PERMISSION_MICROPHONE, new PermissionHelper.PermissionCallback() {
                        @Override
                        public void onGranted() {
                            onCustomActionClick(audioUnit.getActionId());
                        }

                        @Override
                        public void onDenied() {

                        }
                    });
                }
            });
            mInputMoreActionList.add(audioUnit);
        }

        Map<String, Object> videoCallExtension = TUICore.getExtensionInfo(TUIConstants.TUIChat.EXTENSION_INPUT_MORE_VIDEO_CALL, param);
        if (videoCallExtension != null) {
            View videoView = (View) videoCallExtension.get(TUIConstants.TUIChat.INPUT_MORE_VIEW);
            int videoActionId = (Integer) videoCallExtension.get(TUIConstants.TUIChat.INPUT_MORE_ACTION_ID);
            InputMoreActionUnit videoUnit = new InputMoreActionUnit();
            videoUnit.setActionId(videoActionId);
            videoUnit.setUnitView(videoView);
            videoUnit.setPriority(1);
            videoUnit.setOnClickListener(videoUnit.new OnActionClickListener() {
                @Override
                public void onClick() {
                    PermissionHelper.requestPermission(PermissionHelper.PERMISSION_MICROPHONE, new PermissionHelper.PermissionCallback() {
                        @Override
                        public void onGranted() {
                            PermissionHelper.requestPermission(PermissionHelper.PERMISSION_CAMERA, new PermissionHelper.PermissionCallback() {
                                @Override
                                public void onGranted() {
                                    onCustomActionClick(videoUnit.getActionId());
                                }

                                @Override
                                public void onDenied() {

                                }
                            });
                        }

                        @Override
                        public void onDenied() {

                        }
                    });
                }
            });
            mInputMoreActionList.add(videoUnit);
        }

        Map<String, Object> customMessageExtension = TUICore.getExtensionInfo(TUIConstants.TUIChat.EXTENSION_INPUT_MORE_CUSTOM_MESSAGE, param);
        if (customMessageExtension != null) {
            Integer icon = (Integer) customMessageExtension.get(TUIConstants.TUIChat.INPUT_MORE_ICON);
            Integer title = (Integer) customMessageExtension.get(TUIConstants.TUIChat.INPUT_MORE_TITLE);
            Integer id = (Integer) customMessageExtension.get(TUIConstants.TUIChat.INPUT_MORE_ACTION_ID);
            InputMoreActionUnit unit = new InputMoreActionUnit();
            unit.setActionId(id);
            unit.setIconResId(icon);
            unit.setTitleId(title);
            unit.setPriority(10);
            unit.setOnClickListener(unit.new OnActionClickListener() {
                @Override
                public void onClick() {
                    onCustomActionClick(unit.getActionId());
                }
            });
            mInputMoreActionList.add(unit);
        }
    }

    private void onCustomActionClick(int id) {
        if (id == TUIConstants.TUICalling.ACTION_ID_AUDIO_CALL || id == TUIConstants.TUICalling.ACTION_ID_VIDEO_CALL) {
            String type = id == TUIConstants.TUICalling.ACTION_ID_AUDIO_CALL ? TUIConstants.TUICalling.TYPE_AUDIO
                    : TUIConstants.TUICalling.TYPE_VIDEO;
            if (TUIChatUtils.isGroupChat(getChatInfo().getType())) {
                Bundle bundle = new Bundle();
                bundle.putString(TUIConstants.TUICalling.GROUP_ID, getChatInfo().getId());
                bundle.putString(TUIConstants.TUICalling.PARAM_NAME_TYPE, type);
                bundle.putString(TUIChatConstants.GROUP_ID, getChatInfo().getId());
                bundle.putBoolean(TUIChatConstants.SELECT_FOR_CALL, true);
                bundle.putInt(TUIChatConstants.Selection.LIMIT, CALL_MEMBER_LIMIT);
                TUICore.startActivity(getContext(), "StartGroupMemberSelectActivity", bundle, 11);
            }
            return;
        }
        HashMap<String, Object> param = new HashMap<>();
        param.put(TUIConstants.TUIChat.INPUT_MORE_ACTION_ID, id);
        param.put(TUIConstants.TUIChat.CHAT_ID, mChatInfo.getId());
        param.put(TUIConstants.TUIChat.CHAT_NAME, mChatInfo.getChatName());
        param.put(TUIConstants.TUIChat.CHAT_TYPE, mChatInfo.getType());
        TUICore.notifyEvent(TUIConstants.TUIChat.EVENT_KEY_INPUT_MORE, TUIConstants.TUIChat.EVENT_SUB_KEY_ON_CLICK, param);
    }

    public void disableAudioInput(boolean disable) {
        mAudioInputDisable = disable;
        if (disable) {
            mAudioInputSwitchButton.setVisibility(GONE);
        } else {
            mAudioInputSwitchButton.setVisibility(VISIBLE);
        }
    }

    public void disableEmojiInput(boolean disable) {
        mEmojiInputDisable = disable;
        if (disable) {
            mEmojiInputButton.setVisibility(GONE);
        } else {
            mEmojiInputButton.setVisibility(VISIBLE);
        }
    }

    public void disableMoreInput(boolean disable) {
        mMoreInputDisable = disable;
        if (disable) {
//            mMoreInputButton.setVisibility(GONE);
            mSendTextButton.setVisibility(VISIBLE);
        } else {
//            mMoreInputButton.setVisibility(VISIBLE);
            mSendTextButton.setVisibility(GONE);
        }
    }

    public void replaceMoreInput(BaseInputFragment fragment) {
        mMoreInputEvent = fragment;
    }

    public void replaceMoreInput(OnClickListener listener) {
        mMoreInputEvent = listener;
    }

    public void disableSendPhotoAction(boolean disable) {
        mSendPhotoDisable = disable;
    }

    public void disableCaptureAction(boolean disable) {
        mCaptureDisable = disable;
    }

    public void disableVideoRecordAction(boolean disable) {
        mVideoRecordDisable = disable;
    }

    public void disableSendFileAction(boolean disable) {
        mSendFileDisable = disable;
    }

    public void addAction(InputMoreActionUnit action) {
        mInputMoreCustomActionList.add(action);
    }

    public EditText getInputText() {
        return mTextInput;
    }

    protected void showMoreInputButton(int visibility) {
        if (mMoreInputDisable) {
            return;
        }
    }

    protected void showSendTextButton(int visibility) {
        if (mMoreInputDisable) {
            mSendTextButton.setVisibility(VISIBLE);
        } else {
            mSendTextButton.setVisibility(visibility);
        }
    }

    public void showReplyPreview(ReplyPreviewBean previewBean) {
        isReplyModel = true;
        replyPreviewBean = previewBean;
        String replyMessageAbstract = previewBean.getMessageAbstract();
        String msgTypeStr = ChatMessageParser.getMsgTypeStr(previewBean.getMessageType());
        // 如果是回复文字消息，缩略显示文件名中间部分
        if (previewBean.getOriginalMessageBean() instanceof FileMessageBean) {
            replyTv.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        } else {
            replyTv.setEllipsize(TextUtils.TruncateAt.END);
        }
        String text = previewBean.getMessageSender() + " : " + msgTypeStr + " " + replyMessageAbstract;
        text = FaceManager.emojiJudge(text);
        replyTv.setText(text);
//        replyLayout.setVisibility(View.VISIBLE);
        if (mMessageHandler != null) {
            mMessageHandler.scrollToEnd();
        }

        showSoftInput();
    }



    public void exitReply() {
        isReplyModel = false;
        replyPreviewBean = null;
//        replyLayout.setVisibility(View.GONE);
    }

    protected void showEmojiInputButton(int visibility) {
        if (mEmojiInputDisable) {
            return;
        }
        mEmojiInputButton.setVisibility(visibility);
    }

    public void clearCustomActionList() {
        mInputMoreCustomActionList.clear();
    }

    public ChatInfo getChatInfo() {
        return mChatInfo;
    }

    public interface MessageHandler {
        void sendMessage(TUIMessageBean msg);
        void scrollToEnd();
    }

    public interface ChatInputHandler {

        int RECORD_START = 1;
        int RECORD_STOP = 2;
        int RECORD_CANCEL = 3;
        int RECORD_TOO_SHORT = 4;
        int RECORD_FAILED = 5;

        void onInputAreaClick();

        void onRecordStatusChanged(int status);
        void onTouchFlag(boolean isMove);
    }

    public interface OnStartActivityListener {
        void onStartGroupMemberSelectActivity();
    }

    public interface SendOnClickCallback{
        void sendOnClickCallbackOk(MessageHandler messageHandler,TUIMessageBean messageInfo);
        void sendOnClickAudioMessage(MessageHandler messageHandler,TUIMessageBean messageInfo);
        void onClickPhoneVideo();
        void onClickGift();

        void onClickCallAudio();
        void onClickCallVideo();
        void applyAudioPermission();

        void sendBlackStatus(int status);


        //简体表情按钮是否展开
        void onChangedVideoLayout(int height);
    }

}