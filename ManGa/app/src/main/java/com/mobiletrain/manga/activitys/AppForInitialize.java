package com.mobiletrain.manga.activitys;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by qf on 2016/10/20.
 */
public class AppForInitialize extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
