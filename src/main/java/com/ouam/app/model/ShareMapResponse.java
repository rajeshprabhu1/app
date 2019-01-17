package com.ouam.app.model;

import com.ouam.app.entity.CommonResultEntity;

public class ShareMapResponse extends CommonResultEntity {
    private String with;

    public String getWith() {
        return with == null ? "" : with;
    }

    public void setWith(String with) {
        this.with = with;
    }

}
