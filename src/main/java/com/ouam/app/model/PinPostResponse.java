package com.ouam.app.model;


import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.WithEntity;

public class PinPostResponse extends CommonResultEntity {

    private WithEntity with;

    public WithEntity getWithEntity() {
        return with;
    }

    public void setWithEntity(WithEntity withEntity) {
        this.with = withEntity;
    }
}
