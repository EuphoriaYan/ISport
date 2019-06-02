package com.isport.Bean;

/**
 * Created by Euphoria on 2017/8/12.
 */

public class Friend extends User {
    private String reason = null;
    private boolean state;
    public Friend(){
        reason = null;
        state = false;
    }
    public String getReason(){
        return this.reason;
    }
    public void setReason(String reason){
        this.reason=reason;
    }
    public boolean getState(){
        return this.state;
    }
    public void setState(boolean state){
        this.state=state;
    }
    public void exState(){
        state= !state;
    }

}
