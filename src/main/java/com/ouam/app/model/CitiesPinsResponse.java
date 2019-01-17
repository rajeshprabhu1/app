package com.ouam.app.model;


import com.ouam.app.entity.CitiesPinsEntitiy;
import com.ouam.app.entity.CommonResultEntity;

import java.io.Serializable;

public class CitiesPinsResponse extends CommonResultEntity implements Serializable {
    private CitiesPinsEntitiy with;

    public CitiesPinsEntitiy getWith() {
        return with == null ? new CitiesPinsEntitiy() : with;
    }

    public void setWith(CitiesPinsEntitiy with) {
        this.with = with;
    }


}
