package com.jin.facereclogin.widget;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.jin.facereclogin.util.PixelUtil;
import com.jin.facereclogin.util.Utils;

import java.util.List;

public class Preview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters mParams;
    private Camera.PreviewCallback previewCallback;

    public Preview(Context context) {
        super(context);
    }

    public void setCamera(Camera camera, Camera.PreviewCallback previewCallback) {
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        this.previewCallback = previewCallback;
        // deprecated setting, but required on Android versions prior to 3.0
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        start();
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        start();
    }

    private void start() {
        try {
            mCamera.setPreviewDisplay(mHolder);
            mParams = mCamera.getParameters();
            Point p = getBestCameraResolution(mParams);
            mParams.setPreviewSize(p.x, p.y);
            Camera.Size minSize = Utils.getPropPictureSize(mParams.getSupportedPictureSizes());
            System.out.println("mainSize : " + minSize.width + " " + minSize.height);
            mParams.setPictureSize(minSize.width, minSize.height);
            mCamera.setParameters(mParams);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            mCamera.setOneShotPreviewCallback(previewCallback);
        } catch (Exception e) {
        }
    }

    private Point getBestCameraResolution(Camera.Parameters parameters) {
        float tmp = 0f;
        float mindiff = 100f;
        float x_d_y = (float) PixelUtil.getScreenWidth(getContext()) / (float) PixelUtil.getScreenHeight(getContext());
        Camera.Size best = null;
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size s : supportedPreviewSizes) {
            tmp = Math.abs(((float) s.height / (float) s.width) - x_d_y);
            if (tmp < mindiff) {
                mindiff = tmp;
                best = s;
            }
        }
        return new Point(best.width, best.height);
    }

}