package vn.com.zinza.zinzamessenger.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import vn.com.zinza.zinzamessenger.R;

public class SettingActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Toolbar mTtoolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);
        initControl();
    }
    private void initControl(){
        mDrawerLayout = (DrawerLayout)findViewById(R.id.settingDrawerlayout);
        mTtoolbar = (Toolbar)findViewById(R.id.toolbarFr);
        setSupportActionBar(mTtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");
        mTtoolbar.setNavigationIcon(R.drawable.ic_action_back);
        mTtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
