package com.example.cwgo.bean;

public class Post {

    //private Integer postID;
    private String postTime;
    private Integer userID;
    private String text;
    private String title;
    private String road;

    private String avatar;
    private String user_name;


    private String postImage;
    private int hasPraised = 0;
    private int isCollected = 0;
    private int isPraised = 0;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public int getHasPraised() {
        return hasPraised;
    }

    public void setHasPraised(int hasPraised) {
        this.hasPraised = hasPraised;
    }

    public int getIsCollected() {
        return isCollected;
    }

    public void setIsCollected(int isCollected) {
        this.isCollected = isCollected;
    }

    public int getIsPraised() {
        return isPraised;
    }

    public void setIsPraised(int isPraised) {
        this.isPraised = isPraised;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getTime() {
        return postTime;
    }

    public void setTime(String time) {
        this.postTime = time;
    }

}
