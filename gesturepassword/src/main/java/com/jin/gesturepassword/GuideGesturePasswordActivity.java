package com.jin.gesturepassword;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jin.gesturepassword.widget.LockPatternUtils;

/**
 * Created by 雅麟 on 2015/6/23.
 */
public class GuideGesturePasswordActivity extends Activity {

    public static void open(Activity activity) {
        activity.startActivity(new Intent(activity, GuideGesturePasswordActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesturepassword_guide);
        findViewById(R.id.gesturepwd_guide_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockPatternUtils.getInstance(GuideGesturePasswordActivity.this).clearLock();
                Intent intent = new Intent(GuideGesturePasswordActivity.this,
                        CreateGesturePasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
