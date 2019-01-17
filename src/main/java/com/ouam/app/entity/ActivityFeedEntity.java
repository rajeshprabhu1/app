package com.ouam.app.entity;


import java.io.Serializable;

public class ActivityFeedEntity implements Serializable {

    private String timestamp;
    private String actionType;
    private String actionValue;
    private String entityType;
    private String entityID;
    private String entityName;
    private WhoDetailEntity who;
    private String message;
    private ActivityEntity activity;
    private long created;
    private GeopointEntity geopoint;


    public ActivityEntity getActivity() {
        return activity == null ? new ActivityEntity() : activity;
    }

    public void setActivity(ActivityEntity activity) {
        this.activity = activity;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public GeopointEntity getGeopoint() {
        return geopoint == null ? new GeopointEntity() : geopoint;
    }

    public void setGeopoint(GeopointEntity geopoint) {
        this.geopoint = geopoint;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public WhoDetailEntity getWho() {
        return who == null ? new WhoDetailEntity() : who;
    }

    public void setWho(WhoDetailEntity who) {
        this.who = who;
    }

    public String getTimestamp() {
        return timestamp == null ? " " : timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getActionType() {
        return actionType == null ? " " : actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionValue() {
        return actionValue == null ? " " : actionValue;
    }

    public void setActionValue(String actionValue) {
        this.actionValue = actionValue;
    }

    public String getEntityType() {
        return entityType == null ? " " : entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityID() {
        return entityID == null ? " " : entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public String getEntityName() {
        return entityName == null ? " " : entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


}
