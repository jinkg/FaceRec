package com.jin.facereclogin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jin.facereclogin.R;
import com.jin.facereclogin.application.MyApplication;
import com.jin.facereclogin.domain.Face;
import com.jin.facereclogin.domain.User;
import com.jin.facereclogin.util.ToastUtil;
import com.jin.facereclogin.util.Utils;

public class MainActivity extends BaseLoadActivity {
    TextView tvInfo;
    ViewStub vsNotLogin;
    Button emptyBtnLogin;

    public static void open(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UnlockGesturePasswordActivity.lock();
        FaceCameraActivity.lock();
    }

    private void initView() {
        tvInfo = (TextView) findViewById(R.id.main_tv_info);
        vsNotLogin = (ViewStub) findViewById(R.id.main_vs_not_login);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initData() {
        if (User.isLogin(MyApplication.getContext())) {
            vsNotLogin.setVisibility(View.GONE);
            tvInfo.setVisibility(View.VISIBLE);
            tvInfo.setText(String.format(getString(R.string.welcome), User.getUserAccount(MyApplication.getContext())));
        } else {
            if (emptyBtnLogin == null) {
                emptyBtnLogin = (Button) vsNotLogin.inflate().findViewById(R.id.empty_btn_login);
                emptyBtnLogin.setOnClickListener(this);
            }
            tvInfo.setVisibility(View.GONE);
            vsNotLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getDrawerCheckId() {
        return R.id.navigation_home_page;
    }

    @Override
    protected void onLoginStateChanged() {
        initData();
        super.onLoginStateChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empty_btn_login:
                AccountActivity.requestLogin(this, Utils.getViewCenterXY(v));
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
