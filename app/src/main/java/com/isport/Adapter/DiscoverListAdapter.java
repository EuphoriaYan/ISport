package com.isport.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.isport.Activity.BaseActivity;
import com.isport.Bean.Friend;
import com.isport.Bean.User;
import com.isport.R;
import com.isport.Utils.GeneralUtil;

import java.util.List;

/**
 * Created by Euphoria on 2017/8/12.
 */

public class DiscoverListAdapter extends BaseAdapter{
    private Context context;

    private LayoutInflater layoutInflater;

    private List<Friend> data;

    public DiscoverListAdapter(Context context, List<Friend> data) {

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

        DiscoverListAdapter.ViewHolder viewHolder = null;

        if (convertView == null) {

            viewHolder = new DiscoverListAdapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.holder_discover_list,parent,false);
            viewHolder.tvNickname = (TextView) convertView.findViewById(R.id.tv_discover_listholder_nickname);
            viewHolder.tvSex = (TextView) convertView.findViewById(R.id.tv_discover_listholder_sex);
            viewHolder.tvReason = (TextView) convertView.findViewById(R.id.tv_discover_listholder_reason);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DiscoverListAdapter.ViewHolder) convertView.getTag();
        }

        Friend fri = data.get(position);
        viewHolder.tvNickname.setText(fri.getNickName());
        viewHolder.tvSex.setText(fri.getSex());
        //viewHolder.tvReason.setText(fri.getReason());
        return convertView;
    }

    private class ViewHolder {

        private TextView tvNickname;
        private TextView tvSex;
        private TextView tvReason;

    }
}
