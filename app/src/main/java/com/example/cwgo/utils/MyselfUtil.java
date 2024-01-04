package com.example.cwgo.utils;

import android.util.Log;

import com.example.cwgo.bean.ContentInfo;
import com.example.cwgo.bean.User;
import com.example.cwgo.bean.UserData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyselfUtil {
    private String Email;
    private User frid;
    public User httpGet(String hostEmail){
        Email = hostEmail;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                //需要填入url
                .url("http://192.168.31.73:8000/user/findUserByEmail?email="+Email)
                .build();
        Call call = client.newCall(request);

        try{
            Response response = call.execute();
            String json = "";
            if (response.isSuccessful()){
                json = response.body().string();
            }

            Gson gson = new Gson();
            Log.v("aaa",json);
            frid = gson.fromJson(json,new TypeToken<User>(){}.getType());
        }catch (Exception e){
            e.printStackTrace();
        }
        return frid;
    }
}
