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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.*;
import com.isport.*;
import com.isport.Activity.SearchFriendActivity;
import com.isport.Adapter.DiscoverListAdapter;
import com.isport.Application.App;
import com.isport.Bean.Friend;
import com.isport.Bean.GetDiscoverData;
import com.isport.Bean.GlobalValues;
import com.isport.Database.DatabaseManager;
import com.isport.R;
import com.isport.Utils.ChronometerUtils;
import com.isport.Utils.GeneralUtil;
import com.isport.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Euphoria on 2017/8/11.
 */

public class DiscoverFragment extends Fragment {
    private DiscoverListAdapter mAdapter;
    private PullToRefreshListView mPullToRefreshListView;
    private List<String> str;

    private static final int UPDATE_VIEW = 0x1;

    private Button btSearch;
    private Button btRadar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VIEW:
                    Bundle bundle = msg.getData();
                    int num = (int) bundle.get("number");
                    View v = getViewByPosition(num+1, mPullToRefreshListView.getRefreshableView());
                    ImageView iv = (ImageView) v.findViewById(R.id.iv_discover_listholder_like);
                    if (!GlobalValues.DiscoverList.get(num).getState()) {
                        GlobalValues.DiscoverList.get(num).setState(true);
                        iv.setImageResource(R.mipmap.btn_star_big_on);
                        DatabaseManager.getInstance().
                                addFriend(GlobalValues.DiscoverList.get(num).getUserid());
                    }
            }
        }
    };

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_discover,null);
        initPTRListView(view);
        initViews(view);
        return view;
    }

    /**
     * 设置下拉刷新的listview的动作
     */
    private void initPTRListView(View mView) {
        mPullToRefreshListView = (PullToRefreshListView) mView.findViewById(R.id.ptr_lv_discover);
        //设置拉动监听器
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                //设置下拉时显示的日期和时间
                String label = DateUtils.formatDateTime(
                        getContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                // 更新显示的label
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                // 开始执行异步任务，传入适配器来进行数据改变
                new GetDiscoverData(mPullToRefreshListView, mAdapter,str).execute();
            }
        });

        // 添加滑动到底部的监听器
        mPullToRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(getContext(), "已经到底了", Toast.LENGTH_SHORT).show();//
            }
        });

        //在刷新时允许继续滑动
        mPullToRefreshListView.setScrollingWhileRefreshingEnabled(false);
        //mPullRefreshListView.getMode();//得到模式
        //上下都可以刷新的模式。这里有两个选择：Mode.PULL_FROM_START，Mode.BOTH，PULL_FROM_END
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

    }

    public void initViews(View v)
    {
        mAdapter = new DiscoverListAdapter( getActivity().getApplicationContext(), GlobalValues.DiscoverList);
        ListView mListView = mPullToRefreshListView.getRefreshableView();

        btRadar = (Button)  v.findViewById(R.id.bt_sport_radar);
        btSearch = (Button) v.findViewById(R.id.bt_search_friend);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchFriendActivity.class));
            }
        });
        btRadar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.setShortToast(getContext(), "敬请期待");
            }
        });
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>arg0, View arg1, int arg2,long arg3) {
                Message msg = new Message();
                msg.what = UPDATE_VIEW;
                Bundle mBundle = new Bundle();
                mBundle.putInt("number", arg2 - 1);
                msg.setData(mBundle);
                mHandler.sendMessage(msg);
            }
        });

        new Handler().postDelayed(new Runnable(){
            public void run(){
                mPullToRefreshListView.setRefreshing();
            }
        }, 200);
    }
}
