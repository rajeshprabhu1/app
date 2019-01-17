package com.ouam.app.entity;

import java.io.Serializable;

/**
 * Created by sys on 10-May-17.
 */

public class ChatEntity implements Serializable {
    private String msg;
    private String sender;
    private boolean isUserTyping;
    private long timeStamp;


    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        if (msg == null) {
            msg = "";
        }
        this.msg = msg;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        if (sender == null) {
            sender = "0";
        }
        this.sender = sender;
    }

    public boolean isUserTyping() {
        return isUserTyping;
    }

    public void setUserTyping(boolean userTyping) {
        isUserTyping = userTyping;
    }


}
