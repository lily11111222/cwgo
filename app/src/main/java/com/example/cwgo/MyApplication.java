package com.example.cwgo;

import com.example.cwgo.bean.User;

public class MyApplication {
    private User user;
    private static MyApplication mApp;

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
        User user = new User();
        user.setUserID(1);
        return user;
    }
}
