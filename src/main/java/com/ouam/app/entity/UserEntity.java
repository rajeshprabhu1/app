package com.ouam.app.entity;


import java.io.Serializable;

public class UserEntity implements Serializable {

    private WhoDetailEntity compact=new WhoDetailEntity();
    private String createdOn="";
    private String email="";
    private String platformID="";
    private String platform="";
    private int userStatus=0;
    private String id="";
    private String photo="";
    private String provider="";
    private String providerId="";
    private String username="";
    private String created="";
    private int level=0;
    private CityEntity city=new CityEntity();

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }



    public CityEntity getCity() {
        return city == null ? new CityEntity() : city;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }

    public String getCreated() {
        return created == null ? "" : created;
    }

    public void setCreated(String created) {
        this.created = created;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo == null?"":photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getUsername() {
        return username == null ?"":username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public WhoDetailEntity getCompact() {
        return compact == null ? new WhoDetailEntity() : compact;
    }

    public void setCompact(WhoDetailEntity compact) {
        this.compact = compact;
    }

    public String getCreatedOn() {
        return createdOn == null ? "" : createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlatformID() {
        return platformID == null ? "" : platformID;
    }

    public void setPlatformID(String platformID) {
        this.platformID = platformID;
    }

    public String getPlatform() {
        return platform == null ? "" : platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }


}
