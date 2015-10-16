package com.jin.facereclogin.fragment;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import com.jin.facereclogin.domain.User;


/**
 * Created by YaLin on 2015/7/24.
 */
public class BaseFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onStart() {
        super.onStart();
        User.getUserSp(getActivity().getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        User.getUserSp(getActivity().getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (User.USER_ACCOUNT_KEY.equals(key)) {
            onLoginStateChanged();
        }
    }

    protected void onLoginStateChanged() {
    }

}
