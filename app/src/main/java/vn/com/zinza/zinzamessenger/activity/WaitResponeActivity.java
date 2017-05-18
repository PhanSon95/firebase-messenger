package vn.com.zinza.zinzamessenger.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.adapter.AdapterFriendRequest;
import vn.com.zinza.zinzamessenger.model.WaitRespone;
import vn.com.zinza.zinzamessenger.utils.Utils;

public class WaitResponeActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ListView mListView;
    private List<WaitRespone> mList;
    private List<String> requestKey;
    private AdapterFriendRequest mAdapterFriendRequest;

    private DatabaseReference mRequestRef;
    private View ftView,nodataView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wait_respone);
        initView();
        setInstance();
        loadData();
        setListview();
    }
    private void loadData(){
        mRequestRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    WaitRespone waitRespone = dataSnapshot.getValue(WaitRespone.class);
                    String []part = waitRespone.getmId().split("-");
                    String key1 = part[0];
                    String key2 = part[1];
                    if(key1.equals(Utils.USER_ID)){
                        requestKey.add(dataSnapshot.getKey());
                        mList.add(waitRespone);
                        mAdapterFriendRequest.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int index = requestKey.indexOf(dataSnapshot.getKey());
                    if(index>-1){
                        mAdapterFriendRequest.removeRequest(index);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setListview(){
        if(mList.size()==0){
            mListView.removeHeaderView(ftView);
            mListView.addHeaderView(nodataView);
        }
        mListView.removeHeaderView(nodataView);
        mAdapterFriendRequest = new AdapterFriendRequest(this,R.layout.item_friend_request,mList);
        mListView.setAdapter(mAdapterFriendRequest);
    }
    private void setInstance(){
        mRequestRef = FirebaseDatabase.getInstance().getReference().child(Utils.TBL_WAIT);
    }
    private void initView(){
        mList = new ArrayList<>();
        requestKey = new ArrayList<>();
        mToolbar = (Toolbar)findViewById(R.id.toolbarFr);
        mListView = (ListView)findViewById(R.id.lstWaitRespone);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = inflater.inflate(R.layout.footer_view, null);
        nodataView = inflater.inflate(R.layout.no_data_layout,null);
        mListView.addHeaderView(ftView);
        TextView message = (TextView)nodataView.findViewById(R.id.txtNodata);
        message.setText("Không có yêu cầu kết bạn nào cả :)");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Friend Request");
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
