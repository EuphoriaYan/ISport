package com.isport.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import com.isport.R;

/**
 * Created by Euphoria on 2017/8/3.
 */

public class CountdownActivity extends Activity {

    private TextView tvNumber;
    private MyCountTimer timer;
    private String sportType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent mIntent = getIntent();
        Bundle mBundle = mIntent.getExtras();
        sportType = mBundle.getString("sportType");
        setContentView(R.layout.activity_countdown);
        tvNumber = (TextView) findViewById(R.id.tv_number);
        timer = new MyCountTimer(4000, 1000);//启动计时器
        timer.start();
    }


    /***
     * 计时器
     */

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            YoYo.with(Techniques.FlipInX)
                    .duration(700)
                    .playOn(findViewById(R.id.tv_number));
            tvNumber.setText(String.valueOf(millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            Intent intent = new Intent(CountdownActivity.this, RunActivity.class);
            intent.putExtra("sportType", sportType);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_exit, R.anim.anim_enter);
            finish();

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
