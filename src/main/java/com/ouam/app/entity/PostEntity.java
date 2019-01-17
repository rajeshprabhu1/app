package com.ouam.app.entity;


import java.io.Serializable;

public class PostEntity implements Serializable {

    private String[] images;
    private String comment="";
    private String id="";
    private WithEntity place=new WithEntity();
    private String[] thumbnails;
    private WhoDetailEntity user=new WhoDetailEntity();
    private int likeCount=0;
    private int commentCount=0;
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

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getComment() {
        return comment == null ? "" : comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WithEntity getPlace() {
        return place == null ? new WithEntity() : place;
    }

    public void setPlace(WithEntity place) {
        this.place = place;
    }

    public String[] getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String[] thumbnails) {
        this.thumbnails = thumbnails;
    }

    public WhoDetailEntity getUser() {
        return user == null ? new WhoDetailEntity() : user;
    }

    public void setUser(WhoDetailEntity user) {
        this.user = user;
    }


}
