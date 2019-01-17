package com.ouam.app.model;


import com.ouam.app.entity.ActivityFeedEntity;
import com.ouam.app.entity.CommonResultEntity;

import java.io.Serializable;
import java.util.ArrayList;

public class ActivityFeedResponse extends CommonResultEntity implements Serializable {
    public ArrayList<ActivityFeedEntity> with;

    public ArrayList<ActivityFeedEntity> getWith() {
        return with == null ? new ArrayList<ActivityFeedEntity>() : with;
    }

    public void setWith(ArrayList<ActivityFeedEntity> with) {
        this.with = with;
    }
}
