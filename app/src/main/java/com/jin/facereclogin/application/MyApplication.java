package com.jin.facereclogin.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by 雅麟 on 2015/4/23.
 */
public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

}
