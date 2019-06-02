package com.isport.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.isport.Application.App;
import com.isport.Bean.GlobalValues;
import com.isport.Bean.LocalUser;
import com.isport.Bean.RunRecord;
import com.isport.Database.DatabaseManager;
import com.isport.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogRecord;

/**
 * Created by Euphoria on 2017/9/2.
 */

public class HomeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

    TextView tvUserNickName;
    TextView tvUserID;
    TextView tvSuggestionKcal;

    String mUserNickname;
    String mUserID;
    String mSuggestionKcal;

    TextView tvTodayKcal;
    TextView tvTotalDis;
    TextView tvTotalTime;
    TextView tvTotalTimes;

    protected final int UPDATE_WEATHER_VIEW = 0x1;
    protected final int UPDATE_USER_VIEW_STEP1 = 0x2;
    protected final int UPDATE_USER_VIEW_STEP2 = 0x3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        initViews(view);
        return view;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_WEATHER_VIEW:
                    tvWeatherAirQuality.setText("空气质量：" + mAirQuality);
                    tvWeatherCity.setText(mCity);
                    tvWeatherDegree.setText(mDegree + "°C");
                    tvWeatherNow.setText(mNow);
                    tvWeatherSuggestion.setText(mSuggestion);
                    break;
                case UPDATE_USER_VIEW_STEP1:
                    tvUserNickName.setText(mUserNickname);
                    tvUserID.setText(mUserID);
                    tvSuggestionKcal.setText(mSuggestionKcal + " kcal");
                    break;
                case UPDATE_USER_VIEW_STEP2:
                    double mTotalDis = 0.0;
                    int mTotalTime = 0;
                    int mKcal = 0;
                    for (int i = 0; i < GlobalValues.AllRunRecord.size(); i++) {
                        RunRecord mRunRecord = GlobalValues.AllRunRecord.get(i);
                        mTotalTime += mRunRecord.getTime();
                        mTotalDis += mRunRecord.getDistance();
                        DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        DateFormat mCompareDateFormate = new SimpleDateFormat("yyyyMMdd");
                        Date mDate = null;
                        String mDate1 = null;
                        String mDate2 = null;
                        try {
                            mDate = mDateFormat.parse(mRunRecord.getCreateTime());
                            mDate1 = mCompareDateFormate.format(mDate);
                            mDate2 = mCompareDateFormate.format(new Date());
                            if (mDate1.equals(mDate2)) {
                                mKcal += mRunRecord.getKcal();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    tvTotalTimes.setText(String.valueOf(GlobalValues.AllRunRecord.size()));
                    tvTotalTime.setText(String.valueOf(mTotalTime));
                    DecimalFormat mDecimalFormat = new DecimalFormat("#0.00");
                    tvTotalDis.setText(mDecimalFormat.format(mTotalDis) + "KM");
                    tvTodayKcal.setText(String.valueOf(mKcal) + " kcal");
                    break;
            }
        }
    };

    public void initViews(View v) {
        tvWeatherAirQuality = (TextView) v.findViewById(R.id.tv_weather_air_quality);
        tvWeatherCity = (TextView) v.findViewById(R.id.tv_weather_city);
        tvWeatherDegree = (TextView) v.findViewById(R.id.tv_weather_degree);
        tvWeatherNow = (TextView) v.findViewById(R.id.tv_weather_now);
        tvWeatherSuggestion = (TextView) v.findViewById(R.id.tv_weather_suggestion);

        tvUserID = (TextView) v.findViewById(R.id.tv_user_userid_homefragment);
        tvUserNickName = (TextView) v.findViewById(R.id.tv_user_nickname_homefragment);
        tvSuggestionKcal = (TextView) v.findViewById(R.id.tv_user_suggestion_kcal);

        tvTodayKcal = (TextView) v.findViewById(R.id.tv_user_today_kcal);
        tvTotalDis = (TextView) v.findViewById(R.id.tv_user_total_distance);
        tvTotalTime = (TextView) v.findViewById(R.id.tv_user_total_time);
        tvTotalTimes = (TextView) v.findViewById(R.id.tv_user_total_times);

        getWeatherInfo();
        getUserInfo();
        getUserAllRunRecord();
    }

    protected void getWeatherInfo() {
        final String url ="https://free-api.heweather.com/v5/weather?city=beijing&key=" + apikey;
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestFuture future = RequestFuture.newFuture();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, future, future);
                App.getHttpQueue().add(stringRequest);
                try {
                    String s = (String) future.get();
                    try {
                        JSONObject JObject = new JSONObject(s).getJSONArray("HeWeather5").getJSONObject(0);
                        mCity = JObject.getJSONObject("basic").getString("city");
                        mAirQuality = JObject.getJSONObject("aqi").getJSONObject("city").getString("aqi") + ' ' +
                                JObject.getJSONObject("aqi").getJSONObject("city").getString("qlty");
                        mDegree = JObject.getJSONObject("now").getString("tmp");
                        mNow = JObject.getJSONObject("now").getJSONObject("cond").getString("txt");
                        mSuggestion = JObject.getJSONObject("suggestion").getJSONObject("sport").getString("txt");
                        Message msg = new Message();
                        msg.what = UPDATE_WEATHER_VIEW;
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

    protected void getUserInfo() {
        mUserID = LocalUser.getInstance().getUserid();
        mUserNickname = LocalUser.getInstance().getNickName();
        Float mWeight = Float.valueOf(LocalUser.getInstance().getWeight());
        Float mHeight = Float.valueOf(LocalUser.getInstance().getHeight());
        String mSex = LocalUser.getInstance().getSex();
        Double mSuggestionKcal_f;
        if (mSex.equals("女")){
            mSuggestionKcal_f = 655 + 9.6 * mWeight + 1.8 * mHeight - 4.7 * 20;
        } else {
            mSuggestionKcal_f = 66 + 13.7 * mWeight + 5 * mHeight - 6.8 * 20;
        }
        mSuggestionKcal_f *= 0.2;

        mSuggestionKcal = String.valueOf(mSuggestionKcal_f);

        Message msg = new Message();
        msg.what = UPDATE_USER_VIEW_STEP1;
        mHandler.sendMessage(msg);
    }

    protected void getUserAllRunRecord() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                GlobalValues.AllRunRecord.clear();
                RequestFuture future = RequestFuture.newFuture();
                String url = GlobalValues.baseUrl + "record/search/userid";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, future, future){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> hashMap = new HashMap<>();
                        hashMap.put("userid", LocalUser.getInstance().getUserid());
                        return hashMap;
                    }
                };
                App.getHttpQueue().add(stringRequest);
                try {
                    String s = (String) future.get();
                    try {
                        JSONArray JArray = new JSONObject(s).getJSONArray("data");
                        for (int i = 0; i < JArray.length(); i++) {
                            JSONObject JObject = JArray.getJSONObject(i);
                            RunRecord runRecord = new RunRecord();
                            runRecord.setAvgSpeed(JObject.getDouble("userSpeed"));
                            runRecord.setCreateTime(JObject.getString("userCreateTime"));
                            runRecord.setDistance(JObject.getDouble("userDistance"));
                            runRecord.setPace(JObject.getString("userPace"));
                            runRecord.setTime(JObject.getInt("userTime"));
                            GlobalValues.AllRunRecord.add(runRecord);
                        }
                        Message msg = new Message();
                        msg.what = UPDATE_USER_VIEW_STEP2;
                        mHandler.sendMessage(msg);
                    } catch (JSONException e) {
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

}
