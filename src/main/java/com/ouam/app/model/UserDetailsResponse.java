package com.ouam.app.model;


import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.WhoDetailEntity;

import java.io.Serializable;

public class UserDetailsResponse extends CommonResultEntity implements Serializable{

    public WhoDetailEntity with;

    public WhoDetailEntity getWith() {
        return with == null ? new WhoDetailEntity() : with;
    }

    public void setWith(WhoDetailEntity with) {
        this.with = with;
    }

}
