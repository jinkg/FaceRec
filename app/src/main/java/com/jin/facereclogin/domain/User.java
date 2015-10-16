package com.jin.facereclogin.domain;

/**
 * Created by 雅麟 on 2015/6/9.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.jin.gesturepassword.widget.LockPatternUtils;


/**
 * Created by 雅麟 on 2015/6/9.
 */
public class User {

    private static final String USER_SP = "login";

    public static final String USER_ACCOUNT_KEY = "user_account";
    public static final String USER_PASSWORD_KEY = "user_password";

    public String userAccount;
    public String userPassword;


    public static SharedPreferences getUserSp(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                USER_SP, Context.MODE_PRIVATE);
        return sp;
    }

    /**
     * @param context recommend use application context
     * @return
     */
    public static boolean isLogin(Context context) {
        if (context == null) {
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(
                USER_SP, Context.MODE_PRIVATE);
        String userId = sp.getString(USER_ACCOUNT_KEY, null);
        return !TextUtils.isEmpty(userId);
    }

    public static String getUserAccount(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                USER_SP, Context.MODE_PRIVATE);
        return sp.getString(USER_ACCOUNT_KEY, null);
    }

    /**
     * when login success, should store user info
     *
     * @param context recommend use application context
     * @return
     */
    public static void storeUserInfo(Context context, User user) {
        if (user == null) {
            throw new IllegalArgumentException("user can not be null");
        }
        SharedPreferences sp = context.getSharedPreferences(
                USER_SP, Context.MODE_PRIVATE);
        sp.edit().putString(USER_ACCOUNT_KEY, user.userAccount)
                .apply();
    }

    /**
     * when logout success, should delete user info
     *
     * @param context recommend use application context
     * @return
     */
    public static void deleteUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                USER_SP, Context.MODE_PRIVATE);
        sp.edit().putString(USER_ACCOUNT_KEY, null)
                .apply();
        LockPatternUtils.getInstance(context).clearLock();
        Face.enableLock(context, false);
    }
}
