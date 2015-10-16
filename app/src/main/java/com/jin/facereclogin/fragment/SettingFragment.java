package com.jin.facereclogin.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jin.facereclogin.R;
import com.jin.facereclogin.domain.Face;
import com.jin.facereclogin.domain.User;
import com.jin.facereclogin.util.LockUtils;
import com.jin.facereclogin.util.Utils;


/**
 * Created by 雅麟 on 2015/3/22.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {
    public interface SettingItemClickCallback {
        void onPersonalInfoClicked();

        void onGestureSettingClicked();

        void onFaceSettingClicked();
    }

    RelativeLayout rlPersonalInfo;
    LinearLayout llHasLogin;
    LinearLayout llNotLogin;
    ImageView ivProfile;
    TextView tvPhone;
    RelativeLayout rlLockType;
    TextView tvGesturePassword;
    TextView tvFacePassword;
    TextView tvType;

    private SettingItemClickCallback settingItemClickCallback;


    public static SettingFragment getInstance(SettingItemClickCallback settingItemClickCallback) {
        SettingFragment fragment = new SettingFragment();
        fragment.settingItemClickCallback = settingItemClickCallback;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initView(view);
        initData();

        return view;
    }

    private void initView(View view) {
        rlPersonalInfo = (RelativeLayout) view.findViewById(R.id.setting_rl_personal_info);
        llHasLogin = (LinearLayout) view.findViewById(R.id.setting_ll_has_login);
        llNotLogin = (LinearLayout) view.findViewById(R.id.setting_ll_not_login);
        ivProfile = (ImageView) view.findViewById(R.id.setting_iv_profile);
        tvPhone = (TextView) view.findViewById(R.id.setting_tv_phone);
        tvGesturePassword = (TextView) view.findViewById(R.id.setting_tv_gesture_password);
        tvFacePassword = (TextView) view.findViewById(R.id.setting_tv_face_password);
        rlLockType = (RelativeLayout) view.findViewById(R.id.setting_rl_lock_type);
        tvType = (TextView) view.findViewById(R.id.setting_tv_lock_type);

        tvGesturePassword.setOnClickListener(this);
        rlPersonalInfo.setOnClickListener(this);
        tvFacePassword.setOnClickListener(this);
        rlLockType.setOnClickListener(this);
    }

    private void initData() {
        if (User.isLogin(getActivity().getApplicationContext())) {
            tvPhone.setText(User.getUserAccount(getActivity().getApplicationContext()));
            llHasLogin.setVisibility(View.VISIBLE);
            llNotLogin.setVisibility(View.GONE);
        } else {
            llNotLogin.setVisibility(View.VISIBLE);
            llHasLogin.setVisibility(View.GONE);
        }
        initType();
        Glide.with(getActivity())
                .load(R.drawable.user)
                .override(150, 150)
                .centerCrop()
                .into(ivProfile);
    }

    private void initType() {
        String[] types = getResources().getStringArray(R.array.lock_type);
        switch (LockUtils.getLockType(getActivity().getApplicationContext())) {
            case LockUtils.FACE_TYPE:
                tvType.setText(types[0]);
                break;
            case LockUtils.GESTURE_TYPE:
                tvType.setText(types[1]);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (settingItemClickCallback == null) {
            return;
        }
        int[] startingLocation = Utils.getViewCenterXY(v);
        switch (v.getId()) {
            case R.id.setting_rl_personal_info:
                settingItemClickCallback.onPersonalInfoClicked();
                break;
            case R.id.setting_tv_gesture_password:
                settingItemClickCallback.onGestureSettingClicked();
                break;
            case R.id.setting_tv_face_password:
                settingItemClickCallback.onFaceSettingClicked();
                break;
            case R.id.setting_rl_lock_type:
                showSelection();
                break;
        }
    }

    @Override
    protected void onLoginStateChanged() {
        initData();
    }

    void showSelection() {
        new AlertDialog.Builder(getActivity())
                .setItems(getResources().getStringArray(R.array.lock_type),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        LockUtils.setLockType(getActivity().getApplication(), LockUtils.FACE_TYPE);
                                        break;
                                    case 1:
                                        LockUtils.setLockType(getActivity().getApplication(), LockUtils.GESTURE_TYPE);
                                        break;
                                }
                                initType();
                            }
                        }).show();
    }
}
