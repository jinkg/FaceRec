package com.jin.facereclogin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jin.facereclogin.activity.FaceCameraActivity;
import com.jin.facereclogin.activity.UnlockGesturePasswordActivity;


/**
 * Created by 雅麟 on 2015/6/24.
 */
public class SystemEventReceiver extends BroadcastReceiver {
    private static final String TAG = "SystemEventReceiver";
    public static final String SYSTEM_DIALOG_REASON_KEY = "reason";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, action);
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (reason != null) {
                UnlockGesturePasswordActivity.lock();
                FaceCameraActivity.lock();
            }
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            UnlockGesturePasswordActivity.lock();
            FaceCameraActivity.lock();
        }
    }
}
