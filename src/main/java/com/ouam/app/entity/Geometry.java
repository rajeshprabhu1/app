package com.ouam.app.entity;

import java.io.Serializable;

/**
 * Created by Smaat on 9/21/2017.
 */

public class Geometry implements Serializable{
    private LocationEntity location;

    public LocationEntity getLocation() {
        if(location == null){
            location = new LocationEntity();
        }
        return location;
    }

    public void setLocation(LocationEntity location) {
        this.location = location;
    }
}
