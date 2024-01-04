package com.example.cwgo.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.cwgo.bean.User;

import java.util.ArrayList;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    public static final String CREATE_User = "create table user ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT, "
            + "password TEXT,"
            + "sex TEXT)";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, "db_test", null, 1);
        db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<User> getAllDATA() {

        return null;
    }

    public int verify(String name, String pass) {
        return 1;
    }

    public void add(String name, String pass) {

    }
}

