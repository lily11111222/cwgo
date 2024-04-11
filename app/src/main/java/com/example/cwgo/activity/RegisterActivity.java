package com.example.cwgo.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cwgo.MyApplication;
import com.example.cwgo.bean.MyUserResponse;
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

public class RegisterActivity extends AppCompatActivity {
    private static String TAG = "RegisterActivity";
    private Button btnRegister /*”下一步“*/, btnBack /*”返回“*/, btngetVcode /*获取验证码*/;
    private EditText etEmail, etVcode;
    public static String hostEmail;
    private String Vcode = null;

    //以下三个为了让获取验证码30s禁用
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private final long timerDuration = 30000; // 倒计时时长，单位为毫秒
    private MyApplication mApp = MyApplication.getInstance();


    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregister);
        etEmail = (EditText) findViewById(R.id.register_email);
        etVcode = (EditText) findViewById(R.id.register_vcode);
        btnRegister = (Button) findViewById(R.id.register1_button);
        btnBack = (Button) findViewById(R.id.back_button);
        btngetVcode = (Button) findViewById(R.id.register_gainVcode);


        btngetVcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etEmail_str = etEmail.getText().toString().trim();
                String etVcode_str = etVcode.getText().toString().trim();
                if (!TextUtils.isEmpty(etEmail_str)) {
                    if (isValidEmail(etEmail_str)) {
                        if (!isTimerRunning) {
                            startCountdown();

                            hostEmail = etEmail.getText().toString();
                            //HostInfo hostInfo = new HostInfo(hostEmail, null, null);
                            String jsonstr = new Gson().toJson(null);
                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonstr);
                            OkHttpClient client = new OkHttpClient();
                            //url需要填入
                            Request request = new Request.Builder().url("http://"+mApp.getIp()+":8000/auth/sendEmailCode?email=" + hostEmail).get().build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d(TAG, "fail");
                                    Log.d(TAG, e.toString());
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                                    Log.d(TAG, "res1");
                                    Log.d(TAG, String.valueOf(response.code()));

                                    String back = response.body().string();

                                    // 使用 Gson 库解析 JSON 数据
                                    Gson gson = new Gson();
                                    MyUserResponse responce1 = gson.fromJson(back, MyUserResponse.class);

                                    if(responce1.getMsg().equals("Success")){
                                        Vcode = responce1.getData().substring(13);
                                        System.out.println("Vcode = "+Vcode);
                                    }else {
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(RegisterActivity.this, "获取验证码出错或邮箱已注册，请稍后重试！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                }
                            });
                        }
                    } else
                        Toast.makeText(RegisterActivity.this, "邮箱格式错误！", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(RegisterActivity.this, "请输入邮箱！", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etEmail_str = etEmail.getText().toString().trim();
                String etVcode_str = etVcode.getText().toString().trim();
                if (!TextUtils.isEmpty(etEmail_str) && !TextUtils.isEmpty(etVcode_str)) {
                    if (isValidEmail(etEmail_str) && Vcode != null) {
                        if (Vcode.equals(etVcode_str)) {
                            // 创建一个Intent对象
                            Intent intent = new Intent(RegisterActivity.this, RegisterActivity1.class);
                            // 将hostEmail 数据放入Intent中，键值对的形式
                            intent.putExtra("STRING_KEY", hostEmail);
                            intent.putExtra("VCODE_KEY", Vcode); // 传递验证码 verificationCode
                            // 启动目标Activity
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "验证码错误！", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        Toast.makeText(RegisterActivity.this, "邮箱格式错误！", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(RegisterActivity.this, "邮箱和验证码不能为空！！", Toast.LENGTH_SHORT).show();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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

    public void startCountdown() {
        //Toast.makeText(RegisterActivity.this,"倒计时开始了！",Toast.LENGTH_SHORT).show();
        countDownTimer = new CountDownTimer(timerDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isTimerRunning = true;
                btngetVcode.setEnabled(false);
                btngetVcode.setText("重新获取（" + millisUntilFinished / 1000 + "秒）");
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                btngetVcode.setEnabled(true);
                btngetVcode.setText("获取验证码");
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 避免内存泄漏，确保在 Activity 销毁时停止倒计时
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

}
