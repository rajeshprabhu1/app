package com.ouam.app.model;


import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.UserProfileEntity;

import java.io.Serializable;
import java.util.ArrayList;

public class RecommendationFollowResponse extends CommonResultEntity implements Serializable{

    private ArrayList<UserProfileEntity> with=new ArrayList<>();

    public ArrayList<UserProfileEntity> getWith() {
        return with;
    }

    public void setWith(ArrayList<UserProfileEntity> with) {
        this.with = with;
    }


}
