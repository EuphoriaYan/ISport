package com.isport.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.isport.Bean.Friend;
import com.isport.R;

import java.util.List;

/**
 * Created by Euphoria on 2017/8/12.
 */

public class FriendListAdapter extends BaseAdapter {
    private Context context;

    private LayoutInflater layoutInflater;

    private List<Friend> data;

    public FriendListAdapter(Context context, List<Friend> data) {

        this.context = context;

        this.data = data;

        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FriendListAdapter.ViewHolder viewHolder = null;

        if (convertView == null) {

            viewHolder = new FriendListAdapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.holder_friend_list,parent,false);
            viewHolder.tvNickname = (TextView) convertView.findViewById(R.id.tv_friend_listholder_nickname);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FriendListAdapter.ViewHolder) convertView.getTag();
        }

        Friend fri = data.get(position);
        viewHolder.tvNickname.setText(fri.getNickName());
        return convertView;
    }

    private class ViewHolder {

        private TextView tvNickname;

    }
}