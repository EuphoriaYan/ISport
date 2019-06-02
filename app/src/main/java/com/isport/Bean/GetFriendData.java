package com.isport.Bean;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.isport.Adapter.FriendListAdapter;
import com.isport.Application.App;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Euphoria on 2017/8/12.
 */

public class GetFriendData extends AsyncTask<Void, Void, Void> {

    private PullToRefreshListView mPullToRefreshListView;
    private FriendListAdapter mAdapter;
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

    public GetFriendData(PullToRefreshListView listView, FriendListAdapter adapter, List<String> listItems) {
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
            final String url = GlobalValues.baseUrl + "friend/search/userid";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GlobalValues.FriendList.clear();
                    RequestFuture future = RequestFuture.newFuture();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, future, future)
                    {
                        // 定义请求数据
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> hashMap = new HashMap<String, String>();
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
                                Friend friend = new Friend();
                                friend.setUserid(JObject.getString("userId"));
                                friend.setNickName(JObject.getString("nickName"));
                                friend.setSex(JObject.getString("userSex"));
                                GlobalValues.FriendList.add(friend);
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
