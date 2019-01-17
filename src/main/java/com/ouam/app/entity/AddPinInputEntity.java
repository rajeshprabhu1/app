package com.ouam.app.entity;

import java.io.Serializable;


public class AddPinInputEntity implements Serializable {

    private String pinType;
    private String name;
    private double lat;
    private double lon;

    public String getPinType() {
        return pinType == null ? "" : pinType;
    }

    public void setPinType(String pinType) {
        this.pinType = pinType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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