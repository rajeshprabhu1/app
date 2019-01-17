package com.ouam.app.model;


import com.ouam.app.entity.AddressEntity;

import java.io.Serializable;
import java.util.ArrayList;


public class AddressResponse implements Serializable {

    private ArrayList<AddressEntity> results;

    public ArrayList<AddressEntity> getResults() {
        return results == null ? new ArrayList<AddressEntity>() : results;
    }

    public void setResults(ArrayList<AddressEntity> results) {
        this.results = results;
    }

}
