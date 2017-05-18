package vn.com.zinza.zinzamessenger.fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.adapter.chat.AdapterMessage;
import vn.com.zinza.zinzamessenger.model.Message;
import vn.com.zinza.zinzamessenger.model.User;
import vn.com.zinza.zinzamessenger.utils.Utils;

/**
 * Created by ASUS on 4/12/2017.
 */

public class PrivateChatFragment extends android.support.v4.app.Fragment {
    private ListView mListMessage;
    private List<Message> mList;
    private AdapterMessage mAdapterMessage;
    private List<String> mListMessageKeys;
    private List<String> mListConversationKeys;

    private List<String> mListUserKey;
    private List<User> mListUser;
    private static final String TAG_NAME = "NAME";
    private static final String TAG_AVATAR = "AVATAR";
    public static final String TAG_MESSAGE = "MESSAGE";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference mRefMessage;

    private ProgressDialog mProgressDialog;
    private String mProvider;
    private String idFriend;
    public static final int REQUEST_STORAGE = 0x3;
    private RelativeLayout mHeaderLayout;
    private View ftView;
    public PrivateChatFragment(){

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

        mListMessage = (ListView) view.findViewById(R.id.lstListmessage);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        ftView = inflater.inflate(R.layout.footer_view, null);
        mListMessage.addFooterView(ftView);
        loadConversation();
        loadListview();

    }
    private void loadConversation() {
        mListUserKey = new ArrayList<>();
        mListMessageKeys = new ArrayList<>();
        mListConversationKeys = new ArrayList<>();
        mList = new ArrayList<>();
        mRefMessage = FirebaseDatabase.getInstance().getReference().child(Utils.TBL_CHATS);

        mRefMessage.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    String key = dataSnapshot.getKey();
                    mListConversationKeys.add(key);
                    getMessageFromConversation(key);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRefMessage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                mProgressDialog.dismiss();
                mListMessage.removeFooterView(ftView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getMessageFromConversation(final String keyConversation) {
        DatabaseReference mConverRef = mDatabase.getInstance().getReference().child(Utils.TBL_CHATS).child(keyConversation);
        final DatabaseReference mStateRef = mDatabase.getInstance().getReference();
        mConverRef.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    final Message message = dataSnapshot.getValue(Message.class);
                    if (message.getmType().equals(Utils.IMAGE)) {
                        message.setmContent("\uD83C\uDFDD Image");
                    } else if (message.getmType().equals(Utils.FILE)) {
                        message.setmContent("\uD83D\uDCDA Attachment");
                    } else if (message.getmType().equals(Utils.VIDEO)) {
                        message.setmContent("\uD83C\uDF9E video.mp4");
                    }
                    if (isMyConvesation(keyConversation)) {
                        mAdapterMessage.refill(message);
                        mListMessageKeys.add(dataSnapshot.getKey());
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
    private boolean isMyConvesation(String key) {
        String[] parts = key.split("-");
        if(parts.length == 2){

        }
        String uId1 = parts[0];
        String uId2 = parts[1];
        if (Utils.USER_ID.equals(uId1)) {
            return true;
        } else if (Utils.USER_ID.equals(uId2)) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        loadConversation();
    }

    private void loadListview() {
        mAdapterMessage = new AdapterMessage(getContext(), R.layout.activity_item_message, mList);
        mListMessage.setAdapter(mAdapterMessage);
    }
    private void showProgress(String title, String message) {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
}
