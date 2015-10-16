package com.jin.facereclogin.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.jin.facereclogin.R;
import com.jin.facereclogin.activity.FaceCameraActivity;
import com.jin.facereclogin.application.MyApplication;
import com.jin.facereclogin.domain.Face;
import com.jin.facereclogin.domain.User;
import com.jin.facereclogin.util.ToastUtil;
import com.jin.facereclogin.util.Utils;
import com.jin.gesturepassword.CreateGesturePasswordActivity;
import com.jin.gesturepassword.widget.LockPatternUtils;


/**
 * Created by 雅麟 on 2015/6/23.
 */
public class FacePasswordSettingFragment extends BaseSlideFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = FacePasswordSettingFragment.class.getName();

    Switch openGesture;
    Button btnChange;
    Button btnTest;

    public static FacePasswordSettingFragment getInstance() {
        FacePasswordSettingFragment fragment = new FacePasswordSettingFragment();
        return fragment;
    }

    public static void open(int container, FragmentManager manager) {
        if (manager.findFragmentByTag(TAG) != null) {
            return;
        }
        manager.beginTransaction().setCustomAnimations(
                R.anim.push_left_in,
                R.anim.push_left_out,
                R.anim.push_right_in,
                R.anim.push_right_out)
                .add(container, getInstance(), TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face_password_setting, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        openGesture = (Switch) view.findViewById(R.id.face_password_setting_switch);
        btnChange = (Button) view.findViewById(R.id.face_password_setting_btn_change);
        btnTest = (Button) view.findViewById(R.id.face_password_setting_btn_test);
        openGesture.setChecked(Face.isEnable(MyApplication.getContext()));
        openGesture.setOnCheckedChangeListener(this);
        btnChange.setOnClickListener(this);
        btnTest.setOnClickListener(this);
    }

    @Override
    protected String getRequestTag() {
        return FacePasswordSettingFragment.class.getName();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Face.enableLock(MyApplication.getContext(), isChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.face_password_setting_btn_change:
                CreateGesturePasswordActivity.open(getActivity());
                break;
            case R.id.face_password_setting_btn_test:
                FaceCameraActivity.verify(getActivity(), User.getUserAccount(MyApplication.getContext()));
                break;
        }
    }

}
