package vn.com.zinza.zinzamessenger.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.adapter.AdapterFriendSearch;
import vn.com.zinza.zinzamessenger.adapter.chat.AdapterMessage;
import vn.com.zinza.zinzamessenger.fragment.GroupChatFragment;
import vn.com.zinza.zinzamessenger.fragment.PrivateChatFragment;
import vn.com.zinza.zinzamessenger.model.Message;
import vn.com.zinza.zinzamessenger.model.User;
import vn.com.zinza.zinzamessenger.utils.Helper;
import vn.com.zinza.zinzamessenger.utils.Utils;

public class MessageFriendActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener
        , ActionBar.TabListener {
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private long mBackPressed = 0;

    private ListView mListMessage;
    private List<Message> mList;
    private AdapterMessage mAdapterMessage;
    private List<String> mListMessageKeys;
    private List<String> mListConversationKeys;


    private ListView mLstFriendSearch;
    private List<User> mListFriendSearch;
    private AdapterFriendSearch mAdapterFriendSearch;

    private List<String> mListUserKey;
    private List<User> mListUser;
    private NavigationView mNavigationView;


    private static final String TAG_NAME = "NAME";
    private static final String TAG_AVATAR = "AVATAR";
    public static final String TAG_MESSAGE = "MESSAGE";
    private FloatingActionButton mFabAddFriend, fab1, fab2, fab3;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private Boolean isFabOpen = false;

    private Dialog mDlAddFriend;
    private AlertDialog.Builder mBuilder;
    private Dialog mDlDetailFriend;
    private ProgressDialog mProgressDialog;
    private List<String> mListSearchFrKeys;

    private TextView mUsername;
    private TextView mEmail;
    private ImageView mImCurrenUser;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference mRefMessage;

    private GoogleApiClient mGoogleApiClient;
    private String mProvider;
    private User mUser;
    private String idFriend;

    public static final int REQUEST_STORAGE = 0x3;
    private boolean doubleBackToExitPressedOnce = false;
    private RelativeLayout mHeaderLayout;


    //Tablayout
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private int[] tabIcons = {
            R.drawable.chat_private,
            R.drawable.chat_group
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_messenger_friend);
        initControl();
        setAuthInstace();
        showProgress("Loading...", "Please wait...");
        mNavigationView.setNavigationItemSelectedListener(this);
        loadUser();


    }


    private void initControl() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mToolbar = (Toolbar) findViewById(R.id.mainToolbar);
