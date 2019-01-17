package com.ouam.app.entity;

/**
 * Created by Smaat on 9/21/2017.
 */

public class LocationEntity {

    private String lat;
    private String lng;

    public String getLat() {
        if(lat == null){
            lat = "";
        }
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        if(lng == null){
            lng = "";
        }
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


}
