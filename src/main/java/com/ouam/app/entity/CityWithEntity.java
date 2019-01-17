package com.ouam.app.entity;

import java.io.Serializable;

public class CityWithEntity extends CommonResultEntity implements Serializable {
    private String id;
    private String category;
    private String name;
    private String providerIDs;
    private boolean isHiddenGem;
    private String pinnedAs;
    private String tbePinCount;
    private String btPinCount;
    private String hgPinCount;
    private CityEntity city;

    public String getId() {
        return id == null ? "" : id;
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

    public String getProviderIDs() {
        return providerIDs == null ? "" : providerIDs;
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

    public CityEntity getCity() {
        return city == null ? new CityEntity() : city;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }


    public boolean isHiddenGem() {
        return isHiddenGem;
    }

    public void setHiddenGem(boolean hiddenGem) {
        isHiddenGem = hiddenGem;
    }
}
