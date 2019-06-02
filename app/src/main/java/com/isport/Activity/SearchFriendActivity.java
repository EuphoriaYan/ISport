package com.isport.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.isport.Adapter.DiscoverListAdapter;
import com.isport.Application.App;
import com.isport.Bean.Friend;
import com.isport.Bean.GlobalValues;
import com.isport.Bean.LocalUser;
import com.isport.Database.DatabaseManager;
import com.isport.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Euphoria on 2017/9/13.
 */

public class SearchFriendActivity extends Activity {

    EditText etSearch;
    Handler mHandler;
    SearchTask mSearchTask;
    ListView lvSearch;

    private DiscoverListAdapter mAdapter;

    private final int UPDATE_LIST = 0x1;
    private final int UPDATE_VIEW = 0x2;

    Handler mUIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LIST:
                    mAdapter.notifyDataSetChanged();
                    break;
                case UPDATE_VIEW:
                    Bundle bundle = msg.getData();
                    int num = (int) bundle.get("number");
                    View v = getViewByPosition(num+1, lvSearch);
                    ImageView iv = (ImageView) v.findViewById(R.id.iv_discover_listholder_like);
                    if (!GlobalValues.DiscoverList.get(num).getState()) {
                        GlobalValues.DiscoverList.get(num).setState(true);
                        iv.setImageResource(R.mipmap.btn_star_big_on);
                        DatabaseManager.getInstance().
                                addFriend(GlobalValues.DiscoverList.get(num).getUserid());
                    }
            }
        }
    };

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchfriend);
        initView();
        mHandler = new Handler();
        mSearchTask = new SearchTask();
    }

    private void initView() {
        etSearch = (EditText) findViewById(R.id.et_search_friend);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {
                    mHandler.removeCallbacks(mSearchTask);
                    mHandler.postDelayed(mSearchTask, 500);
                } else {
                    mHandler.removeCallbacks(mSearchTask);
                    GlobalValues.DiscoverList.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        lvSearch = (ListView) findViewById(R.id.lv_search_friend);
        mAdapter = new DiscoverListAdapter(getApplicationContext(),GlobalValues.DiscoverList);
        GlobalValues.DiscoverList.clear();
        lvSearch.setAdapter(mAdapter);
        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>arg0, View arg1, int arg2, long arg3) {
                Message msg = new Message();
                msg.what = UPDATE_VIEW;
                Bundle mBundle = new Bundle();
                mBundle.putInt("number", arg2 - 1);
                msg.setData(mBundle);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 搜索任务
     */
    class SearchTask implements Runnable {
        @Override
        public void run() {
            final String search_content = etSearch.getText().toString();
            final String url = GlobalValues.baseUrl + "friend/search/nickname";
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
                            hashMap.put("nickname", search_content);
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
                                GlobalValues.DiscoverList.add(friend);
                            }
                            Message msg = new Message();
                            msg.what = UPDATE_LIST;
                            mUIHandler.sendMessage(msg);
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
