package com.ouam.app.model;


import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.PlacePinsEntity;

import java.io.Serializable;

public class PlacePinsResponse extends CommonResultEntity implements Serializable {
    public PlacePinsEntity with;

    public PlacePinsEntity getWith() {
        return with == null ? new PlacePinsEntity() : with;
    }

    public void setWith(PlacePinsEntity with) {
        this.with = with;
    }


}
