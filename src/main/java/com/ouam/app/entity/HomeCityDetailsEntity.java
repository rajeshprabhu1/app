package com.ouam.app.entity;


import java.io.Serializable;

public class HomeCityDetailsEntity implements Serializable {

    private String name;
    private String locality;
    private String country;
    private String locationLat;
    private String locationLon;


    public String getName() {
        return name == null ? " " : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocality() {
        return locality == null ? " " : locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountry() {
        return country == null ? " " : country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocationLat() {
        return locationLat == null ? " " : locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLon() {
        return locationLon == null ? " " : locationLon;
    }

    public void setLocationLon(String locationLon) {
        this.locationLon = locationLon;
    }


}
