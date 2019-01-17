package com.ouam.app.entity;


import java.io.Serializable;

public class PeopleSearchEntity implements Serializable {

    private WhoDetailEntity user;
    private boolean following;
    private boolean followsMe;
    private boolean youFollow;
    private boolean followsYou;



    public boolean isYouFollow() {
        return youFollow;
    }

    public void setYouFollow(boolean youFollow) {
        this.youFollow = youFollow;
    }

    public boolean isFollowsYou() {
        return followsYou;
    }

    public void setFollowsYou(boolean followsYou) {
        this.followsYou = followsYou;
    }


    public WhoDetailEntity getUser() {
        return user == null ? new WhoDetailEntity() : user;
    }

    public void setUser(WhoDetailEntity user) {
        this.user = user;
    }




    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isFollowsMe() {
        return followsMe;
    }

    public void setFollowsMe(boolean followsMe) {
        this.followsMe = followsMe;
    }


}
