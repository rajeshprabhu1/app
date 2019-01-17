package com.ouam.app.model;

import java.io.Serializable;

public class TinyUrlResponse implements Serializable{
    public String getShortLink() {
        return shortLink == null ? "" : shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    private String shortLink;

}
