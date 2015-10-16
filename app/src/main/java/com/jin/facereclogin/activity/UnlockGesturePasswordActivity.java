package com.jin.facereclogin.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.jin.facereclogin.R;
import com.jin.facereclogin.application.MyApplication;
import com.jin.facereclogin.domain.User;
import com.jin.facereclogin.receiver.SystemEventReceiver;
import com.jin.facereclogin.util.ImageUtil;
import com.jin.facereclogin.util.ToastUtil;
import com.jin.gesturepassword.widget.LockPatternUtils;
import com.jin.gesturepassword.widget.LockPatternView;

import java.util.List;


/**
 * Created by 雅麟 on 2015/6/23.
 */
public class UnlockGesturePasswordActivity extends Activity implements View.OnClickListener {
    public static boolean sGestureDetectSuccess = false;
    private int mFailedPatternAttemptsSinceLastTimeout = 0;
    private CountDownTimer mCountdownTimer = null;
    private Handler mHandler = new Handler();
    private Animation mShakeAnim;
    public static final String sReason = "Home";

    TextView mHeadTextView;
    LockPatternView mLockPatternView;
    TextView tvForget;
    ImageView ivFace;

    public static void open(Activity activity) {
        activity.startActivity(new Intent(activity, UnlockGesturePasswordActivity.class));
    }

    public static void lock() {
        sGestureDetectSuccess = false;
    }

    public static void unLock() {
        sGestureDetectSuccess = true;
    }

    public static boolean isUnLock() {
        return sGestureDetectSuccess;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_gesture_password);
        initView();
        Bitmap bitmap = ImageUtil.decodeSampledBitmapFromResource(getResources(), R.drawable.enterprise_logo_100, 150, 150);
        ivFace.setImageBitmap(bitmap);
        mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
        mLockPatternView.setTactileFeedbackEnabled(true);
        tvForget.setOnClickListener(this);
        mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initView() {
        mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
        mLockPatternView = (LockPatternView) findViewById(R.id.gesturepwd_unlock_lockview);
        tvForget = (TextView) findViewById(R.id.gesturepwd_unlock_forget);
        ivFace = (ImageView) findViewById(R.id.gesturepwd_unlock_face);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountdownTimer != null)
            mCountdownTimer.cancel();
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

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

        public void onPatternStart() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
            patternInProgress();
        }

        public void onPatternCleared() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
        }

        public void onPatternDetected(List<LockPatternView.Cell> pattern) {
            if (pattern == null)
                return;
            if (LockPatternUtils.getInstance(UnlockGesturePasswordActivity.this).checkPattern(pattern)) {
                mLockPatternView
                        .setDisplayMode(LockPatternView.DisplayMode.Correct);
                unLock();
                finish();
            } else {
                mLockPatternView
                        .setDisplayMode(LockPatternView.DisplayMode.Wrong);
                mFailedPatternAttemptsSinceLastTimeout++;
                int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
                        - mFailedPatternAttemptsSinceLastTimeout;
                if (retry >= 0) {
                    if (retry == 0)
                        ToastUtil.showToast(UnlockGesturePasswordActivity.this, R.string.string_try_max);
                    String msg = getString(R.string.string_left_times);
                    mHeadTextView.setText(String.format(msg, retry));
                    mHeadTextView.setTextColor(Color.RED);
                    mHeadTextView.startAnimation(mShakeAnim);
                }

                if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
                    mLockPatternView.setEnabled(false);
                    mHandler.postDelayed(attemptLockout, 2000);
                } else {
                    mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
                }
            }
        }

        public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

        }

        private void patternInProgress() {
        }
    };
    Runnable attemptLockout = new Runnable() {

        @Override
        public void run() {
            mLockPatternView.clearPattern();
            mLockPatternView.setEnabled(false);
            mCountdownTimer = new CountDownTimer(
                    LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
                    if (secondsRemaining > 0) {
                        String msg = getString(R.string.string_retry_timeout);
                        mHeadTextView.setText(String.format(msg, secondsRemaining));
                    } else {
                        mHeadTextView.setText(getString(R.string.string_draw_gesture_password));
                        mHeadTextView.setTextColor(Color.WHITE);
                    }

                }

                @Override
                public void onFinish() {
                    mLockPatternView.setEnabled(true);
                    mFailedPatternAttemptsSinceLastTimeout = 0;
                }
            }.start();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gesturepwd_unlock_forget:
                showConfirmDialog();
                break;
        }
    }

    protected void showConfirmDialog() {
        Dialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.forget_password))
                .setMessage(getString(R.string.string_confirm_forget_gesture_password))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User.deleteUserInfo(MyApplication.getContext());
                        LockPatternUtils.getInstance(UnlockGesturePasswordActivity.this).clearLock();
                        sGestureDetectSuccess = true;
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }
}
