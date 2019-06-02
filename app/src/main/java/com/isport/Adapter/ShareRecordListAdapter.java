package com.isport.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.isport.Bean.GlobalValues;
import com.isport.Bean.ShareRecord;
import com.isport.R;

import java.util.List;

/**
 * Created by Euphoria on 2017/8/10.
 */

public class ShareRecordListAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater layoutInflater;

    private List<ShareRecord> data;

    public ShareRecordListAdapter(Context context, List<ShareRecord> data) {
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

        ShareRecordListAdapter.ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ShareRecordListAdapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.holder_sharerecord_list, parent, false);
            viewHolder.ivPic = (SimpleDraweeView) convertView.findViewById(R.id.iv_share_listholder_pic);
            viewHolder.tvText = (TextView) convertView.findViewById(R.id.tv_share_listholder_text);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_share_listholder_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ShareRecordListAdapter.ViewHolder) convertView.getTag();
        }

        ShareRecord shareRecord = data.get(position);
        viewHolder.tvTime.setText(shareRecord.getTime());
        viewHolder.tvText.setText(shareRecord.getText());
        viewHolder.ivPic.setImageURI(GlobalValues.baseUrl + "images/" + shareRecord.getImgUrl());
        return convertView;
    }

    private class ViewHolder {
        private TextView tvTime;
        private TextView tvText;
        private SimpleDraweeView ivPic;
    }

}
