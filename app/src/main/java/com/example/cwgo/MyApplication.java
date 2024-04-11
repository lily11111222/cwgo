package com.example.cwgo;

import android.app.Application;

import com.example.cwgo.bean.User;

public class MyApplication extends Application {
    private User user;
    private static MyApplication mApp;
    private String ip = "121.43.115.218";

    public static MyApplication getInstance() {
        if (mApp == null) {
            mApp = new MyApplication();
        }
        return mApp;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