//        mListMessage = (ListView) findViewById(R.id.lstListmessage);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setBackgroundResource(R.drawable.ic_background_header);
        View hView = mNavigationView.getHeaderView(0);
        mUsername = (TextView) hView.findViewById(R.id.txtUsername);
        mEmail = (TextView) hView.findViewById(R.id.txtEmail);
        mImCurrenUser = (ImageView) hView.findViewById(R.id.imCurrentUser);
        hView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageFriendActivity.this, ProfileActivity.class));
            }
        });

        setSupportActionBar(mToolbar);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.draw_open, R.string.draw_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                closeFAB();
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        mFabAddFriend = (FloatingActionButton) findViewById(R.id.fabAdd);
        fab1 = (FloatingActionButton) findViewById(R.id.fabAddFriend);
        fab2 = (FloatingActionButton) findViewById(R.id.fabFriendRequest);
        fab3 = (FloatingActionButton) findViewById(R.id.fabCreateGroup);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rorate_foward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        mFabAddFriend.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        //Tab layout
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setUpViewpager(mViewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        setTabIcons();

    }

    private void setUpViewpager(ViewPager viewpager) {
        AdapterViewpager adapter = new AdapterViewpager(getSupportFragmentManager());
        adapter.addFrag(new PrivateChatFragment(), "Private");
        adapter.addFrag(new GroupChatFragment(), "Group");
        viewpager.setAdapter(adapter);
    }

    private void setTabIcons() {
        mTabLayout.getTabAt(0).setIcon(tabIcons[0]);
        mTabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
        int tabIconColor = ContextCompat.getColor(this, R.color.green);
        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public class AdapterViewpager extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitle = new ArrayList<>();

        public AdapterViewpager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitle.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_messenger, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_friend_ol:
                Intent frOL = new Intent(MessageFriendActivity.this, FriendOnlineActivity.class);
                startActivity(frOL);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    //Xu ly key cua bang chat

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent setting = new Intent(MessageFriendActivity.this, SettingActivity.class);
                startActivity(setting);
                break;
            case R.id.action_history_file:
                Intent history_file = new Intent(MessageFriendActivity.this, HistoryFileActivity.class);
                startActivity(history_file);
                break;
            case R.id.action_rating:
                shareApp();
                break;
            case R.id.action_about:
                aboutUs();
                break;
            case R.id.action_logout:
                setUpAlert("Log out", "Are you sure to log out ? ");

                break;
        }
        return true;
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Zinza Messenger");
        String sAux = "\nLet me recommend you this application\n\n";
        sAux = sAux + "http://zinza.com.vn \n\n";
        sendIntent.putExtra(Intent.EXTRA_TEXT, sAux);
        startActivity(Intent.createChooser(sendIntent, "choose one"));
        startActivity(sendIntent);
    }

    private void aboutUs() {
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        startActivity(aboutIntent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fabAdd:
                animateFAB();

                break;
            case R.id.fabAddFriend:
                closeFAB();
                showAddDialog();
                break;
            case R.id.fabFriendRequest:
                closeFAB();
                Intent frRequest = new Intent(MessageFriendActivity.this, WaitResponeActivity.class);
                startActivity(frRequest);
                break;
            case R.id.fabCreateGroup:
                closeFAB();
                Intent createGroup = new Intent(MessageFriendActivity.this, GroupChatActivity.class);
                startActivity(createGroup);
                break;
        }
    }

    private void showAddDialog() {
        mDlAddFriend = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

        mDlAddFriend.setContentView(R.layout.dialog_search_friend);
        mDlAddFriend.show();
        mDlAddFriend.setCancelable(true);
        final EditText edtSearchFr = (EditText) mDlAddFriend.findViewById(R.id.edtSearchNamePhone);
        edtSearchFr.requestFocus();
        Button btnAddFr = (Button) mDlAddFriend.findViewById(R.id.btnSearchFriend);
        btnAddFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                String input = edtSearchFr.getText().toString().trim();
                if (input.equals("")) {
                    edtSearchFr.setError("Please input name ");
                } else {
                    searchByUsername(edtSearchFr.getText().toString().trim());
                    showDetailProfileDialog(mListFriendSearch);
                }

            }
        });


    }

    private void searchByUsername(final String username) {
        mListFriendSearch = new ArrayList<>();
        mListSearchFrKeys = new ArrayList<>();
        mReference = mDatabase.getInstance().getReference();
        mReference.keepSynced(true);
        mReference.child(Utils.TBL_USERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {

                    final String key = dataSnapshot.getKey();
                    final User user = dataSnapshot.getValue(User.class);
                    mListSearchFrKeys.add(key);
                    if (user.getmUsername().toLowerCase().contains(username.toLowerCase())) {
                        final String key1 = Utils.USER_ID + "-" + user.getmId();
                        final String key2 = user.getmId() + "-" + Utils.USER_ID;
                        mReference.child(Utils.TBL_FRIENDS).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!user.getmUsername().equals(Utils.USER_NAME)) {
                                    if (dataSnapshot.child(key1).exists()) {
                                        user.setmIsFriend(true);
                                    } else if (dataSnapshot.child(key2).exists()) {
                                        user.setmIsFriend(true);
                                    } else {
                                        user.setmIsFriend(false);
                                    }
                                    mAdapterFriendSearch.refill(user);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                }

            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }

    private void showDetailProfileDialog(List<User> list) {

        mDlDetailFriend = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        mDlDetailFriend.setContentView(R.layout.dialog_detail_friend);

        mDlDetailFriend.show();
        mDlAddFriend.dismiss();
        mDlDetailFriend.setCancelable(true);

        mLstFriendSearch = (ListView) mDlDetailFriend.findViewById(R.id.lstFriendSearch);
        mAdapterFriendSearch = new AdapterFriendSearch(this, R.layout.item_search_friend, list, mUser, mDlDetailFriend);
        mLstFriendSearch.setAdapter(mAdapterFriendSearch);

    }

    private void setUpAlert(String title, String message) {
        mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(title);
        mBuilder.setMessage(message);

        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
//                LoginManager.getInstance().logOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                Helper.setUserOffline(mReference);
                Intent intent = new Intent(MessageFriendActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void setAuthInstace() {
        mAuth = FirebaseAuth.getInstance();

        final String id = Utils.USER_ID = mAuth.getCurrentUser().getUid();
        final String photoUrl = Utils.AVATAR_URL = String.valueOf(mAuth.getCurrentUser().getPhotoUrl());
        mReference = mDatabase.getInstance().getReference();
        final String Uemail = mAuth.getCurrentUser().getEmail();
        mReference.child(Utils.TBL_USERS).orderByChild("mEmail").equalTo(Uemail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Uname = "";
                String Uavatar = "";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Uname = (String) ds.child("mUsername").getValue();
                    Uavatar = (String) ds.child("mAvatar").getValue();
                }
                mUser = new User(id, Uname, "", "", Uavatar, "", "on", Utils.getToken(), "");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void loadUser() {
        Helper.setUserOnline(mReference);
        String mProvider = getTypeLogIn();
        String email = "";
        String avatarURL = "";
        String displayName = "";
        if (mProvider.equals("facebook.com")) {
            email = mAuth.getCurrentUser().getEmail();
            avatarURL = String.valueOf(mAuth.getCurrentUser().getPhotoUrl());
            displayName = mAuth.getCurrentUser().getDisplayName();
            mEmail.setText(email);
            mUsername.setText(displayName);
            Utils.USER_NAME = displayName;
            Utils.USER_ID = mAuth.getCurrentUser().getUid();
            if (!avatarURL.equals("")) {
                Glide.with(getApplicationContext()).load(avatarURL).into(mImCurrenUser);
            } else {
                mImCurrenUser.setImageResource(R.drawable.ic_avatar_df);
            }
            Utils.GOT_USER = true;
            mProgressDialog.dismiss();


        } else {
            mReference = mDatabase.getInstance().getReference();
            final String Uemail = mAuth.getCurrentUser().getEmail();
            mReference.child(Utils.TBL_USERS).orderByChild("mEmail").equalTo(Uemail).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = new User();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        user = ds.getValue(User.class);
                    }
                    mUsername.setText(user.getmUsername());
                    if (!user.getmAvatar().equals("")) {
                        Glide.with(getApplicationContext()).load(user.getmAvatar()).crossFade().into(mImCurrenUser);
                        Utils.AVATAR_URL = user.getmAvatar();
                    } else {
                        mImCurrenUser.setImageResource(R.drawable.ic_avatar_df);
                    }
                    mEmail.setText(Uemail);
                    Utils.USER_EMAIL = Uemail;

                    Utils.USER_NAME = user.getmUsername();
                    Utils.USER_PASS = user.getmPassword();
                    Utils.USER_DOB = user.getmDateOfBirth();
                    Utils.USER_CREATED = user.getmCreatedAt();
                    Utils.GOT_USER = true;
                    mProgressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private String getTypeLogIn() {
        return mAuth.getCurrentUser().getProviders().get(0);
    }

    private void showProgress(String title, String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void closeFAB() {
        if (isFabOpen) {

            mFabAddFriend.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;

        }
    }

    public void animateFAB() {

        if (isFabOpen) {
            mFabAddFriend.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
        } else {

            mFabAddFriend.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;

        }
    }
}
