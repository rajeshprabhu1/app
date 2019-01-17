package com.ouam.app.model;


import com.ouam.app.entity.CityWithEntity;
import com.ouam.app.entity.CommonResultEntity;

import java.io.Serializable;
import java.util.ArrayList;

public class CitiesTrendingPlaceResponse extends CommonResultEntity implements Serializable {
    private ArrayList<CityWithEntity> with;

    public ArrayList<CityWithEntity> getWith() {
        return with == null ? new ArrayList<CityWithEntity>() : with;
    }

    public void setWith(ArrayList<CityWithEntity> with) {
        this.with = with;
    }


}
