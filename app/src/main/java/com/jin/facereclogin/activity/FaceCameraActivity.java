
// Copyright (c) Philipp Wagner. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.jin.facereclogin.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jin.facereclogin.R;
import com.jin.facereclogin.net.IDsManagerGetRequest;
import com.jin.facereclogin.net.response.DetectResponse;
import com.jin.facereclogin.net.IDsManagerPostRequest;
import com.jin.facereclogin.net.NetService;
import com.jin.facereclogin.net.RequestQueueHelper;
import com.jin.facereclogin.net.response.BaseResponse;
import com.jin.facereclogin.net.response.GroupListResponse;
import com.jin.facereclogin.net.response.PersonResponse;
import com.jin.facereclogin.net.response.VerifyResponse;
import com.jin.facereclogin.receiver.SystemEventReceiver;
import com.jin.facereclogin.util.ImageUtil;
import com.jin.facereclogin.util.ToastUtil;
import com.jin.facereclogin.util.Utils;
import com.jin.facereclogin.widget.Preview;
import com.jin.facereclogin.widget.RadarScanView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class FaceCameraActivity extends BaseLoadNoRevealActivity implements Camera.PreviewCallback {
    private static final String TAG = FaceCameraActivity.class.getName();
    private static final int ENROLL_FACE_COUNT = 3;
    public static final int ENROLL = 1;
    public static final int VERIFY = 2;
    private static final int RADOM_DELAY = 1500;
    private static final String NAME_KEY = "name";
    private static final String TYPE_KEY = "type";

    public static final String sReason = "Home";
    private static boolean sVerified;

    List<String> faceIds = new ArrayList<>();


    private Preview mPreview;
    private Camera mCamera;
    private FaceTask mFaceTask;
    RadarScanView radarScanView;

    private OrientationEventListener mOrientationListener;
    private int mOrientation;
    private int mOrientationCompensation;

    private String mName;
    private int mType;

    public static void enroll(Activity activity, String name) {
        Intent intent = new Intent(activity, FaceCameraActivity.class);
        intent.putExtra(NAME_KEY, name);
        intent.putExtra(TYPE_KEY, ENROLL);
        activity.startActivityForResult(intent, ENROLL);
    }

    public static void verify(Activity activity, String name) {
        Intent intent = new Intent(activity, FaceCameraActivity.class);
        intent.putExtra(NAME_KEY, name);
        intent.putExtra(TYPE_KEY, VERIFY);
        activity.startActivityForResult(intent, VERIFY);
    }

    public static void lock() {
        sVerified = false;
    }

    public static void unLock() {
        sVerified = true;
    }

    public static boolean isUnLock() {
        return sVerified;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        mName = getIntent().getStringExtra(NAME_KEY);
        mType = getIntent().getIntExtra(TYPE_KEY, ENROLL);

        initView();

        mOrientationListener = new SimpleOrientationEventListener(this);
        mOrientationListener.enable();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        mPreview = new Preview(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        radarScanView = (RadarScanView) findViewById(R.id.view_mask);
    }

    private boolean safeCameraOpen(int id) {
        synchronized (this) {
            boolean qOpened = false;
            try {
                releaseCameraAndPreview();
                mCamera = Camera.open(id);
                qOpened = (mCamera != null);
            } catch (Exception e) {
                Log.e(getString(R.string.app_name), "failed to open Camera");
                e.printStackTrace();
            }

            return qOpened;
        }
    }

    private void releaseCameraAndPreview() {
        synchronized (this) {
            mPreview.setCamera(null, null);
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCameraAndPreview();
        mOrientationListener.disable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (safeCameraOpen(1)) {
            mPreview.setCamera(mCamera, this);
            mOrientationListener.enable();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (null != mFaceTask) {
            switch (mFaceTask.getStatus()) {
                case RUNNING:
                    return;
                case PENDING:
                    mFaceTask.cancel(false);
                    break;
            }
        }
        mFaceTask = new FaceTask(data);
        mFaceTask.execute((Void) null);
    }

    private void createPerson() {
        showLoading();
        RequestQueue requestQueue = RequestQueueHelper.getInstance(getApplicationContext());
        final IDsManagerGetRequest<PersonResponse> request = new IDsManagerGetRequest<>(NetService.createPerson(mName, faceIds), PersonResponse.class,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d(TAG, "create person success");
                        setResult(RESULT_OK);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "create person failed");
                        dismissLoading();
                        setResult(RESULT_CANCELED);
                    }
                }
        );
        requestQueue.add(request);
    }


    private void verify(String faceId) {
        showLoading();
        RequestQueue requestQueue = RequestQueueHelper.getInstance(getApplicationContext());
        final IDsManagerGetRequest<VerifyResponse> request = new IDsManagerGetRequest<>(NetService.verify(mName, faceId), VerifyResponse.class,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        dismissLoading();
                        VerifyResponse verifyResponse = (VerifyResponse) response;
                        if (verifyResponse.result) {
                            ToastUtil.showToast(FaceCameraActivity.this, R.string.verify_success);
                            unLock();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            showMsg(getString(R.string.verify_failed));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissLoading();
                        showMsg(getString(R.string.verify_failed));
                    }
                }
        );
        requestQueue.add(request);
    }

    void detect(Bitmap bitmap) {
        String base64 = null;
        try {
            base64 = ImageUtil.createImgToBase64(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(base64)) {
            return;
        }
        RequestQueue requestQueue = RequestQueueHelper.getInstance(getApplicationContext());
        final IDsManagerPostRequest<DetectResponse> request = new IDsManagerPostRequest<>(NetService.DETECT_URL, DetectResponse.class, NetService.detect(base64),
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        detectResult((DetectResponse) response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        initFrame();
                    }
                }
        );
        requestQueue.add(request);
    }


    public void detectResult(DetectResponse rst) {
        if (rst.face != null && rst.face.size() > 0) {
            Log.d(TAG, "detected face.....");
            switch (mType) {
                case ENROLL:
                    faceIds.add(rst.face.get(0).face_id);
                    if (faceIds.size() >= ENROLL_FACE_COUNT) {
                        createPerson();
                    } else {
                        showProgressDialog(faceIds.size());
                    }
                    break;
                case VERIFY:
                    verify(rst.face.get(0).face_id);
                    break;
            }
        } else {
            Log.d(TAG, "no face....");
            initFrame();
        }
    }

    class FaceTask extends AsyncTask<Void, Void, Void> {
        private byte[] mData;

        FaceTask(byte[] data) {
            this.mData = data;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            final int w = size.width;
            final int h = size.height;
            final YuvImage image = new YuvImage(mData, ImageFormat.NV21, w, h, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream(mData.length);

            if (!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
                return null;
            }
            byte[] tmp = os.toByteArray();
            Bitmap b = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
            Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, -90.0f);

            detect(rotaBitmap);
            return null;
        }
    }


    class SimpleOrientationEventListener extends OrientationEventListener {

        public SimpleOrientationEventListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == ORIENTATION_UNKNOWN) return;
            mOrientation = Utils.roundOrientation(orientation, mOrientation);
            int orientationCompensation = mOrientation
                    + Utils.getDisplayRotation(FaceCameraActivity.this);
            if (mOrientationCompensation != orientationCompensation) {
                mOrientationCompensation = orientationCompensation;
            }
        }
    }

    protected void showProgressDialog(int progress) {
        if (progress != 1 && progress != 2) {
            return;
        }
        String msg = progress == 1 ? getString(R.string.face_progress_1) : getString(R.string.face_progress_2);
        Dialog dialog = new android.app.AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initFrame();
                    }
                }).create();
        dialog.show();
    }

    protected void showMsg(String msg) {
        Dialog dialog = new android.app.AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initFrame();
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        Intent homeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        homeIntent.putExtra(SystemEventReceiver.SYSTEM_DIALOG_REASON_KEY, sReason);
        sendBroadcast(homeIntent);
    }

    void initFrame() {
        synchronized (this) {
            if (mCamera != null) {
                Random random = new Random();
                long delay = random.nextInt(1500);
                Log.d(TAG, "delay frame is : " + delay);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mCamera.setOneShotPreviewCallback(FaceCameraActivity.this);
                    }
                }, delay);
            }
        }
    }
}