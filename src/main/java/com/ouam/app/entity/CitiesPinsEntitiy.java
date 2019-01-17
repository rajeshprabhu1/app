package com.ouam.app.entity;


import java.io.Serializable;
import java.util.ArrayList;

public class CitiesPinsEntitiy implements Serializable{

    private ArrayList<WhoDetailEntity> beenThere;
    private ArrayList<WhoDetailEntity> toBeExplored;
    private ArrayList<WhoDetailEntity> hiddenGems;

    public ArrayList<WhoDetailEntity> getBeenThere() {
        return beenThere == null ? new ArrayList<WhoDetailEntity>() : beenThere;
    }

    public void setBeenThere(ArrayList<WhoDetailEntity> beenThere) {
        this.beenThere = beenThere;
    }

    public ArrayList<WhoDetailEntity> getToBeExplored() {
        return toBeExplored == null ? new ArrayList<WhoDetailEntity>() : toBeExplored;
    }

    public void setToBeExplored(ArrayList<WhoDetailEntity> toBeExplored) {
        this.toBeExplored = toBeExplored;
    }

    public ArrayList<WhoDetailEntity> getHiddenGems() {
        return hiddenGems == null ? new ArrayList<WhoDetailEntity>() : hiddenGems;
    }

    public void setHiddenGems(ArrayList<WhoDetailEntity> hiddenGems) {
        this.hiddenGems = hiddenGems;
    }






}
