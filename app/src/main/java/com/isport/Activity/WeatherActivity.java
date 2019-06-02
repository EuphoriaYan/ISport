package com.isport.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.isport.Application.App;
import com.isport.Bean.GlobalValues;
import com.isport.Bean.LocalUser;
import com.isport.Bean.ShareRecord;
import com.isport.R;
import com.isport.Utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Euphoria on 2017/8/2.
 */

public class WeatherActivity extends Activity{

    private static final String apikey = "d7093e7218f34fe89ab37b202dedd435";

    TextView tvWeatherCity;
    TextView tvWeatherAirQuality;
    TextView tvWeatherNow;
    TextView tvWeatherDegree;
    TextView tvWeatherSuggestion;

    String mCity;
    String mAirQuality;
    String mNow;
    String mDegree;
    String mSuggestion;


    android.os.Handler mHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvWeatherCity.setText(mCity);
            tvWeatherSuggestion.setText(mSuggestion);
            tvWeatherNow.setText(mNow);
            tvWeatherDegree.setText(mDegree);
            tvWeatherAirQuality.setText(mAirQuality);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        final String url ="https://free-api.heweather.com/v5/weather?city=beijing&key=" + apikey;
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestFuture future = RequestFuture.newFuture();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, future, future);
                App.getHttpQueue().add(stringRequest);
                String s;
                try {
                    s = (String) future.get();
                    try {
                        JSONObject JObject = new JSONObject(s).getJSONArray("HeWeather5").getJSONObject(0);
                        mCity = JObject.getJSONObject("basic").getString("city");
                        mAirQuality = JObject.getJSONObject("aqi").getJSONObject("city").getString("aqi") + ' ' +
                                JObject.getJSONObject("aqi").getJSONObject("city").getString("qlty");
                        mDegree = JObject.getJSONObject("now").getString("tmp");
                        mNow = JObject.getJSONObject("now").getJSONObject("cond").getString("txt");
                        mSuggestion = JObject.getJSONObject("suggestion").getJSONObject("sport").getString("txt");
                        Message msg = new Message();
                        mHandler.sendMessage(msg);
                    } catch (JSONException JException){
                        JException.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initView() {
        tvWeatherCity=(TextView)findViewById(R.id.tv_weather_city);
        tvWeatherAirQuality=(TextView)findViewById(R.id.tv_weather_air_quality);
        tvWeatherDegree=(TextView)findViewById(R.id.tv_weather_degree);
        tvWeatherNow=(TextView)findViewById(R.id.tv_weather_now);
        tvWeatherSuggestion=(TextView)findViewById(R.id.tv_weather_suggestion);
    }
}