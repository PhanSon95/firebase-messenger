package vn.com.zinza.zinzamessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.model.BodyRequestFCM;
import vn.com.zinza.zinzamessenger.model.Data;
import vn.com.zinza.zinzamessenger.model.Friend;
import vn.com.zinza.zinzamessenger.model.Message;
import vn.com.zinza.zinzamessenger.model.Notification;
import vn.com.zinza.zinzamessenger.model.ResultRequestFCM;
import vn.com.zinza.zinzamessenger.model.WaitRespone;
import vn.com.zinza.zinzamessenger.service.FCMService;
import vn.com.zinza.zinzamessenger.utils.Utils;

/**
 * Created by ASUS on 4/5/2017.
 */

public class AdapterFriendRequest extends BaseAdapter {
    private Context mContext;
    private int mLayout;
    private List<WaitRespone> list;
    private DatabaseReference mReference;

    public AdapterFriendRequest(Context mContext, int mLayout, List<WaitRespone> list) {
        this.mContext = mContext;
        this.mLayout = mLayout;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }
    public void addRequest(WaitRespone waitRespone){
        list.add(waitRespone);
        notifyDataSetChanged();
    }
    public void removeRequest(int index){
        list.remove(index);
        notifyDataSetChanged();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(mLayout,null);

        ImageView imageView = (ImageView)convertView.findViewById(R.id.imgFriendRequest);
        TextView nameFriend = (TextView)convertView.findViewById(R.id.txtnameFriendRequest);
        Button accept = (Button)convertView.findViewById(R.id.btnAcceptRequest);
        Button reject = (Button)convertView.findViewById(R.id.btnRejectRequest);

        Glide.with(mContext).load(list.get(position).getmUrl()).into(imageView);
        nameFriend.setText(list.get(position).getmName());

        String []parts = list.get(position).getmId().split("-");
        final String idFriend = parts[1];
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child(Utils.TBL_WAIT).child(list.get(position).getmId()).removeValue();
                Notification mNotification = new Notification("Zinza Messenger", Utils.USER_NAME);
                Data mData = new Data(Utils.USER_ID, Utils.getToken(), Utils.AVATAR_URL, Utils.TYPE_ANSWER);
                addNewFriend(Utils.USER_ID, idFriend);
                instanceRetrofit(mNotification, mData, list.get(position).getmToken(), mContext);
                Utils.showToast(list.get(position).getmName() + " và bạn đã là bạn bè",mContext);
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child(Utils.TBL_WAIT).child(list.get(position).getmId()).removeValue();
                Utils.showToast("Đéo thích làm bạn với "+list.get(position).getmName(),mContext);
            }
        });
        return convertView;
    }
    private void instanceRetrofit(Notification mNotification, Data mData, String tokenFr, final Context mContext) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.FCM_SEND_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FCMService service = retrofit.create(FCMService.class);
        BodyRequestFCM mBodyRequestFCM = new BodyRequestFCM(tokenFr, mNotification, mData);
        Call<ResultRequestFCM> call = service.sendPush(mBodyRequestFCM);

        call.enqueue(new Callback<ResultRequestFCM>() {
            @Override
            public void onResponse(Response<ResultRequestFCM> response, Retrofit retrofit) {

            }

            @Override
            public void onFailure(Throwable t) {
                Utils.showToast("Failure:" + t.toString(), mContext);
            }
        });

    }
    private void addNewFriend(String idCurrentUser, String idFriend) {
        String idTblFriend = "";
        final String tblContact = idCurrentUser + "-" + idFriend;
        idTblFriend = tblContact;
        final Friend friend = new Friend(idTblFriend, idCurrentUser, idFriend, Utils.createAt());
        createconversation(idCurrentUser,idFriend);

        mReference = FirebaseDatabase.getInstance().getReference(Utils.TBL_FRIENDS);
        mReference.child(tblContact).setValue(friend);
    }

    private void createconversation(String idCurrentUser, String idFriend){
        String mId = idCurrentUser + "-" + idFriend;
        DatabaseReference messRef = FirebaseDatabase.getInstance().getReference("tblChat");
        String idMessage = messRef.push().getKey();
        Message mMessage = new Message(idMessage,idCurrentUser,idFriend, Utils.TEXT, Utils.INTRO_ACCEPT, Utils.createAt());
        messRef.child(mId).child(idMessage).setValue(mMessage);

    }
}
