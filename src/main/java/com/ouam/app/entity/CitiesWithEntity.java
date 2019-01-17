package com.ouam.app.entity;

import java.io.Serializable;

public class CitiesWithEntity implements Serializable {


    private String id;
    private String name;
    private String photoUrl;
    private boolean isHiddenGem;
    private String pinnedAs;
    private int tbePinCount;
    private int btPinCount;
    private int hgPinCount;
    private CityEntity city;
    private double locationLat;
    private double locationLon;
    private String address;
    private String category;
    private ProviderIdEntity providerIDs;
    private String yourPin;
    private boolean isChecked = false;
    private String postalCode;
    private String website;
    private String phoneNumber;
    private String sourceUrl;
    private String pinType;
    private String chatChannelID;
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String user_id;
    private String user_name;
    private String username;
    private String locality;
    private String country;

    public PhotoEntity getPhoto() {
        return photo == null ? new PhotoEntity() : photo;
    }

    public void setPhoto(PhotoEntity photo) {
        this.photo = photo;
    }

    private PhotoEntity photo;
    private double lon;

    public String getUsername() {
        return username;
    }

    private double lat;
    private String[] images;
    private String[] thumbnails;
    private String comment;
    private String created;
    private UserEntity user;
    private String bestPhoto;


    public String getBestPhoto() {
        return bestPhoto == null ? "" : bestPhoto;
    }

    public void setBestPhoto(String bestPhoto) {
        this.bestPhoto = bestPhoto;
    }





    public String[] getImages() {
        return images;
    }


    public String[] getThumbnails() {
        return thumbnails;
    }


    public String getComment() {
        return comment;
    }


    public String getCreated() {
        return created == null?"":created;
    }


    public double getLon() {
        return lon;
    }


    public double getLat() {
        return lat;
    }


    public String getLocality() {
        return locality == null ? "" : locality;
    }


    public String getCountry() {
        return country == null ? "" : country;
    }


    public String getAccess_token() {
        return access_token == null ? "" : access_token;
    }


    public String getExpires_in() {
        return expires_in == null ? "" : expires_in;
    }


    public String getRefresh_token() {
        return refresh_token == null ? "" : refresh_token;
    }


    public String getUser_id() {
        return user_id == null ? "" : user_id;
    }

    public UserEntity getUser() {
        return user == null ? new UserEntity() : user;
    }

    public String getChatChannelID() {
        return chatChannelID == null ? "" : chatChannelID;
    }

    public String getPinType() {
        return pinType == null ? "" : pinType;
    }


    public String getPostalCode() {
        return postalCode == null ? "" : postalCode;
    }

    public String getWebsite() {
        return website == null ? "" : website;
    }

    public ProviderIdEntity getProviderIDs() {
        return providerIDs == null ? new ProviderIdEntity() : providerIDs;
    }
    public String getPhoneNumber() {
        return phoneNumber == null ? "" : phoneNumber;
    }


    public String getSourceUrl() {
        return sourceUrl == null ? "" : sourceUrl;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getYourPin() {
        return yourPin == null ? "" : yourPin;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setPinType(String pinType) {
        this.pinType = pinType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public String getPhotoUrl() {
        return photoUrl == null ? "" : photoUrl;
    }

    public boolean isHiddenGem() {
        return isHiddenGem;
    }


    public String getPinnedAs() {
        return pinnedAs == null ? " " : pinnedAs;
    }


    public int getTbePinCount() {
        return tbePinCount;
    }


    public int getBtPinCount() {
        return btPinCount;
    }


    public int getHgPinCount() {
        return hgPinCount;
    }


    public String getCategory() {
        return category == null ? "" : category;
    }


    public CityEntity getCity() {
        return city == null ? new CityEntity() : city;
    }


    public double getLocationLat() {
        return locationLat;
    }


    public double getLocationLon() {
        return locationLon;
    }


    public String getAddress() {
        return address == null ? "" : address;
    }

    public String getUser_name() {
        return user_name == null ?"" : user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}