package com.ouam.app.entity;


import java.io.Serializable;

public class UserProfileEntity implements Serializable {

    private boolean followsYou;
    private boolean youFollow;
    public UserEntity user = new UserEntity();
    private String bio = "";
    private String url = "";
    private String instagram = "";

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }


    public boolean isFollowsYou() {
        return followsYou;
    }

    public void setFollowsYou(boolean followsYou) {
        this.followsYou = followsYou;
    }

    public boolean isYouFollow() {
        return youFollow;
    }

    public void setYouFollow(boolean youFollow) {
        this.youFollow = youFollow;
    }

    public UserEntity getUser() {
        return user == null ? new UserEntity() : user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }


}
