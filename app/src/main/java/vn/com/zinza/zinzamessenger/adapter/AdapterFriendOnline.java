package vn.com.zinza.zinzamessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.activity.ChattingActivity;
import vn.com.zinza.zinzamessenger.model.User;
import vn.com.zinza.zinzamessenger.utils.Utils;

/**
 * Created by ASUS on 02/10/2017.
 */

public class AdapterFriendOnline extends BaseAdapter {
    private Context mContext;
    private int mLayout;
    private List<User> mListUser;
    private ArrayList<User> lists;

    public AdapterFriendOnline(Context mContext, int mLayout, List<User> mListUser) {
        this.mContext = mContext;
        this.mLayout = mLayout;
        this.mListUser = mListUser;
        this.lists  = new ArrayList<User>();
        this.lists.addAll(mListUser);
    }
    public void filter(String query) {
        if(query.length() == 0){
            mListUser.addAll(lists);
        }
        else {
            for (User user : lists) {
                final String name = user.getmUsername();
                if (name.contains(query) ) {
                    mListUser.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }
    public void setFilter(List<User> userModels) {
        mListUser = new ArrayList<>();
        mListUser.addAll(userModels);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(mLayout,null);
        ImageView avatarUser = (ImageView)convertView.findViewById(R.id.imgFriendOl);
        TextView nameUser = (TextView)convertView.findViewById(R.id.txtFriendOl);
        ImageView statusUser = (ImageView)convertView.findViewById(R.id.imgStatus);
        String urlAvatar = mListUser.get(position).getmAvatar();
        if(!urlAvatar.equals("")){
            Glide.with(mContext).load(urlAvatar).crossFade().into(avatarUser);
        }
        nameUser.setText(mListUser.get(position).getmUsername());

        if (mListUser.get(position).getmStatus().equals("on")){
            statusUser.setImageResource(R.drawable.online);
        }
        else {
            statusUser.setImageResource(R.drawable.offline);
        }
        final User user = mListUser.get(position);
        convertView.setOnClickListener(new View.OnClickListener() {
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
        return convertView;
    }
    public void refill(User user){
        mListUser.add(user);
        notifyDataSetChanged();
    }
    public void changeStatusUser(int index, User user) {
        mListUser.set(index,user);
        notifyDataSetChanged();
    }
    public void removeUser(int index){
        mListUser.remove(index);
        notifyDataSetChanged();
    }
}
