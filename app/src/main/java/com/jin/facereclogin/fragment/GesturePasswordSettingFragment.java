package com.jin.facereclogin.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.jin.facereclogin.R;
import com.jin.facereclogin.activity.MainActivity;
import com.jin.facereclogin.application.MyApplication;
import com.jin.gesturepassword.CreateGesturePasswordActivity;
import com.jin.gesturepassword.widget.LockPatternUtils;


/**
 * Created by 雅麟 on 2015/6/23.
 */
public class GesturePasswordSettingFragment extends BaseSlideFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "GesturePasswordSettingFragment";

    Switch openGesture;
    Button btnChange;

    public static GesturePasswordSettingFragment getInstance() {
        GesturePasswordSettingFragment fragment = new GesturePasswordSettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_gesture_password_setting, container, false);
        initView(view);
        openGesture.setChecked(LockPatternUtils.getInstance(getActivity()).isEnable(MyApplication.getContext()));
        openGesture.setOnCheckedChangeListener(this);
        btnChange.setOnClickListener(this);
        return view;
    }

    private void initView(View view) {
        openGesture = (Switch) view.findViewById(R.id.gesture_password_setting_switch);
        btnChange = (Button) view.findViewById(R.id.gesture_password_setting_btn_change);
    }

    @Override
    protected String getRequestTag() {
        return GesturePasswordSettingFragment.class.getName();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LockPatternUtils.getInstance(getActivity()).enableLock(MyApplication.getContext(), isChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gesture_password_setting_btn_change:
                CreateGesturePasswordActivity.open(getActivity());
                break;
        }
    }
}
