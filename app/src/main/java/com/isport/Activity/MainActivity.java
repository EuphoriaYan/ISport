package com.isport.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.isport.Database.DatabaseManager;
import com.isport.Fragment.DiscoverFragment;
import com.isport.Fragment.FriendFragment;
import com.isport.Fragment.HomeFragment;
import com.isport.Fragment.MeFragment;
import com.isport.Fragment.RunPrepareFragment;
import com.isport.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Euphoria on 2017/7/21.
 */

public class MainActivity extends FragmentActivity {
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        if (Build.VERSION.SDK_INT >= 23) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(MainActivity.this,"自Android 6.0开始需要打开位置权限",Toast.LENGTH_SHORT).show();
                }
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }
        DatabaseManager.getInstance().getMyShareRecord();
    }

    private FragmentTabHost mTabHost;
    private List<Fragment> mFragmentList;
    private Class mClass[] = {
            HomeFragment.class,
            FriendFragment.class,
            RunPrepareFragment.class,
            DiscoverFragment.class,
            MeFragment.class
    };
    private Fragment mFragment[] = {
            new HomeFragment(),
            new FriendFragment(),
            new RunPrepareFragment(),
            new DiscoverFragment(),
            new MeFragment(),
    };
    private String mTitles[] = {
            "首页",
            "朋友",
            "运动",
            "发现",
            "我"
    };
    private int mImages[] = {
            R.drawable.tab_community_icon,
            R.drawable.tab_discover_icon,
            R.drawable.tab_run_icon,
            R.drawable.tab_message_icon,
            R.drawable.tab_me_icon
    };
    private int mLayout[] = {
            R.layout.tab_item_community,
            R.layout.tab_item_discover,
            R.layout.tab_item_run,
            R.layout.tab_item_message,
            R.layout.tab_item_me
    };

    private void initView() {
        //实例化TABHOST对象
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mFragmentList = new ArrayList<Fragment>();
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        for (int i = 0;i < mFragment.length;i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTitles[i]).setIndicator(getTabView(i));
            mTabHost.addTab(tabSpec,mClass[i],null);
            mFragmentList.add(mFragment[i]);
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
        }

        mTabHost.setCurrentTab(0);
    }

    /**
     * 获取一个TAB标签
     * **/
    private View getTabView(int index) {
        View view = LayoutInflater.from(this).inflate(mLayout[index], null);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.title);
        image.setImageResource(mImages[index]);
        title.setText(mTitles[index]);
        return view;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

}
