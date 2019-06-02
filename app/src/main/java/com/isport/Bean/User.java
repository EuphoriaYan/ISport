package com.isport.Bean;

/**
 * Created by Euphoria on 2017/7/26.
 */

public class User {
    private String userid;
    private String nickName;
    private String sex;
    private String height;
    private String weight;
    private String favouriteSport;

    public String getFavouriteSport() {
        return favouriteSport;
    }
    public void setFavouriteSport(String favouriteSport) {
        this.favouriteSport = favouriteSport;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getHeight() {
        return height;
    }
    public void setHeight(String height) {
        this.height = height;
    }
    public String getWeight() {
        return weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
}
