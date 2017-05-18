package vn.com.zinza.zinzamessenger.adapter;

import android.content.Context;
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
import vn.com.zinza.zinzamessenger.model.User;

/**
 * Created by ASUS on 4/5/2017.
 */

public class AdapterCreateGroup extends BaseAdapter {
    private Context mContext;
    private int mLayout;
    private List<User> mListUser;

    public AdapterCreateGroup(Context mContext, int mLayout, List<User> mListUser) {
        this.mContext = mContext;
        this.mLayout = mLayout;
        this.mListUser = mListUser;
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
        ImageView avatarUser = (ImageView)convertView.findViewById(R.id.imgUserGr);
        TextView nameUser = (TextView)convertView.findViewById(R.id.txtUserGr);
        String urlAvatar = mListUser.get(position).getmAvatar();
        if(!urlAvatar.equals("")){
            Glide.with(mContext).load(urlAvatar).crossFade().into(avatarUser);
        }
        nameUser.setText(mListUser.get(position).getmUsername());
        return convertView;
    }
}
