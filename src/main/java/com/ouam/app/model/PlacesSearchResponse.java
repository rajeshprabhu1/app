package com.ouam.app.model;


import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.WithEntity;

import java.util.ArrayList;

public class PlacesSearchResponse extends CommonResultEntity {

    private ArrayList<WithEntity> with;


    public ArrayList<WithEntity> getWith() {
        return with;
    }

    public void setWith(ArrayList<WithEntity> with) {
        this.with = with;
    }
}
