package com.ouam.app.model;

import com.ouam.app.entity.ActivityFeedEntity;
import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.HiddenGemEntity;

import java.io.Serializable;
import java.util.ArrayList;


public class HiddenGemResponse extends CommonResultEntity implements Serializable {
    public ArrayList<HiddenGemEntity> with;

    public ArrayList<HiddenGemEntity> getWith() {
        return with == null ? new ArrayList<HiddenGemEntity>() : with;
    }

    public void setWith(ArrayList<HiddenGemEntity> with) {
        this.with = with;
    }
}
