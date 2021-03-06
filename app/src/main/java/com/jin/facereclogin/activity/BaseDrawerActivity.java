package com.jin.facereclogin.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jin.facereclogin.R;
import com.jin.facereclogin.constant.AnimationConstants;
import com.jin.facereclogin.domain.User;
import com.jin.facereclogin.util.Utils;

/**
 * Created by YaLin on 2015/7/24.
 */
public abstract class BaseDrawerActivity extends BaseActivity implements OnClickListener, OnNavigationItemSelectedListener {
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    protected Toolbar toolbar;
    protected DrawerLayout drawerLayout;
    protected LinearLayout llHeaderRoot;
    protected NavigationView navigationView;

    private DrawerHeaderViewHolder drawerHeaderViewHolder;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final IntentFilter homeFilter = new IntentFilter();
        homeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        homeFilter.addAction(Intent.ACTION_SCREEN_OFF);
        homeFilter.addAction(Intent.ACTION_SCREEN_ON);
        homeFilter.addAction(Intent.ACTION_USER_PRESENT);

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupLayer();
        setupView();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MenuItem checkedItem = navigationView.getMenu().findItem(getDrawerCheckId());
        checkedItem.setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setupLayer() {
        View view = View.inflate(this, R.layout.activity_base_drawer, null);
        findView(view);

        FrameLayout content = (FrameLayout) view.findViewById(R.id.base_content);
        ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
        View contentView = rootView.getChildAt(0);
        rootView.removeView(contentView);
        content.addView(contentView);
        rootView.addView(view);
    }

    private void findView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.base_toolbar);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.base_dl);
        llHeaderRoot = (LinearLayout) view.findViewById(R.id.me_ll_root);
        drawerHeaderViewHolder = new DrawerHeaderViewHolder(llHeaderRoot);
        navigationView = (NavigationView) view.findViewById(R.id.navigation_view);
    }

    private void setupView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        initDrawerView();
    }


    @Override
    protected void onLoginStateChanged() {
        super.onLoginStateChanged();
        initDrawerView();
    }

    protected abstract int getDrawerCheckId();


    private void initDrawerView() {
        if (User.isLogin(getApplicationContext())) {
            drawerHeaderViewHolder.tvPhone.setText(User.getUserAccount(getApplicationContext()));
            drawerHeaderViewHolder.llHasLogin.setVisibility(View.VISIBLE);
            drawerHeaderViewHolder.llNotLogin.setVisibility(View.GONE);
        } else {
            drawerHeaderViewHolder.llNotLogin.setVisibility(View.VISIBLE);
            drawerHeaderViewHolder.llHasLogin.setVisibility(View.GONE);
        }
        Glide.with(this)
                .load(R.drawable.user)
                .override(150, 150)
                .into(drawerHeaderViewHolder.ivProfile);
        drawerHeaderViewHolder.btnLogin.setOnClickListener(this);
        drawerHeaderViewHolder.btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int[] startingLocation = Utils.getViewCenterXY(v);
        switch (v.getId()) {
            case R.id.me_btn_login:
                drawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AccountActivity.requestLogin(BaseDrawerActivity.this, startingLocation);
                    }
                }, NAVDRAWER_LAUNCH_DELAY);

                break;
            case R.id.me_btn_logout:
                User.deleteUserInfo(getApplicationContext());
                animateLogout();
                break;
        }
    }

    private void animateLogout() {
        int height = drawerHeaderViewHolder.llRoot.getHeight();
        drawerHeaderViewHolder.ivProfile.setTranslationY(-height);
        drawerHeaderViewHolder.llLogin.setTranslationY(-height);
        drawerHeaderViewHolder.ivProfile.animate().translationY(0)
                .setDuration(AnimationConstants.ANIM_DURATION)
                .setStartDelay(100)
                .setInterpolator(INTERPOLATOR);

        drawerHeaderViewHolder.llLogin.animate().translationY(0)
                .setDuration(AnimationConstants.ANIM_DURATION)
                .setStartDelay(200)
                .setInterpolator(INTERPOLATOR).start();
    }

    private void goToNavDrawerItem(int itemId) {
        switch (itemId) {
            case R.id.navigation_home_page:
                MainActivity.open(this);
                break;
            case R.id.navigation_setting:
                SettingActivity.open(this);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToNavDrawerItem(menuItem.getItemId());
            }
        }, NAVDRAWER_LAUNCH_DELAY);
        drawerLayout.closeDrawer(Gravity.LEFT);
        return false;
    }


    static class DrawerHeaderViewHolder {
        View itemView;
        LinearLayout llRoot;
        LinearLayout llLogin;
        LinearLayout llHasLogin;
        LinearLayout llNotLogin;
        ImageView ivProfile;
        Button btnLogin;
        Button btnLogout;
        TextView tvPhone;

        public DrawerHeaderViewHolder(View view) {
            itemView = view;
            initView(view);
        }

        void initView(View view) {
            llRoot = (LinearLayout) view.findViewById(R.id.me_ll_root);
            llLogin = (LinearLayout) view.findViewById(R.id.setting_ll_login);
            llHasLogin = (LinearLayout) view.findViewById(R.id.setting_ll_has_login);
            llNotLogin = (LinearLayout) view.findViewById(R.id.setting_ll_not_login);
            ivProfile = (ImageView) view.findViewById(R.id.setting_iv_profile);
            btnLogin = (Button) view.findViewById(R.id.me_btn_login);
            btnLogout = (Button) view.findViewById(R.id.me_btn_logout);
            tvPhone = (TextView) view.findViewById(R.id.me_tv_phone);
        }
    }
}
