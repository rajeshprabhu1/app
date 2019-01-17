package com.ouam.app.entity;

import java.io.Serializable;
import java.util.ArrayList;


public class UserFollowingEntity implements Serializable {

    private ArrayList<WhoDetailEntity> users;

    public ArrayList<WhoDetailEntity> getUsers() {
        return users == null ? new ArrayList<WhoDetailEntity>() : users ;
    }

    public void setUsers(ArrayList<WhoDetailEntity> users) {
        this.users = users;
    }


}
