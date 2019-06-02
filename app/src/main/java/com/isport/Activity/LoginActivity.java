package com.isport.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import com.isport.Application.App;
import com.isport.Bean.LocalUser;
import com.isport.Bean.User;
import com.isport.Database.DatabaseManager;
import com.isport.R;
import com.isport.Utils.GeneralUtil;
import com.isport.Utils.StringUtil;
import com.isport.Utils.ToastUtil;
import com.isport.Bean.GlobalValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by Euphoria on 2017/7/23.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    EditText etLoginMobile;
    EditText etLoginPassword;
    Button btLogin;
    Button btRegister;
    TextView tvForget;
    private String loginNumber;//登录手机号码
    private String loginPassword;//登录密码

    private static final int LOGIN_RIGHT = 0x01;
    private static final int LOGIN_WRONG = 0x02;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case LOGIN_RIGHT:
                    ToastUtil.setShortToast(LoginActivity.this, "登陆成功");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    break;
                case LOGIN_WRONG:
                    ToastUtil.setShortToast(LoginActivity.this, "用户名或密码错误");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponent();
    }

    /**
     * 初始化组件
     **/
    private void initComponent() {
        etLoginMobile = (EditText) findViewById(R.id.et_login_mobile);
        etLoginPassword = (EditText) findViewById(R.id.et_login_password);
        btLogin = (Button) findViewById(R.id.bt_login);
        btRegister = (Button) findViewById(R.id.bt_login_register);
        tvForget = (TextView) findViewById(R.id.tv_forget_pwd);

        btLogin.setOnClickListener(this);
        btRegister.setOnClickListener(this);
        tvForget.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!GeneralUtil.isNetworkAvailable(context))
        {
            ToastUtil.setShortToast(context, "未连接网络");
            return;
        }
        switch (v.getId()) {
            case R.id.bt_login: // 登录
                if (checkInput()) {
/*
                    if (loginPassword.equals("123456")&&loginNumber.equals("123456"))
                    {
                        LocalUser.getInstance().setUserid("123456");
                        LocalUser.getInstance().setNickName("zhangsan");
                        LocalUser.getInstance().setSex("female");
                        LocalUser.getInstance().setHeight("160");
                        LocalUser.getInstance().setWeight("50");
                        LocalUser.getInstance().setLoginPassword("123456");
                        Message msg = new Message();
                        msg.what = LOGIN_RIGHT;
                        mHandler.sendMessage(msg);
                        break;
                    }
*/
                    showProgressDialog(this,"正在登录...");
                    final String url = GlobalValues.baseUrl + "information/search/check";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RequestFuture future = RequestFuture.newFuture();
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, future, future)
                            {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> hashMap = new HashMap<>();
                                    hashMap.put("userid",loginNumber);
                                    hashMap.put("password",loginPassword);
                                    return hashMap;
                                }
                            };
                            App.getHttpQueue().add(stringRequest);
                            String s;
                            try {
                                s = (String) future.get();
                                resolveJSON(s);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            onResponse();
                        }
                    }).start();
                }
                break;
            case R.id.bt_login_register: // 注册
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.tv_forget_pwd:// 忘记密码
                Intent resetPwdIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(resetPwdIntent);
                break;
        }
    }

    public void resolveJSON(String s){
        try {
            JSONObject JObject = new JSONObject(s);
            String msg = JObject.getString("msg");
            LocalUser.getInstance().setMsg(msg);
            if (msg.equals("查找失败")) {
                return;
            }
            JObject = JObject.getJSONObject("data");
            LocalUser.getInstance().setNickName(JObject.getString("nickName"));
            LocalUser.getInstance().setSex(JObject.getString("userSex"));
            LocalUser.getInstance().setHeight(JObject.getString("userHeight"));
            LocalUser.getInstance().setWeight(JObject.getString("userWeight"));
            if(JObject.getString("userSex").equals("male")){
                LocalUser.getInstance().setSex("男");
            } else {
                LocalUser.getInstance().setSex("女");
            }
            LocalUser.getInstance().setFavouriteSport(JObject.getString("userHabit"));
            LocalUser.getInstance().setUserid(loginNumber);
            LocalUser.getInstance().setLoginPassword(loginPassword);
        } catch (JSONException JException){
            JException.printStackTrace();
        }
    }

    public void onResponse(){
        Message msg = new Message();
        if (LocalUser.getInstance().getMsg().equals("查找成功")) {
            msg.what = LOGIN_RIGHT;
        } else {
            msg.what = LOGIN_WRONG;
        }
        mHandler.sendMessage(msg);
    }

    /***
     * 检查输入
     **/
    private boolean checkInput() {
        loginNumber = etLoginMobile.getText().toString();
        loginPassword = etLoginPassword.getText().toString();

        if (loginNumber.isEmpty()) {
            ToastUtil.setShortToast(context, "用户名不能为空");
            return false;
        } else if (loginPassword.isEmpty()) {
            ToastUtil.setShortToast(context, "密码不能为空");
            return false;
        } else if (!StringUtil.isLoginNumberValid(loginNumber)) {
            ToastUtil.setShortToast(context, "用户名不合法");
            return false;
        } else if (!StringUtil.isLoginPasswordValid(loginPassword)) {
            ToastUtil.setShortToast(context, "密码不合法");
            return false;
        }
        return true;
    }

}
