package com.ouam.app.entity;


import java.io.Serializable;

public class GeopointEntity implements Serializable {
    private String type;
    private Double[] coordinates;

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Double[] coordinates) {
        this.coordinates = coordinates;
    }
}
