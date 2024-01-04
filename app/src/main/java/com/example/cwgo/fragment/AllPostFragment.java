package com.example.cwgo.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.cwgo.R;
import com.example.cwgo.adapter.AllPostAdapter;
import com.example.cwgo.bean.Post;
import com.example.cwgo.bean.ReceiveInfo;

import java.util.List;

public class AllPostFragment extends Fragment {
    private static String TAG = "AllPostFragment";

    public AllPostFragment() {
        // Required empty public constructor
    }

    ListView lv_view;
    List<Post> list_item;
    AllPostAdapter adapter = new AllPostAdapter();
    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, msg.obj.toString());
            adapter.setDatas((List<Post>)msg.obj);
            adapter.setInflater(getActivity());
            lv_view.setAdapter(adapter);

            adapter.setOnItemCollectListener(i -> {
//                PraiseOrCollectMsg msg1 = new PraiseOrCollectMsg();
//                msg1.setDynamicID(list_item.get(i).getDynamicID());
//                msg1.setHostID(1);
//                String jsonstr = new Gson().toJson(msg1);
//                RequestBody body = RequestBody.create(MediaType.parse("application/json"),jsonstr);
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder()
//                        .url("http://192.168.43.121:8080/findcollect")
//                        .post(body)
//                        .build();
//                client.newCall(request).enqueue(new okhttp3.Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        String json = response.body().string();
//                        Log.v("accepet",json);
//                        list_item.get(i).setIsCollected(Integer.parseInt(json));
//                        //adapter.notifyDataSetChanged();
//                        Message message = new Message();
//                        message.what = 0;
//                        handlerPraAndCom.sendMessage(message);
//                    }
//                });

            });
            adapter.setOnItemCommentClickListener(i -> {
//                @Override
//                public void onCommentClick(int i) {
//                    System.out.println("");
//                    comment(i);
//                }
            });

            adapter.setOnItemPraiseClickListener(i -> {
//                @Override
//                public void onPraiseClick(final int i) {
//                    PraiseOrCollectMsg msg = new PraiseOrCollectMsg();
//                    msg.setDynamicID(list_item.get(i).getDynamicID());
//                    msg.setHostID(1);
//                    String jsonstr = new Gson().toJson(msg);
//                    RequestBody body = RequestBody.create(MediaType.parse("application/json"),jsonstr);
//                    OkHttpClient client = new OkHttpClient();
//                    Request request = new Request.Builder()
//                            .url("http://192.168.43.121:8080/findpraise")
//                            .post(body)
//                            .build();
//                    client.newCall(request).enqueue(new okhttp3.Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            String json = response.body().string();
//                            Gson gson = new Gson();
//                            PraiseDetail pra = gson.fromJson(json,PraiseDetail.class);
//                            list_item.get(i).setHasPraised(pra.haspriased);
//                            list_item.get(i).setIsPraised(pra.isprased);
//                            //adapter.notifyDataSetChanged();
//                            Message message = new Message();
//                            message.what = 0;
//                            handlerPraAndCom.sendMessage(message);
//                        }
//                    });
//
//                }
            });
        }
    };

    Handler handlerPraAndCom = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0){
                adapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_post,container,false);
        lv_view = view.findViewById(R.id.lv_allpost);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ReceiveInfo rec = new ReceiveInfo();//Log.v("1234","1234");
                    list_item = rec.ReiceiveAllPost();
                    //Log.v("getinfo",list_item.get(0).toString());
                    if(!list_item.isEmpty())
                        handler.sendMessage(handler.obtainMessage(22,list_item));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        return view;
    }

//    private void comment(int i){
//        Post dy = list_item.get(i);
//        Intent commentDetail = new Intent(getActivity(), CommentDetail.class);
//        commentDetail.putExtra("mDynamic",dy);
//        startActivity(commentDetail);
//    }
}