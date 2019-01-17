package com.ouam.app.model;


import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.WithEntity;

import java.io.Serializable;

public class LoginResponse extends CommonResultEntity implements Serializable {

    private WithEntity with;

    public WithEntity getWith() {
        return with == null ? new WithEntity() : with;
    }

    public void setWith(WithEntity with) {
        this.with = with;
    }
}
