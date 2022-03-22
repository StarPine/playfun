package com.tencent.qcloud.tuicore.util;

import android.os.Handler;
import android.os.Looper;


public class BackgroundTasks {

    private static final BackgroundTasks instance = new BackgroundTasks();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private BackgroundTasks() {}

    public static BackgroundTasks getInstance() {
        return instance;
    }

    public void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public boolean postDelayed(Runnable r, long delayMillis) {
        return handler.postDelayed(r, delayMillis);
    }
}
