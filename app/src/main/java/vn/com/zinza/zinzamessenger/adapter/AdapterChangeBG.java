package vn.com.zinza.zinzamessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import vn.com.zinza.zinzamessenger.R;

/**
 * Created by ASUS on 4/3/2017.
 */

public class AdapterChangeBG extends BaseAdapter {
    private Context mContext;
    private int mLayout;
    private int []list;

    public AdapterChangeBG(Context mContext, int mLayout, int[] list) {
        this.mContext = mContext;
        this.mLayout = mLayout;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.length;
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
        ImageView imageView = (ImageView)convertView.findViewById(R.id.imgChangeBG);
        imageView.setBackgroundResource(list[position]);
        return convertView;
    }
}
