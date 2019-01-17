package com.ouam.app.entity;

import java.io.Serializable;


public class WhoDetailEntity implements Serializable {

    private String id="";
    private String username="";
    private String photoUrl="";
    private String pinType="";
    private String photo="";
    private String provider="";
    private String providerId="";
    private HomeCityDetailsEntity homeCity=new HomeCityDetailsEntity();
    private String email="";
    private String name="";
    private String category="";
    //    private ProviderIdEntity providerIDs;
    private CityEntity city=new CityEntity();

    private String providerIDs="";


    private int level=0;


    private double lon=0;
    private double lat=0;

    private String pinnedAs="";
    private String tbePinCount="";
    private String btPinCount="";
    private String hgPinCount="";
    private String bestPhoto="";

    public String getProviderIDs() {
        return providerIDs ==  null ? "" : providerIDs;
    }

    public void setProviderIDs(String providerIDs) {
        this.providerIDs = providerIDs;
    }
    public String getPinnedAs() {
        return pinnedAs == null ? "" : pinnedAs;
    }

    public void setPinnedAs(String pinnedAs) {
        this.pinnedAs = pinnedAs;
    }

    public String getTbePinCount() {
        return tbePinCount == null ? "" : tbePinCount;
    }

    public void setTbePinCount(String tbePinCount) {
        this.tbePinCount = tbePinCount;
    }

    public String getBtPinCount() {
        return btPinCount == null ? "" : btPinCount;
    }

    public void setBtPinCount(String btPinCount) {
        this.btPinCount = btPinCount;
    }

    public String getHgPinCount() {
        return hgPinCount == null ? "" : hgPinCount;
    }

    public void setHgPinCount(String hgPinCount) {
        this.hgPinCount = hgPinCount;
    }

    public String getBestPhoto() {
        return bestPhoto == null ? "" : bestPhoto;
    }

    public void setBestPhoto(String bestPhoto) {
        this.bestPhoto = bestPhoto;
    }



    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }


    public CityEntity getCity() {
        return city == null ? new CityEntity() : city;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category == null ? "" : category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

//    public ProviderIdEntity getProviderIDs() {
//        return providerIDs == null ? new ProviderIdEntity() : providerIDs;
//    }

//    public void setProviderIDs(ProviderIdEntity providerIDs) {
//        this.providerIDs = providerIDs;
//    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPhoto() {
        return photo == null ? "" : photo;
    }

    public String getProvider() {
        return provider == null ? "" : provider;
    }

    public String getProviderId() {
        return providerId == null ? "" : providerId;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }


    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPinType() {
        return pinType == null ? "" : pinType;
    }

    public void setPinType(String pinType) {
        this.pinType = pinType;
    }

    public HomeCityDetailsEntity getHomeCity() {
        return homeCity == null ? new HomeCityDetailsEntity() : homeCity;
    }

    public void setHomeCity(HomeCityDetailsEntity homeCity) {
        this.homeCity = homeCity;
    }

    public String getId() {
        return id == null ? " " : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username == null ? " " : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoUrl() {
        return photoUrl == null ? " " : photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


}


