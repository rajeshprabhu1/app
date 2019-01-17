package com.ouam.app.entity;


import java.io.Serializable;

public class ActivityEntity implements Serializable {
    private WithEntity place;
    private String activityType;
    private String subtype;
    private WhoDetailEntity user;
    private String entityType;
    private PostEntity post;
    private CityEntity city;
    private String message;
    private String type;
    private WhoDetailEntity likingUser;
    private WhoDetailEntity commentingUser;

    private int likeCount = 0;
    private int commentCount = 0;
    private boolean youLike;


    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isYouLike() {
        return youLike;
    }

    public void setYouLike(boolean youLike) {
        this.youLike = youLike;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public WhoDetailEntity getLikingUser() {
        return likingUser == null ? new WhoDetailEntity() : likingUser;
    }

    public void setLikingUser(WhoDetailEntity likingUser) {
        this.likingUser = likingUser;
    }

    public WhoDetailEntity getCommentingUser() {
        return commentingUser == null ? new WhoDetailEntity() : commentingUser;
    }

    public void setCommentingUser(WhoDetailEntity commentingUser) {
        this.commentingUser = commentingUser;
    }


    public CityEntity getCity() {
        return city;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }


    public WithEntity getPlace() {
        return place == null ? new WithEntity() : place;
    }

    public void setPlace(WithEntity place) {
        this.place = place;
    }

    public String getActivityType() {
        return activityType == null ? "" : activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getSubtype() {
        return subtype == null ? "" : subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public WhoDetailEntity getUser() {
        return user == null ? new WhoDetailEntity() : user;
    }

    public void setUser(WhoDetailEntity user) {
        this.user = user;
    }

    public String getEntityType() {
        return entityType == null ? "" : entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public PostEntity getPost() {
        return post == null ? new PostEntity() : post;
    }

    public void setPost(PostEntity post) {
        this.post = post;
    }


}
