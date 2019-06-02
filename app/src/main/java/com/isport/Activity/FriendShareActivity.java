package com.isport.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.isport.Adapter.ShareRecordListAdapter;
import com.isport.Bean.GetFriendShareData;
import com.isport.Bean.GlobalValues;
import com.isport.R;
import com.isport.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Euphoria on 2017/8/13.
 */

public class FriendShareActivity extends Activity {
    private ShareRecordListAdapter mAdapter;
    private PullToRefreshListView mPullToRefreshListView;
    private List<String> str;
    private String number;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_friend_share);
        Bundle mBundle = getIntent().getExtras();
        number = mBundle.getString("number");
        initPTRListView();
        initViews();
    }

    /**
     * 设置下拉刷新的listview的动作
     */
    private void initPTRListView() {
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.ptr_lv_friend_share);
        //设置拉动监听器
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                //设置下拉时显示的日期和时间
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),//getApplication
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // 更新显示的label
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                // 开始执行异步任务，传入适配器来进行数据改变
                new GetFriendShareData(mPullToRefreshListView, mAdapter,str, number).execute();
            }
        });

        // 添加滑动到底部的监听器
        mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                ToastUtil.setShortToast(getApplicationContext(), "已经到底了");
            }
        });

        //mPullRefreshListView.isScrollingWhileRefreshingEnabled();//看刷新时是否允许滑动
        //在刷新时允许继续滑动
        mPullToRefreshListView.setScrollingWhileRefreshingEnabled(true);
        //mPullRefreshListView.getMode();//得到模式
        //上下都可以刷新的模式。这里有两个选择：Mode.PULL_FROM_START，Mode.BOTH，PULL_FROM_END
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

    }

    public void initViews()
    {
        mAdapter = new ShareRecordListAdapter(this.getApplicationContext(), GlobalValues.FriendShareRecord);
        ListView mListView = mPullToRefreshListView.getRefreshableView();

        mListView.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable(){
            public void run(){
                mPullToRefreshListView.setRefreshing();
            }
        }, 200);
    }
}
