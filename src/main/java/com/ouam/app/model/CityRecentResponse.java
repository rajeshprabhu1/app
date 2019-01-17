package com.ouam.app.model;

import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.WithEntity;

import java.util.ArrayList;

public class CityRecentResponse extends CommonResultEntity {
    ArrayList<WithEntity> with;

    public ArrayList<WithEntity> getWith() {
        return with == null ? new ArrayList<WithEntity>() : with;
    }

    public void setWith(ArrayList<WithEntity> with) {
        this.with = with;
    }
}
