package com.ouam.app.model;


import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.UserProfileEntity;

import java.io.Serializable;

public class UserProfileResponse extends CommonResultEntity implements Serializable{

    private UserProfileEntity with;
    public UserProfileEntity getWith() {
        return with == null ? new UserProfileEntity() : with;
    }

    public void setWith(UserProfileEntity with) {
        this.with = with;
    }


}
