package com.isport.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.isport.Adapter.RunRecordListAdapter;
import com.isport.Bean.GlobalValues;
import com.isport.Bean.RunRecord;
import com.isport.R;
import com.isport.Utils.GeneralUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Euphoria on 2017/8/4.
 */

public class RunRecordActivity extends BaseActivity {

    private RunRecordListAdapter mAdapter;
    private List<RunRecord> data = new ArrayList<>(); //本地数据

    private SwipeRefreshLayout swipeRefreshLayout; //下拉刷新组件

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_record);
        initComponent();
        getdata();
        setAdapter();
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.run_record_swiperefreshlayout);
        mListView = (ListView) findViewById(R.id.run_record_listview);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
    }

    /**
     * 获取数据
     */
    private void getdata(){
        //获取本地数据
        data = GlobalValues.AllRunRecord;
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        mAdapter = new RunRecordListAdapter(context, data);
        mListView.setAdapter(mAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(){
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
