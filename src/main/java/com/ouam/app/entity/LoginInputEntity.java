package com.ouam.app.entity;


import java.io.Serializable;

public class LoginInputEntity implements Serializable {


    private String provider;
    private String accessToken;

    public String getProvider() {
        return provider == null ? "" : provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAccessToken() {
        return accessToken == null ? "" : accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
