package com.jin.facereclogin.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jin.facereclogin.application.MyApplication;
import com.jin.facereclogin.domain.User;
import com.jin.facereclogin.receiver.SystemEventReceiver;
import com.jin.facereclogin.util.LockUtils;
import com.jin.gesturepassword.widget.LockPatternUtils;


/**
 * Created by YaLin on 2015/7/24.
 */
public class BaseActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    protected SystemEventReceiver systemEventReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User.getUserSp(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);

        final IntentFilter homeFilter = new IntentFilter();
        homeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        homeFilter.addAction(Intent.ACTION_SCREEN_OFF);
        homeFilter.addAction(Intent.ACTION_SCREEN_ON);
        homeFilter.addAction(Intent.ACTION_USER_PRESENT);

        systemEventReceiver = new SystemEventReceiver();
        registerReceiver(systemEventReceiver, homeFilter);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LockUtils.requestLock(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        User.getUserSp(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
        unregisterReceiver(systemEventReceiver);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (User.USER_ACCOUNT_KEY.equals(key)) {
            onLoginStateChanged();
        }
    }

    protected void onLoginStateChanged() {
    }
}


