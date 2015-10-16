package com.jin.facereclogin.net;


import android.support.v4.util.ArrayMap;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.jin.facereclogin.net.response.BaseResponse;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by 雅麟 on 2015/3/21.
 */
public class IDsManagerGetRequest<T extends BaseResponse> extends IDsManagerBaseRequest<T> {

    protected Map<String, JSONArray> mListParams;

    public IDsManagerGetRequest(String url, Class<T> cls, Map<String, String> header, Response.Listener listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, cls, header, listener, errorListener);
    }

    public IDsManagerGetRequest(String url, Class<T> cls, Response.Listener listener, Response.ErrorListener errorListener) {
        this(url, cls, null, listener, errorListener);
    }

    public void addListParams(String key, JSONArray array) {
        if (mListParams == null) {
            mListParams = new ArrayMap<>();
        }
        mListParams.put(key, array);
    }
}