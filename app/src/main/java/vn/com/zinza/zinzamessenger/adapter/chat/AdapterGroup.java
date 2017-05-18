package vn.com.zinza.zinzamessenger.adapter.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.activity.ChattingActivity;
import vn.com.zinza.zinzamessenger.model.Group;
import vn.com.zinza.zinzamessenger.model.User;
import vn.com.zinza.zinzamessenger.utils.Helper;
import vn.com.zinza.zinzamessenger.utils.Utils;

/**
 * Created by ASUS on 4/13/2017.
 */

public class AdapterGroup extends BaseAdapter {
    private DatabaseReference mReference;
    private Context mContext;
    private int mLayout;
    private List<Group> mListGroup;

    public AdapterGroup(Context mContext, int mLayout, List<Group> mListGroup) {
        this.mContext = mContext;
        this.mLayout = mLayout;
        this.mListGroup = mListGroup;
        mReference = FirebaseDatabase.getInstance().getReference();
    }
    public void addGroup(Group group){
        mListGroup.add(group);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mListGroup.size();
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
        convertView = inflater.inflate(R.layout.activity_item_message,null);
        ImageView avartarGroup = (ImageView) convertView.findViewById(R.id.imgAvatar);
        TextView nameGroup = (TextView) convertView.findViewById(R.id.txtName);
        EmojiconTextView contentMessage = (EmojiconTextView) convertView.findViewById(R.id.txtContent);
        TextView timeMessage = (TextView) convertView.findViewById(R.id.txtTime);
        if(avartarGroup.equals("")){
            avartarGroup.setImageResource(R.drawable.avatar_group);
        } else {
            Glide.with(mContext).load(mListGroup.get(position).getmAvatar()).crossFade().into(avartarGroup);
        }
        nameGroup.setText(mListGroup.get(position).getmName());
        timeMessage.setText(Helper.convertTime(mListGroup.get(position).getmCreated()));
        contentMessage.setText(mListGroup.get(position).getmDescribe());
        goToChat(convertView,position);
        return convertView;
    }
    private void goToChat(View v, final int position){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent(mContext,ChattingActivity.class);
                t.putExtra(Utils.TYPE_CHAT,Utils.GROUP);
                t.putExtra(Utils.GROUP_OBJECT,mListGroup.get(position));
                mContext.startActivity(t);
            }

        });
    }

}
