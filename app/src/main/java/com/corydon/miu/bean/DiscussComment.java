package com.corydon.miu.bean;

import java.io.Serializable;
import java.util.Date;

public class DiscussComment implements Serializable {
    private String discussId;
    private String userMail;
    private String content;
    private Date createDate;
    private String userName;
    private String userPic;

    public DiscussComment() {
    }

    public DiscussComment(String discussId, String userMail, String content, Date createDate) {
        this.discussId = discussId;
        this.userMail = userMail;
        this.content = content;
        this.createDate = createDate;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }
}
