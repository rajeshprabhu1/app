package com.ouam.app.entity;

import java.io.Serializable;

public class CommentInputEntity implements Serializable {
    private String comment;
    public String getComment() {
        return comment == null ? "" : comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
