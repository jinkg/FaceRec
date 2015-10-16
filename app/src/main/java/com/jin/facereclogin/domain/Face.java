package com.jin.facereclogin.domain;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by YaLin on 2015/10/10.
 */
public class Face {

    private static final String FACE_SP = "face";

    public static final String ENABLE_KEY = "enable";
    public static boolean sVerified = false;

    public String face_id;

    public static boolean isEnable(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                FACE_SP, Context.MODE_PRIVATE);
        return sp.getBoolean(ENABLE_KEY, false);
    }

    public static void enableLock(Context context, boolean enable) {
        SharedPreferences sp = context.getSharedPreferences(
                FACE_SP, Context.MODE_PRIVATE);
        sp.edit().putBoolean(ENABLE_KEY, enable)
                .apply();
    }

    public static void setVerified(boolean verified) {
        sVerified = verified;
    }
}
