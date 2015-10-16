package com.jin.facereclogin.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.jin.facereclogin.activity.FaceCameraActivity;
import com.jin.facereclogin.activity.UnlockGesturePasswordActivity;
import com.jin.facereclogin.application.MyApplication;
import com.jin.facereclogin.domain.Face;
import com.jin.facereclogin.domain.User;
import com.jin.gesturepassword.widget.LockPatternUtils;

/**
 * Created by YaLin on 2015/10/12.
 */
public class LockUtils {
    private static final String LOCK_SP = "lock";

    public static final String LOCK_TYPE_KET = "lock_type";

    public static final int FACE_TYPE = 1;
    public static final int GESTURE_TYPE = 2;

    public static final int getLockType(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                LOCK_SP, Context.MODE_PRIVATE);
        return sp.getInt(LOCK_TYPE_KET, FACE_TYPE);
    }

    public static void setLockType(Context context, int type) {
        SharedPreferences sp = context.getSharedPreferences(
                LOCK_SP, Context.MODE_PRIVATE);
        sp.edit().putInt(LOCK_TYPE_KET, type)
                .apply();
    }


    public static void requestLock(Activity activity) {
        switch (getLockType(activity.getApplicationContext())) {
            case FACE_TYPE:
                if (Face.isEnable(activity.getApplicationContext()) && !FaceCameraActivity.isUnLock()) {
                    FaceCameraActivity.verify(activity, User.getUserAccount(activity.getApplicationContext()));
                }
                break;
            case GESTURE_TYPE:
                if (!UnlockGesturePasswordActivity.isUnLock() && LockPatternUtils.getInstance(activity).savedPatternExists(MyApplication.getContext())) {
                    UnlockGesturePasswordActivity.open(activity);
                } else {
                    UnlockGesturePasswordActivity.unLock();
                }
                break;
        }
    }
}
