package vn.com.zinza.zinzamessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.utils.Helper;
import vn.com.zinza.zinzamessenger.utils.Utils;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ImageView mImgProfile;
    private TextView mTxtnameprofile;
    private TextView txtEmailProfile;
    private TextView txtCreated;
    private ProgressDialog mProgressDialog;
    private View ftView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        initView();
        loadInformation();
    }
    private void initView(){
        mToolbar =(Toolbar)findViewById(R.id.toolbarFr) ;
        mImgProfile = (ImageView)findViewById(R.id.imgProfile);
        mTxtnameprofile = (TextView)findViewById(R.id.txtNameProfile);
        txtEmailProfile = (TextView)findViewById(R.id.txtEmailProfile);
        txtCreated = (TextView)findViewById(R.id.txtCreated);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.prfDrawer);
        if(!Utils.GOT_USER){
            mDrawerLayout.addView(ftView);
        } else {
            mDrawerLayout.removeView(ftView);
        }
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = inflater.inflate(R.layout.footer_view, null);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Profile");
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void loadInformation(){
        Glide.with(this).load(Utils.AVATAR_URL).crossFade().into(mImgProfile);
        mTxtnameprofile.setText(Utils.USER_NAME);
        txtEmailProfile.setText(Utils.USER_EMAIL);
        txtCreated.setText(Helper.convertDay(Utils.USER_CREATED));

    }

}
