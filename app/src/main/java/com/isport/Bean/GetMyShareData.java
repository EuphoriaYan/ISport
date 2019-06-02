package com.isport.Bean;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.isport.Adapter.DiscoverListAdapter;
import com.isport.Adapter.ShareRecordListAdapter;
import com.isport.Application.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Euphoria on 2017/9/9.
 */

public class GetMyShareData extends AsyncTask<Void, Void, Void> {

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 通知数据改变了
            mAdapter.notifyDataSetChanged();
            // 加载完成后停止刷新
            mPTRScrollView.onRefreshComplete();
        }
    };
    private PullToRefreshScrollView mPTRScrollView;
    private ListView mListView;
    private ShareRecordListAdapter mAdapter;
    private List<String> mListItems;

    public GetMyShareData(
            PullToRefreshScrollView PTRScrollView,
            ShareRecordListAdapter adapter,
            List<String> listItems,
            ListView listView) {
        // TODO 自动生成的构造函数存根
        mPTRScrollView = PTRScrollView;
        mListView = listView;
        mAdapter = adapter;
        mListItems = listItems;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //模拟请求
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // TODO 自动生成的方法存根
        super.onPostExecute(result);
        //得到当前的模式
        PullToRefreshBase.Mode mode = mPTRScrollView.getCurrentMode();
        if(mode == PullToRefreshBase.Mode.PULL_FROM_START) {
            final String url = GlobalValues.baseUrl + "space/search/userid";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GlobalValues.MyShareRecord.clear();
                    RequestFuture future = RequestFuture.newFuture();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, future, future) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> hashMap = new HashMap<>();
                            hashMap.put("userid", LocalUser.getInstance().getUserid());
                            return hashMap;
                        }
                    };
                    App.getHttpQueue().add(stringRequest);
                    String s;
                    try {
                        s = (String) future.get();
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
                                GlobalValues.MyShareRecord.add(0, shareRecord);
                            }
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
    }
}
