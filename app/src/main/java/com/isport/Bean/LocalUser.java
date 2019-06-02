package com.isport.Bean;

/**
 * Created by Euphoria on 2017/7/29.
 */

//本地用户，单例模式

public class LocalUser extends User {
    private String msg;
    private String loginPassword;

    private LocalUser()
    {
        msg = "0";
    }
    private static final LocalUser instance = new LocalUser();
    public static LocalUser getInstance()
    {
        return instance;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg)
    {
        this.msg = msg;
    }
    public String getLoginPassword() {
        return loginPassword;
    }
    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }
}
