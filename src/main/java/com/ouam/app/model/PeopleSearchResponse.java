package com.ouam.app.model;


import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.PeopleSearchEntity;

import java.io.Serializable;
import java.util.ArrayList;

public class PeopleSearchResponse extends CommonResultEntity implements Serializable {
    public ArrayList<PeopleSearchEntity> with;

    public ArrayList<PeopleSearchEntity> getWith() {
        return with == null ? new ArrayList<PeopleSearchEntity>() : with;
    }

    public void setWith(ArrayList<PeopleSearchEntity> with) {
        this.with = with;
    }


}
