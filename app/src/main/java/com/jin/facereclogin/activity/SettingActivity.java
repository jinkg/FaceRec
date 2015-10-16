package com.jin.facereclogin.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jin.facereclogin.R;
import com.jin.facereclogin.application.MyApplication;
import com.jin.facereclogin.domain.Face;
import com.jin.facereclogin.domain.User;
import com.jin.facereclogin.fragment.FacePasswordSettingFragment;
import com.jin.facereclogin.fragment.GesturePasswordSettingFragment;
import com.jin.facereclogin.fragment.SettingFragment;
import com.jin.facereclogin.net.IDsManagerGetRequest;
import com.jin.facereclogin.net.IDsManagerPostRequest;
import com.jin.facereclogin.net.NetService;
import com.jin.facereclogin.net.RequestQueueHelper;
import com.jin.facereclogin.net.response.PersonResponse;
import com.jin.facereclogin.util.ToastUtil;
import com.jin.gesturepassword.GuideGesturePasswordActivity;
import com.jin.gesturepassword.widget.LockPatternUtils;


/**
 * Created by YaLin on 2015/7/30.
 */
public class SettingActivity extends BaseLoadActivity implements SettingFragment.SettingItemClickCallback {

    enum ContentType {
        PersonalInfo, Gesture, Face
    }

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, SettingActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        switchContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected int getDrawerCheckId() {
        return R.id.navigation_setting;
    }

    private void switchContent() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rl_content, SettingFragment.getInstance(this))
                .commit();

    }

    public void addContent(ContentType contentType) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.push_left_in,
                R.anim.push_left_out,
                R.anim.push_right_in,
                R.anim.push_right_out);
        switch (contentType) {
            case PersonalInfo:
                break;
            case Gesture:
                transaction.add(R.id.rl_content, GesturePasswordSettingFragment.getInstance())
                        .addToBackStack(null)
                        .commit();
                break;
            case Face:
                transaction.add(R.id.rl_content, FacePasswordSettingFragment.getInstance())
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }


    @Override
    public void onPersonalInfoClicked() {
        addContent(ContentType.PersonalInfo);
    }

    @Override
    public void onGestureSettingClicked() {
        if (!User.isLogin(MyApplication.getContext())) {
            ToastUtil.showToast(this, R.string.login_before_option);
            return;
        }
        if (LockPatternUtils.getInstance(this).savedPatternJustExists()) {
            addContent(ContentType.Gesture);
        } else {
            GuideGesturePasswordActivity.open(this);
        }
    }

    @Override
    public void onFaceSettingClicked() {
        if (!User.isLogin(MyApplication.getContext())) {
            ToastUtil.showToast(this, R.string.login_before_option);
            return;
        }
        getPerson(User.getUserAccount(MyApplication.getContext()));
    }

    private void getPerson(final String personName) {
        showLoading();
        RequestQueue requestQueue = RequestQueueHelper.getInstance(getApplicationContext());
        final IDsManagerGetRequest<PersonResponse> request = new IDsManagerGetRequest<>(NetService.getPerson(personName), PersonResponse.class,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        PersonResponse personResponse = (PersonResponse) response;
                        if (personResponse.res_code == 0) {
                            addContent(ContentType.Face);
                        } else {
                            FaceCameraActivity.enroll(SettingActivity.this, personName);
                        }
                        dismissLoading();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        FaceCameraActivity.enroll(SettingActivity.this, personName);
                        dismissLoading();
                    }
                }
        );
        requestQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSupportFragmentManager().popBackStack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FaceCameraActivity.ENROLL) {
            if (resultCode == RESULT_OK) {
                showConfirmDialog("注册人脸成功!");
            } else {
                showConfirmDialog("注册人脸失败,请重试!");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void showConfirmDialog(String msg) {
        Dialog dialog = new android.app.AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        dialog.show();
    }
}
