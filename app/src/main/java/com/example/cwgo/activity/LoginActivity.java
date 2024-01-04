package com.example.cwgo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cwgo.bean.LoginInput;
import com.example.cwgo.bean.LoginResponse;
import com.example.cwgo.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity{

    private static String TAG = "LoginActivity";
    private Button login_button;
    private Button btnRegister;
    private EditText email, password;
    public static String hostEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button=(Button)findViewById(R.id.login_button);
        btnRegister=(Button)findViewById(R.id.register_button);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password_str = password.getText().toString().trim();
                String email_str = email.getText().toString().trim();
                if(!TextUtils.isEmpty(email_str) && !TextUtils.isEmpty(password_str)){
                    if(isValidEmail(email_str)){

                        //没连服务器端没法运行这里……？

                        hostEmail = email.getText().toString();
                        LoginInput loginInput = new LoginInput(email_str,password_str);
                        String jsonstr = new Gson().toJson(loginInput);

                        RequestBody body = RequestBody.create(MediaType.parse("application/json"),jsonstr);
                        OkHttpClient client = new OkHttpClient();
                        //url需要填入
                        Request request = new Request.Builder().url("http://192.168.31.73:8000/auth/login").post(body).build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d(TAG, "fail");
                                Log.d(TAG, e.toString());
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Log.d(TAG,"res");
                                String back = response.body().string();

                                // 使用 Gson 库解析 JSON 数据
                                Gson gson = new Gson();
                                LoginResponse responce1 = gson.fromJson(back, LoginResponse.class);

                                if(responce1.getMsg().equals("Success")){
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    //为MainActivity传入用户邮箱
                                    intent.putExtra("STRING_KEY", hostEmail);
                                    startActivity(intent);
                                    //finish();
                                }else{
                                    Handler handler=new Handler(Looper.getMainLooper());
                                    handler.post(new Runnable(){
                                        public void run(){
                                            Toast.makeText(LoginActivity.this,"邮箱或密码错误，请重新输入！",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    } else
                        Toast.makeText(LoginActivity.this,"请填入正确邮箱！！",Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(LoginActivity.this,"邮箱和密码不能为空！！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isValidEmail(String email) {
        // 定义电子邮件的正则表达式模式
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        // 编译正则表达式模式
        Pattern pattern = Pattern.compile(emailPattern);
        // 创建 Matcher 对象
        Matcher matcher = pattern.matcher(email);
//        if(matcher.matches()){
//            Toast.makeText(LoginActivity.this,"邮箱正确呐！！",Toast.LENGTH_SHORT).show();
//        }
        // 进行匹配并返回匹配结果
        return matcher.matches();
    }
}
