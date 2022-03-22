package com.dl.playfun.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.dl.playfun.R;
import com.dl.playfun.utils.StringUtil;

/**
 * Author: 彭石林
 * Time: 2021/12/13 16:58
 * Description: This is MessageDetailDialog
 */
public class MessageDetailDialog {
    /**
     * @return android.app.Dialog
     * @Desc TODO(弹出选择发送视频或者图片)
     * @author 彭石林
     * @parame [context, touchOutside, audioCallHintOnClickListener]
     * @Date 2022/3/1
     */
    public static Dialog CheckImgViewFile(Context context, boolean touchOutside, AudioCallHintOnClickListener audioCallHintOnClickListener) {
        Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(touchOutside);
        dialog.setCancelable(true);
        View view = View.inflate(context, R.layout.dialog_check_img_video, null);

        LinearLayout video_layout = view.findViewById(R.id.video_layout);
        LinearLayout image_layout = view.findViewById(R.id.image_layout);
        FrameLayout cancel_layout = view.findViewById(R.id.cancel_layout);
        video_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                audioCallHintOnClickListener.check1OnClick();
            }
        });
        image_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                audioCallHintOnClickListener.check2OnClick();
            }
        });
        cancel_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //设置背景透明,去四个角
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(view);
        //设置宽度充满屏幕
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        return dialog;
    }

    /**
     * @return android.app.Dialog
     * @Desc TODO(拨打语音视频唤起弹窗)
     * @author 彭石林
     * @parame [context, touchOutside, audioText, videoText, audioAndVideoCallOnClickListener]
     * @Date 2022/3/1
     */
    public static Dialog AudioAndVideoCallDialog(Context context, boolean touchOutside, String audioText, String videoText, AudioAndVideoCallOnClickListener audioAndVideoCallOnClickListener) {
        Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(touchOutside);
        dialog.setCancelable(true);
        View view = View.inflate(context, R.layout.dialog_audio_video_bottom, null);

        FrameLayout video_layout = view.findViewById(R.id.video_layout);
        FrameLayout audio_layout = view.findViewById(R.id.audio_layout);
        FrameLayout cancel_layout = view.findViewById(R.id.cancel_layout);
        if (!StringUtils.isEmpty(audioText)) {
            TextView audioTx = view.findViewById(R.id.audio_text);
            audioTx.setText(audioText);
        }
        if (!StringUtils.isEmpty(videoText)) {
            TextView videoTx = view.findViewById(R.id.video_text);
            videoTx.setText(videoText);
        }
        video_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                audioAndVideoCallOnClickListener.videoOnClick();
            }
        });
        audio_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                audioAndVideoCallOnClickListener.audioOnClick();
            }
        });
        cancel_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //设置背景透明,去四个角
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(view);
        //设置宽度充满屏幕
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        return dialog;
    }

    public static Dialog BgaCardDialog(final Context context, Integer type, String text, String hintText) {

        Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        View view = View.inflate(context, R.layout.alert_bag_gift_card, null);
        ImageView card_img = view.findViewById(R.id.card_img);
        ImageView ic_close = view.findViewById(R.id.ic_close);
        TextView title = view.findViewById(R.id.title);
        TextView hint_text = view.findViewById(R.id.hint_text);
        title.setText(text);
        hint_text.setText(hintText);
        if (type == 1) {
            card_img.setImageResource(R.drawable.alert_bag_gift_card_img4);
        } else if (type == 2) {
            card_img.setImageResource(R.drawable.alert_bag_gift_card_img1);
        } else if (type == 3) {
            card_img.setImageResource(R.drawable.alert_bag_gift_card_img2);
        } else if (type == 4) {
            card_img.setImageResource(R.drawable.alert_bag_gift_card_img3);
        }
        ic_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //设置背景透明,去四个角
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(view);
        //设置宽度充满屏幕
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        return dialog;
    }
    //挂断提示-男生
    public static Dialog callAudioHint(final Context context,AudioCallHintOnClickListener audioCallHintOnClickListener){
        Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        View view = View.inflate(context, R.layout.alert_call_audio_hint, null);
        TextView btn_txt_1 = view.findViewById(R.id.btn_txt_1);
        TextView btn_txt_2 = view.findViewById(R.id.btn_txt_2);
        Button btn_confirm = view.findViewById(R.id.btn_confirm);

        btn_txt_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                audioCallHintOnClickListener.check1OnClick();
            }
        });
        btn_txt_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                audioCallHintOnClickListener.check2OnClick();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //设置背景透明,去四个角
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(view);
        //设置宽度充满屏幕
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        return dialog;
    }

    //挂断提示-男生
    public static Dialog callAudioHint2(final Context context,String title,String content,AudioCallHintOnClickListener audioCallHintOnClickListener){
        Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        View view = View.inflate(context, R.layout.alert_call_audio_hint2, null);
        TextView titleText = view.findViewById(R.id.title);
        TextView titleText2 = view.findViewById(R.id.title_txt);
        if(StringUtils.isEmpty(title)){
            titleText2.setVisibility(View.GONE);
        }else{
            titleText2.setText(content);
        }
        if(!StringUtils.isEmpty(title)){
            titleText.setText(title);
        }
        TextView cancel = view.findViewById(R.id.cancel);
        Button btn_confirm = view.findViewById(R.id.btn_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                audioCallHintOnClickListener.check1OnClick();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                audioCallHintOnClickListener.check2OnClick();
            }
        });

        //设置背景透明,去四个角
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(view);
        //设置宽度充满屏幕
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        return dialog;
    }

    public static Dialog getImageDialog(Context context, String imagePath) {
        Dialog bottomDialog = new Dialog(context, R.style.ShowImageDialog);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_image_view, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.CENTER);
        ImageView imageView = contentView.findViewById(R.id.imageView);
        if (imagePath != null) {
//            GlideEngine.createGlideEngine().loadImage(context, StringUtil.getFullImageUrl(drawable), imageView);
            Glide.with(context)
                    .load(StringUtil.getFullImageUrl(imagePath))
                    .fitCenter()//防止部分账号图片被拉伸
//                    .error(R.drawable.radio_dating_img_default) //异常时候显示的图片
//                    .placeholder(R.drawable.radio_dating_img_default) //加载成功前显示的图片
//                    .fallback( R.drawable.radio_dating_img_default) //url为空的时候,显示的图片
                    .into(imageView);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });
        //设置背景透明,去四个角
        bottomDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //设置宽度充满屏幕
        Window window = bottomDialog.getWindow();
        window.setGravity(Gravity.CENTER); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        return bottomDialog;
    }

    public interface AudioAndVideoCallOnClickListener {
        void audioOnClick();

        void videoOnClick();
    }

    public interface AudioCallHintOnClickListener {
        void check1OnClick();

        void check2OnClick();
    }
}
