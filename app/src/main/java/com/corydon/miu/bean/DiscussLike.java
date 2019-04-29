package com.corydon.miu.bean;

public class DiscussLike {
    private String discussId;
    private String userMail;

    public DiscussLike(){

    }
    public DiscussLike(String discussId, String userMail) {
        this.discussId = discussId;
        this.userMail = userMail;
    }

    public String getDiscussId() {
        return discussId;
    }

    public void setDiscussId(String discussId) {
        this.discussId = discussId;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }
}
