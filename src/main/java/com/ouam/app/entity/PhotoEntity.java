package com.ouam.app.entity;

import java.io.Serializable;

public class PhotoEntity implements Serializable {

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

}
