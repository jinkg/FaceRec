package jin.baseanimationui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import jin.baseanimationui.R;


/**
 * Created by 雅麟 on 2015/4/22.
 */
public class ToolbarActivity extends AppCompatActivity {
    Toolbar mToolbar;
    FrameLayout mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupToolbar() {
        View view = View.inflate(this, R.layout.activity_toolbar, null);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mContent = (FrameLayout) view.findViewById(R.id.base_content);
        ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
        View contentView = rootView.getChildAt(0);
        rootView.removeView(contentView);
        mContent.addView(contentView);
        rootView.addView(view);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStackImmediate();
                } else {
                    finish();
                }
                break;
        }
        return false;
    }

    protected Toolbar getToolbar() {
        return mToolbar;
    }
}
