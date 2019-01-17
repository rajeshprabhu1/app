package com.ouam.app.entity;

import java.io.Serializable;

public class TinyBodyEntity implements Serializable{
    public String getLongUrl() {
        return longDynamicLink == null ? "" : longDynamicLink;
    }

    public void setLongUrl(String longUrl) {
        this.longDynamicLink = longUrl;
    }

    private String longDynamicLink;
}
