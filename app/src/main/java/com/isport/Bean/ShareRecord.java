package com.isport.Bean;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Euphoria on 2017/8/2.
 */

public class ShareRecord {
    private String text = null;
    private String imgUrl = null;
    private String time = null;
    private String userId = null;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public void setText(String text) {
        this.text = text;
    }
    public String getText() {
        return this.text == null ? null : this.text;
    }
    public void setImgUrl(String imgUrl){
        this.imgUrl = imgUrl;
    }
    public String getImgUrl(){
        return this.imgUrl;
    }
    public void setTime(String time){
        this.time = time;
    }
    public String getTime() {
        return this.time;
    }
}
