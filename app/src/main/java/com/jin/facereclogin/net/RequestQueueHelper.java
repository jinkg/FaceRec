package com.jin.facereclogin.net;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;

/**
 * Created by YaLin on 2015/9/15.
 */
public class RequestQueueHelper {

    private static RequestQueue sInstance;

    public static RequestQueue getInstance() {
        return sInstance;
    }

    public static RequestQueue getInstance(Context paramContext) {
        if (sInstance == null) {
            sInstance = Volley.newRequestQueue(paramContext);
        }
        return sInstance;
    }

    public static RequestQueue newRequestQueue(Context paramContext, HttpStack paramHttpStack) {
        return Volley.newRequestQueue(paramContext, paramHttpStack);
    }
}
