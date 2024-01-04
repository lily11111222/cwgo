package com.example.cwgo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cwgo.R;


public class MyMomentActivity extends AppCompatActivity {

    private ImageView back,logo;
    private EditText username, userid, userpassword, email;
    private Button regist;
    protected static Uri temUri;
    private Bitmap photo;
    private String id_str;
    private CheckBox check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_mysetting);
        logo = (ImageView) findViewById(R.id.r_logo);
        username =(EditText) findViewById(R.id.setting_username);
        userid =(EditText) findViewById(R.id.setting_id);
        userpassword =(EditText) findViewById(R.id.setting_password);
        email =(EditText) findViewById(R.id.setting_email);
        regist =(Button) findViewById(R.id.regist);
        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent nextIntent = new Intent(MyMomentActivity.this, MainActivity.class);
                startActivity(nextIntent);
                finish();
            }
        });

        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


}
