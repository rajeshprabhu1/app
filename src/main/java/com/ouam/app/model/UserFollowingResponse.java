package com.ouam.app.model;


import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.WhoDetailEntity;

import java.io.Serializable;
import java.util.ArrayList;

public class UserFollowingResponse extends CommonResultEntity implements Serializable{

    public ArrayList<WhoDetailEntity> getWith() {
        return with == null ? new ArrayList<WhoDetailEntity>() : with;
    }

    public void setWith(ArrayList<WhoDetailEntity> with) {
        this.with = with;
    }

    private ArrayList<WhoDetailEntity> with;




}
