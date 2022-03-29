package com.dl.playfun.kl.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.svideo.common.utils.FastClickUtil;
import com.dl.playfun.R;
import com.dl.playfun.app.AppContext;
import com.dl.playfun.event.CallChatingHangupEvent;
import com.tencent.liteav.trtccalling.model.TRTCCalling;
import com.tencent.liteav.trtccalling.model.TUICalling;

import me.goldze.mvvmhabit.bus.RxBus;

/**
 * @Name： PlayFun_Google
 * @Description：
 * @Author： liaosf
 * @Date： 2022/3/29 17:13
 * 修改备注：
 */
public class VideoPresetActivity extends AppCompatActivity {

    private RelativeLayout video_preset_container;
    private LinearLayout jm_line;
    private JMTUICallVideoView mCallView;
    private TUICalling.Role role;
    protected TRTCCalling mTRTCCalling;

    private String[] userIds =new String[1];
    private SeekBar seekbar_one;
    private SeekBar seekbar_two;
    private SeekBar seekbar_three;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_perset);
        mTRTCCalling = TRTCCalling.sharedInstance(this);
        video_preset_container = findViewById(R.id.video_preset_container);
        jm_line = findViewById(R.id.jm_line);
        role = TUICalling.Role.CALL;
        userIds[0] ="preset";
        mCallView = new JMTUICallVideoView(this, role, userIds, null, null, false) {
            @Override
            public void finish() {
                super.finish();
                //2秒最多发一次
            }
        };
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        video_preset_container.addView(mCallView, params);
        jm_line.bringToFront();
        initView();
    }

    private void initView() {
        seekbar_one = findViewById(R.id.seekbar_one);
        seekbar_two = findViewById(R.id.seekbar_two);
        seekbar_three = findViewById(R.id.seekbar_three);
        seekbar_one.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTRTCCalling.presetWhitenessLevel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbar_two.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTRTCCalling.presetBeautyLevel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbar_three.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTRTCCalling.presetRuddyLevel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
