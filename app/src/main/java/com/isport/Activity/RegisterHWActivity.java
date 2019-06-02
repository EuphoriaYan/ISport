package com.isport.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.isport.Application.App;
import com.isport.Bean.GlobalValues;
import com.isport.Bean.LocalUser;
import com.isport.Bean.User;
import com.isport.R;
import com.isport.UI.DecimalScaleRulerView;
import com.isport.UI.ScaleRulerView;
import com.isport.Utils.DrawUtil;
import com.isport.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Euphoria on 2017/8/30.
 */

public class RegisterHWActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.scaleWheelView_height)
    ScaleRulerView mHeightWheelView;

    @Bind(R.id.tv_user_height_value)
    TextView mHeightValue;

    @Bind(R.id.ruler_weight)
    DecimalScaleRulerView mWeightRulerView;

    @Bind(R.id.tv_user_weight_value_two)
    TextView mWeightValueTwo;

    @Bind(R.id.bt_register_finish)
    Button btFinish;

    @Bind(R.id.tv_register_cancel)
    TextView tvCancel;

    @Bind(R.id.et_register_favourite_sport)
    EditText etFavouriteSport;

    private float mHeight = 170;
    private float mMaxHeight = 220;
    private float mMinHeight = 100;

    private float mWeight = 60.0f;
    private float mMaxWeight = 150;
    private float mMinWeight = 25;

    private LocalUser user; //用户

    private static final int REGISTER_RIGHT = 0x01;
    private static final int REGISTER_WRONG = 0x02;

    Handler mHandler= new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REGISTER_RIGHT:
                    showSuccessDialog();
                    break;
                case REGISTER_WRONG:
                    showFailureDialog();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_hw);
        ButterKnife.bind(this);  //依赖注入
        init();
        btFinish.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        etFavouriteSport.setOnClickListener(this);
    }

    private void init() {
        mHeightValue.setText(String.valueOf(mHeight));
        mWeightValueTwo.setText(mWeight + "kg");

        mHeightWheelView.initViewParam(mHeight, mMaxHeight, mMinHeight);
        mHeightWheelView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mHeightValue.setText((int) value + "");
                mHeight = value;
            }
        });

        mWeightRulerView.setParam(
                DrawUtil.dip2px(10), DrawUtil.dip2px(32), DrawUtil.dip2px(24),
                DrawUtil.dip2px(14), DrawUtil.dip2px(9),  DrawUtil.dip2px(12));
        mWeightRulerView.initViewParam(mWeight, mMinWeight, mMaxWeight, 1);
        mWeightRulerView.setValueChangeListener(new DecimalScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mWeightValueTwo.setText(value + "kg");
                mWeight = value;
            }
        });
    }

    /**
     * 注册用户
     */
    private void registerUser(){
        user = LocalUser.getInstance();
        user.setHeight(String.valueOf(mHeight));
        user.setWeight(String.valueOf(mWeight));
        user.setFavouriteSport(etFavouriteSport.getText().toString());
        final String url = GlobalValues.baseUrl + "information/add";
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestFuture future = RequestFuture.newFuture();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, future, future)
                {
                    //定义请求数据
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> hashMap = new HashMap<>();
                        hashMap.put("userid", user.getUserid());
                        hashMap.put("password", user.getLoginPassword());
                        hashMap.put("nickname", user.getNickName());
                        hashMap.put("height", user.getHeight());
                        hashMap.put("weight", user.getWeight());

                        if (user.getSex().equals("女")) {
                            hashMap.put("sex", "female");
                        } else {
                            hashMap.put("sex", "male");
                        }

                        if (user.getFavouriteSport().equals("跑步")) {
                            hashMap.put("habit", "run");
                        } else if (user.getFavouriteSport().equals("骑行")){
                            hashMap.put("habit", "bike");
                        } else {
                            hashMap.put("habit", "walk");
                        }
                        return hashMap;
                    }
                };
                App.getHttpQueue().add(stringRequest);
                String s;
                try {
                    s = (String) future.get();
                    try {
                        JSONObject JObject = new JSONObject(s);
                        String msg = JObject.getString("msg");
                        Message message = new Message();
                        if (msg.equals("添加用户成功")){
                            message.what = REGISTER_RIGHT;
                        } else {
                            message.what = REGISTER_WRONG;
                        }
                        mHandler.sendMessage(message);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String[] sportArray = new String[] { "跑步", "骑行", "步行" };// 性别选择

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_register_finish:
                if (!etFavouriteSport.getText().toString().isEmpty()) {
                    showProgressDialog(this,"正在注册...");
                    registerUser();
                } else {
                    ToastUtil.setShortToast(context, "喜爱运动不能为空");
                }
                break;
            case R.id.tv_register_cancel:
                showCancelDialog();
                break;
            case R.id.et_register_favourite_sport:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
                builder.setSingleChoiceItems(sportArray, 0, new DialogInterface.OnClickListener() {// 2默认的选中
                    @Override
                    public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                        etFavouriteSport.setText(sportArray[which]);
                        dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
                    }
                });
                builder.show();// 让弹出框显示
                break;
        }
    }

    /**
     * 显示dialog
     * **/
    private void showSuccessDialog(){
        closeProgressDialog();
        new com.isport.UI.AlertDialog(this, "注意", "注册成功，请登录!", false, 0,
                new com.isport.UI.AlertDialog.OnDialogButtonClickListener() {
                    @Override
                    public void onDialogButtonClick(int requestCode, boolean isPositive) {
                        if(isPositive){
                            Intent intent = new Intent(RegisterHWActivity.this,LoginActivity.class);
                            startActivity(intent);
                            RegisterHWActivity.this.finish();
                        }
                    }
        }).show();
    }
    /**
     * 显示失败dialog
     * **/
    private void showFailureDialog(){
        closeProgressDialog();
        new com.isport.UI.AlertDialog(this, "注册失败", "是否重试？", true, 0,
                new com.isport.UI.AlertDialog.OnDialogButtonClickListener() {
                    @Override
                    public void onDialogButtonClick(int requestCode, boolean isPositive) {
                        if(isPositive) {
                            return;
                        } else {
                            finish();
                        }
                    }
        }).show();
    }

    /**
     * 显示取消注册dialog
     * **/
    private void showCancelDialog(){
        new com.isport.UI.AlertDialog(this, "取消注册", "确定取消注册？", true, 0,
                new com.isport.UI.AlertDialog.OnDialogButtonClickListener() {
                    @Override
                    public void onDialogButtonClick(int requestCode, boolean isPositive) {
                        if(isPositive){
                            finish();
                        } else {
                            return;
                        }
                    }
        }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}