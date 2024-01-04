package com.example.cwgo.bean;

public class User {
    private Integer userID;
    private String userName;
    private String password;
    private String email;
    private String signature;
    private String avatar;
    private String code;

    public Integer getUserID() {
        return userID;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCode() {
        return code;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getSignature() {
        return signature;
    }

    public String getUserName() {
        return userName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
