package com.ouam.app.entity;

import java.io.Serializable;

public class SignUpEntity implements Serializable {

    private String username;
    private String platform;
    private String platformId;
    private String homeCity;
    private String homeLocality;
    private String homeCountry;
    private String email;
    private String profileImageURL;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username == null ? "" : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlatform() {
        return platform == null ? "" : platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatformId() {
        return platformId == null ? "" : platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getHomeCity() {
        return homeCity == null ? "" : homeCity;
    }

    public void setHomeCity(String homeCity) {
        this.homeCity = homeCity;
    }

    public String getHomeLocality() {
        return homeLocality == null ? "" : homeLocality;
    }

    public void setHomeLocality(String homeLocality) {
        this.homeLocality = homeLocality;
    }

    public String getHomeCountry() {
        return homeCountry == null ? "" : homeCountry;
    }

    public void setHomeCountry(String homeCountry) {
        this.homeCountry = homeCountry;
    }

    public String getProfileImageURL() {
        return profileImageURL == null ? "" : profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
}
