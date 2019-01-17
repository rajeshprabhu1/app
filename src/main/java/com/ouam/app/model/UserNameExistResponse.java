package com.ouam.app.model;

import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.UserNameExistEntity;

import java.io.Serializable;



public class UserNameExistResponse extends CommonResultEntity implements Serializable {
    private UserNameExistEntity with ;

    public UserNameExistEntity getWith() {
        return with == null ? new UserNameExistEntity() : with;
    }

    public void setWith(UserNameExistEntity with) {
        this.with = with;
    }
}
