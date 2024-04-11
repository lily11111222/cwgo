package com.example.cwgo.bean;

public class MyResponse {
    private String code;
    private UserData data;
    private String msg;

    public String getCode() { return code; }
    public void setCode(String value) { this.code = value; }

    public UserData getData() { return data; }
    public void setData(UserData value) { this.data = value; }

    public String getMsg() { return msg; }
    public void setMsg(String value) { this.msg = value; }
}
