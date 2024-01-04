package com.example.cwgo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cwgo.bean.HostInfo;
import com.example.cwgo.bean.RegisterResponce;
import com.example.cwgo.R;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity1 extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "RegisterActivity1";
    private Button btnRegister, btnBack;
    private EditText etName;
    private EditText etPassword;
    private EditText etPassword1;
    private String code;
    private String hostEmail;
    private CheckBox check;


    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregister1);
        // 获取Intent对象
        Intent intent = getIntent();
        // 从Intent中获取传输的字符串数据，键值对的形式，这里的"STRING_KEY"是之前放入Intent中的键
        hostEmail = intent.getStringExtra("STRING_KEY");
        code = intent.getStringExtra("VCODE_KEY");


        etName = (EditText) findViewById(R.id.register_name);
        etPassword = (EditText) findViewById(R.id.register_password);
        etPassword1 = (EditText) findViewById(R.id.register_password_confirm);
        btnRegister = (Button) findViewById(R.id.register2_button);
        btnBack = (Button) findViewById(R.id.back_button);
        check = (CheckBox) findViewById(R.id.check_box);

        btnRegister.setOnClickListener(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity1.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (check.isChecked()) {
            String username_str = etName.getText().toString();
            //id_str = userid.getText().toString();
            //int userid_str = Integer.parseInt(userid.getText().toString());
            String userpassword_str = etPassword.getText().toString();
            String repassword_str = etPassword1.getText().toString();
            //String imagePath = AlbumUtil.savePhoto(photo, Environment.getExternalStorageDirectory().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));

            if (!TextUtils.isEmpty(username_str) && !TextUtils.isEmpty(username_str) && !TextUtils.isEmpty(repassword_str)) {
                if (userpassword_str.equals(repassword_str)) {
                    HostInfo hostInfo = new HostInfo("http://192.168.31.73:8000/user/download/1.jpg",code,hostEmail,userpassword_str,"Hello CWgo！",username_str);
                    String jsonstr = new Gson().toJson(hostInfo);
                    System.out.println(jsonstr);
                    //uploadPic(path);

                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonstr);
                    OkHttpClient client = new OkHttpClient();
                    //url需要填
                    Request request = new Request.Builder().url("http://192.168.31.73:8000/auth/register").post(body).build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "fail");
                            Log.d(TAG, e.toString());

                            Log.v("call", "fail");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            String back = response.body().string();

                            // 使用 Gson 库解析 JSON 数据
                            Gson gson = new Gson();
                            RegisterResponce responce1 = gson.fromJson(back, RegisterResponce.class);
                            if(responce1.getMsg().equals("Success")){

                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(RegisterActivity1.this, "注册成功，请重新登录！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Intent nextIntent = new Intent(RegisterActivity1.this, LoginActivity.class);
                                startActivity(nextIntent);
                                finish();
                            } else{
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(RegisterActivity1.this, "注册失败，请重试！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(RegisterActivity1.this, "两次密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else
                Toast.makeText(RegisterActivity1.this, "用户名和密码不可以为空，确认密码不可以为空！", Toast.LENGTH_SHORT).show();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(RegisterActivity1.this, "请勾选使用条款！", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
