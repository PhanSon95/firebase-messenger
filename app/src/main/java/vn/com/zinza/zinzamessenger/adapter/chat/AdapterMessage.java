package vn.com.zinza.zinzamessenger.adapter.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.activity.ChattingActivity;
import vn.com.zinza.zinzamessenger.model.Message;
import vn.com.zinza.zinzamessenger.model.User;
import vn.com.zinza.zinzamessenger.utils.Helper;
import vn.com.zinza.zinzamessenger.utils.Utils;

import static java.security.AccessController.getContext;

/**
 * Created by ASUS on 02/13/2017.
 */

public class AdapterMessage extends BaseAdapter {
    private Context mContext;
    private int mLayout;
    private List<Message> mListMessage;

    private List<Message> searchList;
    private List<String> mListUserKeys;
    private List<String> mListMessageKeys;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserReference;
    private DatabaseReference mChatReference;

     String keyConversation = "";
    private String mSender_id = "";
    private String mRecipient_id = "";
    private int lastPosition = -1;

    public String IS_NEW_MESSAGE = Utils.UNREAD;

    public AdapterMessage(Context mContext, int mLayout, List<Message> mListMessage) {
        this.mContext = mContext;
        this.mLayout = mLayout;
        this.mListMessage = mListMessage;

        mListUserKeys = new ArrayList<>();
        mListMessageKeys = new ArrayList<>();
        searchList = new ArrayList<>();
        searchList.addAll(mListMessage);
    }

    private String getIdFr(Message message) {
        String result = "";
        if (Utils.USER_ID.equals(message.getmIdSender())) {
            result = message.getmIdRecipient();
        } else if (Utils.USER_ID.equals(message.getmIdRecipient())) {
            result = message.getmIdSender();
        }
        return result;
    }

    public void refill(Message message) {
        String idFr = getIdFr(message);
        if (!mListMessageKeys.contains(idFr)) {
            mListMessageKeys.add(idFr);
            mListMessage.add(message);
        } else {
            for (int i = 0; i < mListMessage.size(); i++) {
                if (mListMessage.get(i).getmIdRecipient().equals(idFr)) {
                    mListMessage.remove(i);
                    mListMessage.add(message);

                }
            }
        }
        notifyDataSetChanged();
    }
    public void isNewMessage(String result){
        this.IS_NEW_MESSAGE = result;
        notifyDataSetChanged();
    }
    public void chaneMessage(int index, Message message) {
        mListMessage.add(index, message);
        notifyDataSetChanged();
    }

    public void removeConversation(int index) {
        mListMessage.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListMessage.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(mLayout, null);

        SwipeLayout swipeLayout = (SwipeLayout)convertView.findViewById(R.id.swipeItemMessageMain);
        setUpSwipe(swipeLayout);
        ImageView avatarUser = (ImageView) convertView.findViewById(R.id.imgAvatar);
        TextView nameUser = (TextView) convertView.findViewById(R.id.txtName);
        EmojiconTextView contentMessage = (EmojiconTextView) convertView.findViewById(R.id.txtContent);
        TextView timeMessage = (TextView) convertView.findViewById(R.id.txtTime);
        ImageView imgNewMessage =  (ImageView)convertView.findViewById(R.id.imgNewMessage);
//        if(stateMessage.equals(Utils.UNREAD)){
//            imgNewMessage.setVisibility(View.VISIBLE);
//        } else {
//            imgNewMessage.setVisibility(View.INVISIBLE);
//        }

        String idFriend = "";
        mSender_id = mListMessage.get(position).getmIdSender();
        mRecipient_id = mListMessage.get(position).getmIdRecipient();
        if (Utils.USER_ID.equals(mSender_id)) {
            idFriend = mRecipient_id;
        } else {
            idFriend = mSender_id;
        }
        mListMessageKeys.add(idFriend);
        getUser(idFriend, avatarUser, nameUser,swipeLayout,position);
        contentMessage.setText(mListMessage.get(position).getmContent());
        timeMessage.setText(Helper.convertTime(mListMessage.get(position).getmTime()));
        lastPosition = position;
        return convertView;
    }
    private void setUpSwipe(final SwipeLayout swipe){
        swipe.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipe.addDrag(SwipeLayout.DragEdge.Right,swipe.findViewWithTag("Bottom2"));

        swipe.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipe.close();
            }
        });


    }
    private void getUser(String idFriend, final ImageView avatar, final TextView txtname, final SwipeLayout swipeLayout, final int position) {
        mUserReference = mDatabase.getInstance().getReference().child(Utils.TBL_USERS);
        mUserReference.orderByChild("mId").equalTo(idFriend).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    mListUserKeys.add(dataSnapshot.getKey());
                    final User user = dataSnapshot.getValue(User.class);
                    Glide.with(mContext).load(user.getmAvatar()).crossFade().into(avatar);
                    txtname.setText(user.getmUsername());
                    swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent chatting = new Intent(mContext, ChattingActivity.class);
                            chatting.putExtra(Utils.FR_USER, user);
                            chatting.putExtra(Utils.SENDER_ID, Utils.USER_ID);
                            chatting.putExtra(Utils.RECIPIENT_ID, user.getmId());
                            chatting.putExtra(Utils.TYPE_CHAT,Utils.PRIVATE);
                            mContext.startActivity(chatting);
                        }
                    });
                    swipeLayout.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String kcv1 = Utils.USER_ID + "-" + user.getmId();
                            final String kcv2 = user.getmId() + "-" + Utils.USER_ID;
                        }
                    });
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

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mListMessage.clear();
        if (charText.length() == 0) {
            mListMessage.addAll(searchList);
        } else {
            for (Message s : searchList) {
                // search by name...
                String nameSearch = "Phan";
                if (nameSearch.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mListMessage.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }
    private void setChatReference(){
        mChatReference = FirebaseDatabase.getInstance().getReference().child("tblChat");
    }
}
