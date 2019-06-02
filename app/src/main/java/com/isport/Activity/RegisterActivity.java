package com.isport.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.isport.Database.DatabaseManager;
import com.isport.R;
import com.isport.Utils.GeneralUtil;
import com.isport.Utils.StringUtil;
import com.isport.Utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Euphoria on 2017/7/22.
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener{
    EditText etRegisterUserId;
    EditText etRegisterPassword;
    EditText etRegisterPassword2;
    EditText etRegisterNickName;
    EditText etRegisterSex;

    Button btRegisterNext;

    String registerUserID;
    String registerPassword;
    String registerPassword2;
    String registerNickname;
    String registerSex;

    private final int NEXT_SUCCESS = 0x01;
    private final int NEXT_FAILURE = 0x02;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case NEXT_SUCCESS:
                    closeProgressDialog();
                    LocalUser user = LocalUser.getInstance();
                    user.setSex(registerSex);
                    user.setNickName(registerNickname);
                    user.setUserid(registerUserID);
                    user.setLoginPassword(registerPassword);
                    Intent intent = new Intent(RegisterActivity.this,RegisterHWActivity.class);
                    startActivity(intent);
                    RegisterActivity.this.finish();
                    break;
                case NEXT_FAILURE:
                    closeProgressDialog();
                    new com.isport.UI.AlertDialog(RegisterActivity.this, "您的用户名重复了！", "是否重试？", true, 0,
                            new com.isport.UI.AlertDialog.OnDialogButtonClickListener() {
                                @Override
                                public void onDialogButtonClick(int requestCode, boolean isPositive) {
                                    if (!isPositive) { finish(); }
                                }
                            }).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponent();
    }

    /**
     * 初始化组件
     **/
    private void initComponent() {
        etRegisterUserId = (EditText) findViewById(R.id.et_register_userid);
        etRegisterPassword = (EditText) findViewById(R.id.et_register_password);
        etRegisterPassword2 = (EditText) findViewById(R.id.et_register_password_again);
        etRegisterSex = (EditText) findViewById(R.id.et_register_sex);
        btRegisterNext = (Button) findViewById(R.id.bt_register_next);
        etRegisterNickName = (EditText) findViewById(R.id.et_register_nickname);

        etRegisterSex.setOnClickListener(this);
        btRegisterNext.setOnClickListener(this);
    }

    private String[] sexArry = new String[] { "男", "女" };// 性别选择

    @Override
    public void onClick(View v) {
        if (!GeneralUtil.isNetworkAvailable(context))
        {
            ToastUtil.setShortToast(context, "未连接网络");
            return;
        }
        switch (v.getId()) {
            case R.id.et_register_sex:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
                builder.setSingleChoiceItems(sexArry, 0, new DialogInterface.OnClickListener() {// 2默认的选中
                    @Override
                    public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                        etRegisterSex.setText(sexArry[which]);
                        dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
                    }
                });
                builder.show();// 让弹出框显示
                break;
            case R.id.bt_register_next: // 下一步
                if (checkInput()) {
                    showProgressDialog(this, "正在检查信息……");
                    // 请求地址
                    final String url = GlobalValues.baseUrl + "information/search/repeat";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RequestFuture future = RequestFuture.newFuture();
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, future, future) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> hashMap = new HashMap<>();
                                    hashMap.put("userid", registerUserID);
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
                                    if (msg.equals("用户名合法")){
                                        message.what = NEXT_SUCCESS;
                                    } else {
                                        message.what = NEXT_FAILURE;
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
                break;
        }
    }

    /***
     * 检查输入
     **/
    private boolean checkInput() {
        registerUserID = etRegisterUserId.getText().toString();
        registerPassword = etRegisterPassword.getText().toString();
        registerPassword2 = etRegisterPassword2.getText().toString();
        registerNickname = etRegisterNickName.getText().toString();
        registerSex = etRegisterSex.getText().toString();
        if (registerUserID.isEmpty()) {
            ToastUtil.setShortToast(context, "用户名不能为空");
            return false;
        }
        if (registerPassword.isEmpty()) {
            ToastUtil.setShortToast(context, "密码不能为空");
            return false;
        }
        if (!registerPassword.equals(registerPassword2)) {
            ToastUtil.setShortToast(context, "两次输入密码不一样");
            return false;
        }
        if (registerNickname.isEmpty()) {
            ToastUtil.setShortToast(context, "昵称不能为空");
            return false;
        }
        if (registerSex.isEmpty()) {
            ToastUtil.setShortToast(context, "性别不能为空");
            return false;
        }
        if (!StringUtil.isLoginNumberValid(registerUserID)) {
            ToastUtil.setShortToast(context, "用户名不合法");
            return false;
        }
        if (!StringUtil.isLoginPasswordValid(registerPassword)) {
            ToastUtil.setShortToast(context, "密码不合法");
            return false;
        }
        return true;
    }
}
