package com.jin.facereclogin.net;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.jin.facereclogin.net.response.BaseResponse;

import java.util.Map;

/**
 * Created by 雅麟 on 2015/6/17.
 */
public class IDsManagerBaseRequest<T extends BaseResponse> extends Request<T> {
    private static final String TAG = "IDsManagerBaseRequest";

    protected Response.Listener<T> mListener;
    protected Gson mGson;
    protected Class<T> mCls;
    protected Map<String, String> mHeaders;

    protected IDsManagerBaseRequest(int method, String url, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
    }

    public IDsManagerBaseRequest(int method, String url, Class<T> cls, Response.Listener listener, Response.ErrorListener errorListener) {
        this(method, url, cls, null, listener, errorListener);
    }

    public IDsManagerBaseRequest(int method, String url, Class<T> cls, Map<String, String> header, Response.Listener listener, Response.ErrorListener errorListener) {
        this(method, url, errorListener);
        mListener = listener;
        mGson = new Gson();
        mCls = cls;
        mHeaders = header;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        T parsedGSON;
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            parsedGSON = mGson.fromJson(jsonString, mCls);
        } catch (Exception e) {
            return Response.error(new VolleyError(e));
        }

        return Response.success(parsedGSON, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    public Map<String, String> getHeaders()
            throws AuthFailureError {
        if (mHeaders != null && mHeaders.size() > 0) {
            return mHeaders;
        }
        return super.getHeaders();
    }

    public void setHeaders(Map<String, String> headers) {
        mHeaders = headers;
    }

}
