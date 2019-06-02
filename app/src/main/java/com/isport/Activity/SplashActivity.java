package com.isport.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.isport.Adapter.GuiderViewPagerAdapter;
import com.isport.Bean.User;
import com.isport.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Euphoria on 2017/7/26.
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    GotoLoginActivity();
                }
            },2000);
    }

    private void GotoLoginActivity(){
        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }


}
