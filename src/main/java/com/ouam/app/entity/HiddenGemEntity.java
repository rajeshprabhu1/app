package com.ouam.app.entity;

import java.io.Serializable;

public class HiddenGemEntity implements Serializable {

    public String getId() {
        return id  == null ? "" : id ;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category == null ? "" : category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHiddenGem() {
        return isHiddenGem;
    }

    public void setHiddenGem(boolean hiddenGem) {
        isHiddenGem = hiddenGem;
    }

    public String getPinnedAs() {
        return pinnedAs == null ? "" : pinnedAs;
    }

    public void setPinnedAs(String pinnedAs) {
        this.pinnedAs = pinnedAs;
    }

    public int getTbePinCount() {
        return tbePinCount;
    }

    public void setTbePinCount(int tbePinCount) {
        this.tbePinCount = tbePinCount;
    }

    public int getBtPinCount() {
        return btPinCount;
    }

    public void setBtPinCount(int btPinCount) {
        this.btPinCount = btPinCount;
    }

    public int getHgPinCount() {
        return hgPinCount;
    }

    public void setHgPinCount(int hgPinCount) {
        this.hgPinCount = hgPinCount;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBestPhoto() {
        return bestPhoto;
    }

    public void setBestPhoto(String bestPhoto) {
        this.bestPhoto = bestPhoto;
    }


    public CityEntity getCity() {
        return city;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }



    private String id;
    private String category;
    private String name;
    private boolean isHiddenGem;
    private String pinnedAs;
    private int tbePinCount;
    private int btPinCount;
    private int hgPinCount;
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String locality;
    private String country;
    private String bestPhoto;
    private double lat;
    private double lon;

    private CityEntity city;

    public String getProviderIDs() {
        return providerIDs == null ? "" : providerIDs;
    }

    public void setProviderIDs(String providerIDs) {
        this.providerIDs = providerIDs;
    }

    private String providerIDs;
//    private ProviderIdEntity providerIDs;
//    public ProviderIdEntity getProviderIDs() {
//        return providerIDs == null ? new ProviderIdEntity() : providerIDs;
//    }
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
