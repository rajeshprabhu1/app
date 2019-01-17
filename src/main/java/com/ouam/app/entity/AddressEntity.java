package com.ouam.app.entity;

import java.io.Serializable;


public class AddressEntity implements Serializable {

    private String formatted_address;
    private Geometry geometry;

    public String getFormatted_address() {
        return formatted_address==null?"":formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
