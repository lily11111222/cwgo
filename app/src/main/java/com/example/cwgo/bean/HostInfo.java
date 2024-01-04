package com.example.cwgo.bean;

public class HostInfo {
    private String avatar="";
    private String code;
    private String email;
    private String password;
    private String signature;
    private String userName;

    public HostInfo(String avatar,String code,String email,String password,String signature,String userName){
        this.avatar = avatar;
        this.code = code;
        this.email = email;
        this.signature = signature;
        this.password = password;
        this.userName = userName;
    }

    public String getAvatar() { return avatar; }
    public void setAvatar(String value) { this.avatar = value; }

    public String getCode() { return code; }
    public void setCode(String value) { this.code = value; }

    public String getEmail() { return email; }
    public void setEmail(String value) { this.email = value; }

    public String getPassword() { return password; }
    public void setPassword(String value) { this.password = value; }

    public String getSignature() { return signature; }
    public void setSignature(String value) { this.signature = value; }


    public String getUserName() { return userName; }
    public void setUserName(String value) { this.userName = value; }


}
