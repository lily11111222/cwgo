package com.example.cwgo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.cwgo.R;
import com.example.cwgo.fragment.AllPostFragment;
import com.example.cwgo.fragment.CityWalkFragment;
import com.example.cwgo.fragment.HomeFragment;

public class NewWalkActivity extends AppCompatActivity {

//    FrameLayout fl;
    private FragmentManager fManager;
    //Fragment Object
    private CityWalkFragment walklF;
    private AllPostFragment channelF;
    private ImageButton im_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_walk);

//        fl = findViewById(R.id.fl);
        im_return = findViewById(R.id.im_return);

        walklF = new CityWalkFragment();
        fManager = getSupportFragmentManager();
        fManager.beginTransaction().replace(R.id.fl,walklF).commit();
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        im_return.setOnClickListener(v -> {
            fManager.popBackStack();
            finish();
        });
    }
}