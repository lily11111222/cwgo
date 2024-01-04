package com.example.cwgo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cwgo.R;
import com.example.cwgo.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_body,new HomeFragment()).commit();
    }
}