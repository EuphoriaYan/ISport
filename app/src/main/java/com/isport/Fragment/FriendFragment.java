package com.isport.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.isport.Activity.FriendShareActivity;
import com.isport.Adapter.DiscoverListAdapter;
import com.isport.Adapter.FriendListAdapter;
import com.isport.Bean.GetDiscoverData;
import com.isport.Bean.GetFriendData;
import com.isport.Bean.GlobalValues;
import com.isport.R;

import java.util.List;

/**
 * Created by Euphoria on 2017/8/11.
 */

public class FriendFragment extends Fragment {

    private FriendListAdapter mAdapter;
    private PullToRefreshListView mPullToRefreshListView;
    private List<String> str;

    private static final int UPDATE_VIEW = 0x1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_friend,null);
        initPTRListView(view);
        initViews(view);
        return view;
    }

    /**
     * 设置下拉刷新的listview的动作
     */
    private void initPTRListView(View mView) {
        mPullToRefreshListView = (PullToRefreshListView) mView.findViewById(R.id.ptr_lv_friend);
        //设置拉动监听器
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                //设置下拉时显示的日期和时间
                String label = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(),//getApplication
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // 更新显示的label
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                // 开始执行异步任务，传入适配器来进行数据改变
                new GetFriendData(mPullToRefreshListView, mAdapter, str).execute();
            }
        });

        // 添加滑动到底部的监听器
        mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(getContext(), "已经到底了", Toast.LENGTH_SHORT).show();
            }
        });

        //在刷新时允许继续滑动
        mPullToRefreshListView.setScrollingWhileRefreshingEnabled(false);
        //mPullRefreshListView.getMode();//得到模式
        //上下都可以刷新的模式。这里有两个选择：Mode.PULL_FROM_START，Mode.BOTH，PULL_FROM_END
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

    }

    public void initViews(View mView)
    {

        mAdapter = new FriendListAdapter(this.getActivity().getApplicationContext(), GlobalValues.FriendList);
        ListView mListView = mPullToRefreshListView.getRefreshableView();

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>arg0, View arg1, int arg2,long arg3) {
                Intent mIntent = new Intent(getActivity(), FriendShareActivity.class);
                int num = arg2 - 1;
                String asknum = GlobalValues.FriendList.get(num).getUserid();
                mIntent.putExtra("number", asknum);
                startActivity(mIntent);
            }
        });

        new Handler().postDelayed(new Runnable(){
            public void run(){
                mPullToRefreshListView.setRefreshing();
            }
        }, 200);
    }

    @Override
    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }

}
