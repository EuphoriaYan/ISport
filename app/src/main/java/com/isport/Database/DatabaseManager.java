package com.isport.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import com.isport.Activity.LoginActivity;
import com.isport.Application.App;
import com.isport.Bean.*;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Euphoria on 2017/7/29.
 */

public class DatabaseManager {

    private static final DatabaseManager instance = new DatabaseManager();
    public static DatabaseManager getInstance()
    {
        return instance;
    }

    /***
     * 增加跑步记录
     ***/
    public void insertRunRecord(final RunRecord runRecord){
        // 请求地址
        String url = GlobalValues.baseUrl + "record/add";
        // 创建StringRequest，定义字符串请求的请求方式为POST
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() { @Override public void onResponse(String s) {} },
                new Response.ErrorListener() { @Override public void onErrorResponse(VolleyError e) {e.printStackTrace(); }}) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<>();
                hashMap.put("speed",String.valueOf(runRecord.getAvgSpeed()));
                hashMap.put("distance",String.valueOf(runRecord.getDistance()));
                hashMap.put("pace",runRecord.getPace());
                hashMap.put("time",String.valueOf(runRecord.getTime()));
                hashMap.put("userid", LocalUser.getInstance().getUserid());
                hashMap.put("createtime", runRecord.getCreateTime());
                hashMap.put("kcal", String.valueOf(runRecord.getKcal()));
                if (runRecord.getSportType().equals("run"))
                    hashMap.put("type","0");
                else if (runRecord.getSportType().equals("walk"))
                    hashMap.put("type","1");
                else
                    hashMap.put("type","2");
                return hashMap;
            }
        };
        // 将请求添加到队列中
        App.getHttpQueue().add(request);
    }

    /***
     * 上传图片
     ***/
    public void UpdatePicture(String filePath, String fileName) {
        String url = GlobalValues.baseUrl + "testUploadFile";
        HashMap<String, String> param = new HashMap<>();
        param.put("filename", fileName);
        File file = new File(filePath);
        FileRequest fileRequest = new FileRequest(url, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError e){ e.printStackTrace(); }
        },
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) { try { JSONObject JObject = new JSONObject(s); } catch(JSONException e ) {} }
                },
                "file1", file, param);
        App.getHttpQueue().add(fileRequest);
    }

    public void getMyShareRecord() {
        String url = GlobalValues.baseUrl + "space/search/userid";
        GlobalValues.MyShareRecord.clear();
        // 创建StringRequest，定义字符串请求的请求方式为POST
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // 请求成功后执行的函数
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray JArray = new JSONObject(s).getJSONArray("data");
                    for (int i = 0; i < JArray.length(); i++)
                    {
                        JSONObject JObject = JArray.getJSONObject(i);
                        ShareRecord shareRecord = new ShareRecord();
                        shareRecord.setUserId(JObject.getString("userId"));
                        shareRecord.setText(JObject.getString("userContext"));
                        shareRecord.setTime(JObject.getString("userTime"));
                        shareRecord.setImgUrl(JObject.getString("userPhoto"));
                        GlobalValues.MyShareRecord.add(shareRecord);
                    }
                } catch (JSONException JException){
                    JException.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            // 请求失败时执行的函数
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        })
        {
            // 定义请求数据
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("userid", LocalUser.getInstance().getUserid());
                return hashMap;
            }
        };
        // 将请求添加到队列中
        App.getHttpQueue().add(request);
    }


    public void addFriend(final String friendNumber) {
        String url = GlobalValues.baseUrl + "friend/add";
        // 创建StringRequest，定义字符串请求的请求方式为POST
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // 请求成功后执行的函数
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray JArray = new JSONObject(s).getJSONArray("data");
                    for (int i = 0; i < JArray.length(); i++)
                    {
                        JSONObject JObject = JArray.getJSONObject(i);
                    }
                } catch (JSONException JException){
                    JException.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            // 请求失败时执行的函数
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        })
        {
            // 定义请求数据
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("userid", LocalUser.getInstance().getUserid());
                hashMap.put("friendid", friendNumber);
                return hashMap;
            }
        };
        // 将请求添加到队列中
        App.getHttpQueue().add(request);
    }

    public void insertShareRecord(final ShareRecord shareRecord) {
        String url = GlobalValues.baseUrl + "space/add";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONArray JArray = new JSONObject(s).getJSONArray("data");
                } catch (JSONException JException){
                    JException.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            // 请求失败时执行的函数
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        })
        {
            // 定义请求数据
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("userid",shareRecord.getUserId());
                hashMap.put("photo",shareRecord.getImgUrl());
                if (!shareRecord.getText().isEmpty()) {
                    hashMap.put("context", shareRecord.getText());
                }
                hashMap.put("time",shareRecord.getTime());
                return hashMap;
            }
        };
        // 将请求添加到队列中
        App.getHttpQueue().add(request);
    }

}
