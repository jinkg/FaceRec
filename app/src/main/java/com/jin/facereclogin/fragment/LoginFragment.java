package com.jin.facereclogin.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jin.facereclogin.R;
import com.jin.facereclogin.application.MyApplication;
import com.jin.facereclogin.domain.User;
import com.jin.facereclogin.util.ToastUtil;
import com.jin.facereclogin.util.Utils;


/**
 * Created by 雅麟 on 2015/3/22.
 */
public class LoginFragment extends BaseLoadingFragment implements View.OnClickListener {

    public interface LoginSuccessCallback {
        void onLoginSuccess();
    }

    private static final String TAG = "LoginFragment";

    EditText mEtAccount;
    EditText mEtPassword;
    Button mBtnLogin;

    TextInputLayout tilAccount;
    TextInputLayout tilPassword;

    private LoginSuccessCallback callback;

    public static LoginFragment getInstance(LoginSuccessCallback callback) {
        LoginFragment fragment = new LoginFragment();
        fragment.callback = callback;
        return fragment;
    }

    public static void open(int container, FragmentManager manager, LoginSuccessCallback callback) {
        if (manager.findFragmentByTag(TAG) != null) {
            return;
        }
        manager.beginTransaction().setCustomAnimations(
                R.anim.push_left_in,
                R.anim.push_left_out,
                R.anim.push_right_in,
                R.anim.push_right_out)
                .add(container, getInstance(callback), TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mEtAccount = (EditText) view.findViewById(R.id.login_et_account);
        mEtPassword = (EditText) view.findViewById(R.id.login_et_password);
        mBtnLogin = (Button) view.findViewById(R.id.login_btn_login);
        mBtnLogin.setOnClickListener(this);
        tilAccount = (TextInputLayout) view.findViewById(R.id.login_til_account);
        tilPassword = (TextInputLayout) view.findViewById(R.id.login_til_password);
    }

    @Override
    public void onStart() {
        super.onStart();
        mEtPassword.setText(null);
        mEtAccount.post(new Runnable() {
            @Override
            public void run() {
                mEtAccount.requestFocus();
            }
        });
    }

    @Override
    protected String getRequestTag() {
        return LoginFragment.class.getName();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_login:
                Utils.closeInput(getActivity());
                login();
                break;
        }
    }

    private void login() {
        final String account = mEtAccount.getText().toString().trim();
        final String password = mEtPassword.getText().toString().trim();
        User user = new User();
        user.userAccount = account;
        user.userPassword = password;
        // todo  add new user to database;

        loginSuccess(user);
    }

    private void loginSuccess(User user) {
        User.storeUserInfo(MyApplication.getContext(), user);
        if (callback != null) {
            callback.onLoginSuccess();
            callback = null;
        }
        if (getActivity() == null) {
            return;
        }
        dismissLoading();
        ToastUtil.showToast(getActivity(), R.string.login_success);
    }
}
