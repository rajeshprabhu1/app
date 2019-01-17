package com.ouam.app.model;


import com.ouam.app.entity.CitiesWithEntity;
import com.ouam.app.entity.CommonResultEntity;

import java.io.Serializable;

public class CitiesDetailsResponse extends CommonResultEntity implements Serializable{

    private CitiesWithEntity with;

    public CitiesWithEntity getWith() {
        return with == null ? new CitiesWithEntity() : with;
    }

    public void setWith(CitiesWithEntity with) {
        this.with = with;
    }


}
