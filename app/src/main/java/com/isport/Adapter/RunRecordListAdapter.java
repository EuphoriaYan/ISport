package com.isport.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.isport.Bean.RunRecord;
import com.isport.R;
import com.isport.Utils.GeneralUtil;

import java.util.List;

/**
 * Created by Euphoria on 2017/8/4.
 */

public class RunRecordListAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater layoutInflater;

    private List<RunRecord> data;

    public RunRecordListAdapter(Context context, List<RunRecord> data) {

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

        ViewHolder viewHolder = null;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.holder_runreord_list,parent,false);
            viewHolder.tvCreateTime = (TextView) convertView.findViewById(R.id.tv_listholder_createtime);
            viewHolder.tvDistance = (TextView) convertView.findViewById(R.id.tv_listholder_distance);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_listholder_time);
            viewHolder.tvPace = (TextView) convertView.findViewById(R.id.tv_listholder_pace);
            viewHolder.tvAvgSpeed = (TextView) convertView.findViewById(R.id.tv_listholder_avgspeed);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RunRecord runRecord = data.get(position);
        viewHolder.tvCreateTime.setText(runRecord.getCreateTime());
        viewHolder.tvDistance.setText(GeneralUtil.doubleToString(runRecord.getDistance())+"KM");
        viewHolder.tvTime.setText(GeneralUtil.secondsToString(runRecord.getTime()));
        viewHolder.tvPace.setText("配速:" + runRecord.getPace());
        viewHolder.tvAvgSpeed.setText("均速:" + runRecord.getAvgSpeed());
        return convertView;
    }

    private class ViewHolder {

        private TextView tvCreateTime;
        private TextView tvDistance;
        private TextView tvTime;
        private TextView tvPace;
        private TextView tvAvgSpeed;

    }
}
