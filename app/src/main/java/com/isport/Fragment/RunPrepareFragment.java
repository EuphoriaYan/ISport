package com.isport.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.isport.Activity.CountdownActivity;
import com.isport.Activity.RunActivity;
import com.isport.Application.App;
import com.isport.Bean.GlobalValues;
import com.isport.Bean.LocalUser;
import com.isport.Bean.RunRecord;
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

/**
 * Created by Euphoria on 2017/9/2.
 */

public class RunPrepareFragment extends Fragment {

    TextView tvSuggestionKcal;


    String mUserNickname;
    String mUserID;
    String mSuggestionKcal;
    String mLast;
    String mPercent;

    TextView tvTodayKcal;
    TextView tvTodayLast;
    TextView tvTodayPercent;

    private RadioButton rdWalk,rdRun,rdBike;
    private RadioGroup rdGroup;
    Double mSuggestionKcal_f;
    double mTotalDis = 0.0;
    int mTotalTime = 0;
    int mKcal = 0;
    double theRest=0;

    Button btRun;
    String sportType;

    protected final int UPDATE_USER_VIEW_REST = 0x1;
    protected final int UPDATE_USER_VIEW_STEP1 = 0x2;
    protected final int UPDATE_USER_VIEW_STEP2 = 0x3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_runprepare, null);
        initViews(view);
        return view;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_USER_VIEW_STEP1:
                    tvSuggestionKcal.setText(mSuggestionKcal + " kcal");
                    break;
                case UPDATE_USER_VIEW_STEP2:
                    tvTodayKcal.setText(String.valueOf(mKcal) + " kcal");
                    tvTodayPercent.setText(mPercent);
                    break;
                case UPDATE_USER_VIEW_REST:
                    tvTodayLast.setText(mLast);
                    break;
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener rdg_listener=new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int my_last = 0;
            if(checkedId==rdWalk.getId())
            {
                my_last=(int)(theRest / 0.042);
                mLast="你还需慢走" + String.valueOf(my_last)+"步";
                sportType = "walk";
            }else if(checkedId==rdRun.getId())
            {
                my_last=(int)(theRest / 0.051);
                mLast="你还需户外跑" + String.valueOf(my_last)+"步";
                sportType = "run";
            }
            else if(checkedId==rdBike.getId())
            {
                my_last=(int)(theRest / 31.19);
                mLast="你还需骑行" + String.valueOf(my_last)+"公里";
                sportType = "bike";
            }
            Message msg = new Message();
            msg.what = UPDATE_USER_VIEW_REST;
            mHandler.sendMessage(msg);
        }
    };

    public void initViews(View v) {
        tvSuggestionKcal = (TextView) v.findViewById(R.id.tv_user_suggestion_kcal) ;
        tvTodayKcal = (TextView) v.findViewById(R.id.tv_user_today_kcal);
        tvTodayPercent = (TextView) v.findViewById(R.id.tv_user_today_finish_percent);

        tvTodayLast = (TextView) v.findViewById(R.id.tv_user_today_last);
        rdGroup = (RadioGroup) v.findViewById(R.id.radioGroup_sport_kind);
        rdWalk = (RadioButton) v.findViewById(R.id.radio_walk);
        rdRun = (RadioButton) v.findViewById(R.id.radio_run);
        rdBike = (RadioButton) v.findViewById(R.id.radio_bike);
        rdGroup.setOnCheckedChangeListener(rdg_listener);

        btRun = (Button) v.findViewById(R.id.bt_run_start);

        getUserInfo();
        getUserAllRunRecord();
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
        theRest = mSuggestionKcal_f - mKcal;
        Double percent = (double) mKcal / mSuggestionKcal_f * 100.0;
        DecimalFormat mDecimalFormat = new DecimalFormat("#0.00");
        mPercent = mDecimalFormat.format(percent) + "%";

        String mHabit = LocalUser.getInstance().getFavouriteSport();
        if (mHabit.equals("run"))
        {
            rdRun.setChecked(true);
        } else if (mHabit.equals("bike")) {
            rdBike.setChecked(true);
        } else {
            rdWalk.setChecked(true);
        }

        btRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CountdownActivity.class);
                intent.putExtra("sportType", sportType);
                startActivity(intent);
            }
        });
    }

    protected void getUserInfo() {
        mUserID = LocalUser.getInstance().getUserid();
        mUserNickname = LocalUser.getInstance().getNickName();
        Float mWeight = Float.valueOf(LocalUser.getInstance().getWeight());
        Float mHeight = Float.valueOf(LocalUser.getInstance().getHeight());
        String mSex = LocalUser.getInstance().getSex();

        if (mSex.equals("女")) {
            mSuggestionKcal_f = 655 + 9.6 * mWeight + 1.8 * mHeight - 4.7 * 20;
        } else {
            mSuggestionKcal_f = 66 + 13.7 * mWeight + 5 * mHeight - 6.8 * 20;
        }
        mSuggestionKcal_f *= 0.2;

        DecimalFormat mDecimalFormat = new DecimalFormat("#0.00");
        mSuggestionKcal = mDecimalFormat.format(mSuggestionKcal_f);

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
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, future, future) {
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
