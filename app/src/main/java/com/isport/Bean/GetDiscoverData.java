package com.isport.Bean;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.widget.SimpleAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.isport.Adapter.DiscoverListAdapter;
import com.isport.Application.App;
import com.isport.Database.DatabaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Euphoria on 2017/8/11.
 */


public class GetDiscoverData extends AsyncTask<Void, Void, Void> {

    private PullToRefreshListView mPullToRefreshListView;
    private DiscoverListAdapter mAdapter;
    private List<String> mListItems;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 通知数据改变了
            mAdapter.notifyDataSetChanged();
            // 加载完成后停止刷新
            mPullToRefreshListView.onRefreshComplete();
        }
    };

    public GetDiscoverData(PullToRefreshListView listView, DiscoverListAdapter adapter, List<String> listItems) {
        // TODO 自动生成的构造函数存根
        mPullToRefreshListView = listView;
        mAdapter = adapter;
        //mAdapter.notifyDataSetChanged();
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
        PullToRefreshBase.Mode mode = mPullToRefreshListView.getCurrentMode();
        if(mode == PullToRefreshBase.Mode.PULL_FROM_START) {
            final String url = GlobalValues.baseUrl + "friend/search/newfriend";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GlobalValues.DiscoverList.clear();
                    RequestFuture future = RequestFuture.newFuture();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, future, future) {
                        // 定义请求数据
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
                            JSONObject JObject = new JSONObject(s);
                            if (s.contains("data")) {
                                JSONArray JArray = new JSONObject(s).getJSONArray("data");
                                for (int i = 0; i < JArray.length(); i++) {
                                    JObject = JArray.getJSONObject(i);
                                    Friend friend = new Friend();
                                    friend.setUserid(JObject.getString("userId"));
                                    friend.setNickName(JObject.getString("nickName"));
                                    friend.setSex(JObject.getString("userSex"));
                                    GlobalValues.DiscoverList.add(friend);
                                }
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
