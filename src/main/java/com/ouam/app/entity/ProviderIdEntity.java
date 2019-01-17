package com.ouam.app.entity;


import java.io.Serializable;

public class ProviderIdEntity implements Serializable {
    private String fs;

    public String getFs() {
        return fs == null ? "" : fs;
    }

    public void setFs(String fs) {
        this.fs = fs;
    }
}
