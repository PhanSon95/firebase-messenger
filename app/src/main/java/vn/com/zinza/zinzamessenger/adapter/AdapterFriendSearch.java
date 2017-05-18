package vn.com.zinza.zinzamessenger.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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
import vn.com.zinza.zinzamessenger.model.Notification;
import vn.com.zinza.zinzamessenger.model.ResultRequestFCM;
import vn.com.zinza.zinzamessenger.model.User;
import vn.com.zinza.zinzamessenger.service.FCMService;
import vn.com.zinza.zinzamessenger.utils.Utils;

/**
 * Created by ASUS on 02/27/2017.
 */

public class AdapterFriendSearch extends BaseAdapter {
    private Context mContext;
    private int mLayout;
    private List<User> mListUser;
    private User mUser;
    public static boolean ishaveData = false;

    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private Dialog mDialog;

    public AdapterFriendSearch(Context mContext, int mLayout, List<User> mListUser, User mUser,Dialog mDialog) {
        this.mContext = mContext;
        this.mLayout = mLayout;
        this.mListUser = mListUser;
        mAuth = FirebaseAuth.getInstance();
        this.mUser = mUser;
        this.mDialog = mDialog;
    }
    public void refill(User user){
        mListUser.add(user);
        notifyDataSetChanged();
    }
    public void changeUser(int index, User user) {
        mListUser.set(index,user);
        notifyDataSetChanged();
    }
    public void removeUser(int index){
        mListUser.remove(index);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mListUser.size();
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

        ImageView avatarUser = (ImageView)convertView.findViewById(R.id.imgFriendSearch);
        TextView nameUser = (TextView)convertView.findViewById(R.id.txtNameFriendSearch);
        Button btnAddFr= (Button) convertView.findViewById(R.id.btnAddFriend);

        if(!mListUser.get(position).getmAvatar().equals("")){
            Glide.with(mContext).load(mListUser.get(position).getmAvatar()).into(avatarUser);
        }
        nameUser.setText(mListUser.get(position).getmUsername());
        if(mListUser.get(position).getmIsFriend()){
            btnAddFr.setVisibility(View.GONE);
        } else {
            btnAddFr.setVisibility(View.VISIBLE);
        }


        btnAddFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idFr= mListUser.get(position).getmId();
                String tokenFr = mListUser.get(position).getmToken();
                String currentAvatarUrl = mUser.getmAvatar();
                String title = "Zinza Messenger";
                String body = mUser.getmUsername();

                String currentToken = mUser.getmToken();
                Notification mNotification = new Notification(title,body);
                Data mData = new Data(Utils.USER_ID,currentToken,currentAvatarUrl, Utils.TYPE_ADD);
                instanceRetrofit(mNotification,mData,tokenFr);
                Utils.showToast("Đã gửi lời mời kết bạn thành công!!",mContext);
                mDialog.dismiss();
            }
        });
        return convertView;
    }

    private void instanceRetrofit(Notification mNotification, Data mData, String tokenFr){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.FCM_SEND_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FCMService service = retrofit.create(FCMService.class);
        BodyRequestFCM mBodyRequestFCM = new BodyRequestFCM(tokenFr,mNotification,mData);
        Call<ResultRequestFCM> call = service.sendPush(mBodyRequestFCM);

        call.enqueue(new Callback<ResultRequestFCM>() {
            @Override
            public void onResponse(Response<ResultRequestFCM> response, Retrofit retrofit) {
                if(response.isSuccess()){
                    Log.e("Success","Send add friend success");
                } else {
                    Log.e("Success","Respone is null");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.showToast("Failure:"+t.toString(),mContext);
            }
        });
    }

}
