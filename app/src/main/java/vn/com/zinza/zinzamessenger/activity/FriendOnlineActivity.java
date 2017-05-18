package vn.com.zinza.zinzamessenger.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.adapter.AdapterFriendOnline;
import vn.com.zinza.zinzamessenger.model.Friend;
import vn.com.zinza.zinzamessenger.model.User;
import vn.com.zinza.zinzamessenger.utils.Utils;

import static java.security.AccessController.getContext;

public class FriendOnlineActivity extends AppCompatActivity implements ListView.OnItemClickListener, SearchView.OnQueryTextListener {
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ListView mListFriend;
    private List<User> mListUser;
    private AdapterFriendOnline mAdapterFriendOnline;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private List<Friend> mListFriends;
    private List<String> mListFrStr;
    private View ftView,nodateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_online);
        initControl();
        setFirebaseInstace();

        mListFriends = new ArrayList<>();
        mListUser = new ArrayList<>();
        mListFrStr = new ArrayList<>();
        getListFr();


    }

    private void initControl() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.olDrawerlayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbarFr);
        mListFriend = (ListView) findViewById(R.id.lstListFriend);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = inflater.inflate(R.layout.footer_view, null);
        mListFriend.addHeaderView(ftView);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Friend online");
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadListview() {
        mAdapterFriendOnline = new AdapterFriendOnline(this, R.layout.item_friend_ol, mListUser);
        mListFriend.setAdapter(mAdapterFriendOnline);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_friend_ol, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search friend...");
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        final List<User> filteredModelList = filter(mListUser, query);
        mAdapterFriendOnline.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<User> filteredModelList = filter(mListUser, newText);
        mAdapterFriendOnline.setFilter(filteredModelList);
        return true;
    }

    private void setFirebaseInstace() {
        mReference = mDatabase.getInstance().getReference();
    }

    private void loadFr(Friend friend) {
        String myFriendID = "";
        String currentID = friend.getmIdCurrentUser();
        String friendID = friend.getmIdFriend();
        if (currentID.equals(Utils.USER_ID)) {
            myFriendID = friendID;
            getListUser(myFriendID);
            mListFrStr.add(myFriendID);
        } else if (friendID.equals(Utils.USER_ID)) {
            myFriendID = currentID;
            getListUser(myFriendID);
            mListFrStr.add(myFriendID);

        } else {

        }
    }

    // get list friends
    private List<User> filter(List<User> models, String query) {
        query = query.toLowerCase();
        final List<User> filteredModelList = new ArrayList<>();
        for (User model : models) {
            final String text = model.getmUsername().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
    private void getListFr() {
        mReference.child(Utils.TBL_FRIENDS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Friend friend = dataSnapshot.getValue(Friend.class);
                        mListFriends.add(friend);
                        loadFr(friend);


                } else {

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

    //get list fr online
    private void getListUser(final String id) {
        mReference.child(Utils.TBL_USERS).orderByChild("mId").equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListFriend.removeHeaderView(ftView);
                List<User> users = new ArrayList<User>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                        mListUser.add(user);
                }
                loadListview();
                Log.e("Size lstUsr:", mListUser.size() + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


}
