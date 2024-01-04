package com.example.cwgo.bean;
import android.util.Log;

import java.net.URLDecoder;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Comment;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ReceiveInfo {


    private int NAME;
    List<Post> posts;
    List<Comment> com;
//    PraiseDetail pra;
    public List<Post> ReiceiveAllPost() {


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()//get
                .url("http://121.43.115.218:8000/post/selectAllPost")
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String json = response.body().string();
            Log.v("1234",json);
            Gson gson = new Gson();
            posts = gson.fromJson(json, new TypeToken<List<Post>>() {}.getType());
            for(Post post : posts)
                Log.v("bbb",post.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return posts;
    }

    public List<Post> ReiceiveCollect(int hostID) {

        NAME = hostID;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()//get
                .url("http://192.168.43.121:8080/mycollect?id="+ NAME)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String json = response.body().string();
            Log.v("1234",json);
            Gson gson = new Gson();
            posts = gson.fromJson(json, new TypeToken<List<Post>>() {}.getType());
            for(Post post : posts)
                Log.v("bbb1",post.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return posts;
    }

    public List<Comment> ReiceiveComment(int DynamicID) {

        NAME = DynamicID;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()//get
                .url("http://192.168.43.121:8080/comment?id="+ NAME)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String json = URLDecoder.decode(response.body().string(),"utf-8");
            Log.v("aaa",json);
            Gson gson = new Gson();
            com = gson.fromJson(json, new TypeToken<List<Comment>>() {}.getType());
/*            for(Dynamic dy1 : dy)
                Log.v("bbb",dy1.toString());*/
        }catch (Exception e){
            e.printStackTrace();
        }
        return com;
    }

    //看某个人的帖子
    public List<Post> ReiceiveOnesPost(int userName) {

        NAME = userName;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()//get
                .url("http://121.43.115.218:8000/post/selectPostByUser/userName"+ NAME)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String json = response.body().string();
            Log.v("1234",json);
            Gson gson = new Gson();
            posts = gson.fromJson(json, new TypeToken<List<Post>>() {}.getType());
            for(Post post : posts)
                Log.v("bbb1",post.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return posts;
    }


}

