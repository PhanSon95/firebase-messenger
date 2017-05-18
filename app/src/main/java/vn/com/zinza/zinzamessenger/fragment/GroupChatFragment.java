package vn.com.zinza.zinzamessenger.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import vn.com.zinza.zinzamessenger.adapter.chat.AdapterGroup;
import vn.com.zinza.zinzamessenger.model.Group;
import vn.com.zinza.zinzamessenger.utils.Utils;

/**
 * Created by ASUS on 4/12/2017.
 */

public class GroupChatFragment extends Fragment {
    private ListView mListViewGroupChat;
    private DatabaseReference mDatabaseReference;
    private List<Group> mListGroup;
    private AdapterGroup mAdapterGroup;
    private View ftView;
    public GroupChatFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setFirebaseInstace();
        loadMyGroupChat();
        setListGroup();
    }
    private void setFirebaseInstace(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }
    private void initView(View view){
        mListGroup = new ArrayList<>();
        mListViewGroupChat = (ListView)view.findViewById(R.id.lstListmessage);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        ftView = inflater.inflate(R.layout.footer_view, null);
    }
    private void loadMyGroupChat(){
        mListViewGroupChat.addFooterView(ftView);
        mDatabaseReference.child(Utils.TBL_GROUP).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                        Group group = dataSnapshot.getValue(Group.class);
                        List<String> members = group.getMembers();
                        if(members.contains(Utils.USER_ID)){
                            mAdapterGroup.addGroup(group);
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
        mDatabaseReference.child(Utils.TBL_GROUP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListViewGroupChat.removeFooterView(ftView);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setListGroup(){
        mAdapterGroup = new AdapterGroup(getContext(),R.layout.activity_item_message,mListGroup);
        mListViewGroupChat.setAdapter(mAdapterGroup);
    }
}
