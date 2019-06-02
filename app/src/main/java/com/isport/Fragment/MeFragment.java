package com.isport.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.isport.Activity.RunRecordActivity;
import com.isport.Adapter.ShareRecordListAdapter;
import com.isport.Bean.GetDiscoverData;
import com.isport.Bean.GetFriendShareData;
import com.isport.Bean.GetMyShareData;
import com.isport.Bean.GlobalValues;
import com.isport.Bean.LocalUser;
import com.isport.Bean.RunRecord;
import com.isport.Bean.ShareRecord;
import com.isport.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.xinlan.discview.DiscView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Euphoria on 2017/9/2.
 */

public class MeFragment extends Fragment implements View.OnClickListener{

    private DiscView mDiscView;
    private TextView tvName;
    private TextView tvTips;
    private RelativeLayout rlRunRecord;
    private LocalUser user;
    private double totalDistance;//总里程
    private int totalTarget;//总目标

    private PullToRefreshScrollView mPTRScrollView;
    private ShareRecordListAdapter mAdapter;
    private List<ShareRecord> data = new ArrayList<>(); //本地数据
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_me, null);
        data = GlobalValues.MyShareRecord;
        initComponent(view);
        initPTRListView(view);
        return view;
    }

    private void initComponent(View v) {
        user = LocalUser.getInstance();
        mDiscView = (DiscView) v.findViewById(R.id.disc_view);
        tvName = (TextView) v.findViewById(R.id.tv_name);
        tvTips = (TextView) v.findViewById(R.id.tv_me_tips);
        rlRunRecord = (RelativeLayout) v.findViewById(R.id.rl_runrecord);
        rlRunRecord.setOnClickListener(this);

        List<RunRecord> data = GlobalValues.AllRunRecord;
        for (RunRecord runRecord:data) {
            totalDistance += runRecord.getDistance();
        }
        totalDistance/=1000;

        tvName.setText(user.getNickName());
        tvTips.setText("你离第一个100KM，还有"+ (100 - (int)totalDistance) +"KM，继续加油吧！");
        mDiscView.setValue((int)totalDistance,100);

    }

    private List<String> str;

    private void initPTRListView(View v){
        mPTRScrollView = (PullToRefreshScrollView) v.findViewById(R.id.ptr_sv_me);
        mListView = (ListView) v.findViewById(R.id.lv_my_share_record);
        mListView.setFocusable(false);
        //设置拉动监听器
        mPTRScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                //设置下拉时显示的日期和时间
                String label = DateUtils.formatDateTime(
                        getContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                // 更新显示的label
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                // 开始执行异步任务，传入适配器来进行数据改变
                new GetMyShareData(mPTRScrollView, mAdapter, str, mListView).execute();
            }
        });

        mPTRScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        mAdapter = new ShareRecordListAdapter(getActivity(), data);

        mListView.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable(){
            public void run(){
                mPTRScrollView.setRefreshing();
            }
        }, 200);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_runrecord://跑步记录
                Intent runRecordIntent = new Intent(getActivity(), RunRecordActivity.class);
                startActivity(runRecordIntent);
                break;
        }
    }

}
