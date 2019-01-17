package com.ouam.app.entity;

import java.io.Serializable;

public class ImageUploadResultEntity implements Serializable{

    private String status;
    private String by;
    private String the;

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBy() {
        return by == null ? "" : by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getThe() {
        return the == null ? "" : the;
    }

    public void setThe(String the) {
        this.the = the;
    }
}
