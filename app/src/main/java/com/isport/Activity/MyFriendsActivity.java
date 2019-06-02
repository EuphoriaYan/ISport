package com.isport.Activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.isport.Fragment.DiscoverFragment;
import com.isport.Fragment.FriendFragment;
import com.isport.R;

/**
 * Created by Euphoria on 2017/8/2.
 */

public class MyFriendsActivity extends AppCompatActivity {

    private FrameLayout mFrameContent;
    private RadioGroup mRadioGroup;
    private RadioButton mFriendRb;
    private RadioButton mDiscoverRb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfriends);
        initView();
    }

    public void initView()
    {
        mFrameContent = (FrameLayout) findViewById(R.id.fl_friend_frame); //tab上方的区域;
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_friend);  //底部的tab
        mFriendRb = (RadioButton) findViewById(R.id.rb_friend);
        mDiscoverRb = (RadioButton) findViewById(R.id.rb_discover);

        //监听事件：为底部的RadioGroup绑定状态改变的监听事件
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int arg1) {
                int mCheckedID = group.getCheckedRadioButtonId();
                int mFragmentIndex = 0;
                switch (mCheckedID) {
                    case R.id.rb_friend:
                        mFragmentIndex = 0; break;
                    case R.id.rb_discover:
                        mFragmentIndex = 1; break;
                }
                //通过mFragments这个adapter还有index来替换帧布局中的内容，当前帧为mNowFragment
                Fragment mNowFragment = (Fragment) mFragments.instantiateItem(mFrameContent, mFragmentIndex);
                //一开始将帧布局中的内容设置为第一个
                mFragments.setPrimaryItem(mFrameContent, 0, mNowFragment);
                mFragments.finishUpdate(mFrameContent);
            }
        });
        mRadioGroup.check(R.id.rb_friend);
    }

    FragmentStatePagerAdapter mFragments = new FragmentStatePagerAdapter(getSupportFragmentManager())
    {
        @Override
        public int getCount() { return 3; }

        @Override
        public Fragment getItem(int mFragmentIndex)
        {
            Fragment mFragment = null;
            switch (mFragmentIndex)
            {
                case 0: //首页
                    mFragment = new FriendFragment();
                    break;
                case 1: //订单
                    mFragment = new DiscoverFragment();
                    break;
            }
            return mFragment;
        }
    };

}
