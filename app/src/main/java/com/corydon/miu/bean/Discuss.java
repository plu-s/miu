package com.corydon.miu.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Discuss implements Serializable {
    private String id;
    private String authorMail;
    private String title;
    private String content;
    private Date createDate;
    private int likeCount;
    private int commentCount;
    private List<DiscussImage> discussImageList;
    public Discuss(){

    }

    public Discuss(String authorMail,String title, String content, Date createDate){
        this.authorMail = authorMail;
        this.title=title;
        this.content=content;
        this.createDate=createDate;
        this.likeCount=0;
    }

    public Discuss(String authorMail, String title, String content, Date createDate, int likeCount){
        this(authorMail, title, content, createDate);
        this.likeCount=likeCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorMail() {
        return authorMail;
    }

    public void setAuthorMail(String authorMail) {
        this.authorMail = authorMail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<DiscussImage> getDiscussImageList() {
        return discussImageList;
    }

    public void setDiscussImageList(List<DiscussImage> discussImageList) {
        this.discussImageList = discussImageList;
    }
}
