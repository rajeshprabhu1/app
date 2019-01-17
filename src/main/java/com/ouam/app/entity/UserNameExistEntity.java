package com.ouam.app.entity;

import java.io.Serializable;



public class UserNameExistEntity implements Serializable {
    private boolean exists;

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }
}
