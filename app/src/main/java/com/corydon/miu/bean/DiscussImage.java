package com.corydon.miu.bean;

import java.io.Serializable;

public class DiscussImage implements Serializable {
    private String discussId;
    private String imageUrl;

    public DiscussImage() {
    }

    public DiscussImage(String discussId, String imageUrl) {
        this.discussId = discussId;
        this.imageUrl = imageUrl;
    }

    public String getDiscussId() {
        return discussId;
    }

    public void setDiscussId(String discussId) {
        this.discussId = discussId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
