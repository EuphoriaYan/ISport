package com.isport.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Euphoria on 2017/7/22.
 */

public class BaseActivity extends AppCompatActivity {

    public Context context;

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        SDKInitializer.initialize(context);
    }

    /**
     * 开启进度弹窗
     * @param content
     */
    public void showProgressDialog(Context c,String content){
        if (progressDialog==null) {
            progressDialog = new ProgressDialog(c);
            progressDialog.setMessage(content);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度弹窗
     */
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
