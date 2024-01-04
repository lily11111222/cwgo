package com.example.cwgo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cwgo.activity.NewPostActivity;
import com.example.cwgo.fragment.AllPostFragment;
import com.example.cwgo.fragment.NewPostFragment;
import com.example.cwgo.ninegrid.GlideEngine;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button send_btn;
    NewPostFragment newPostFragment;
    AllPostFragment allPostFragment;
    androidx.fragment.app.FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            transaction = getSupportFragmentManager().beginTransaction();
        }
        findViewById(R.id.btn_begin1).setOnClickListener(v -> {
            if (newPostFragment == null) {
                newPostFragment = new NewPostFragment();
                transaction.replace(R.id.fragment_container, newPostFragment);
                transaction.commit();
            }
        });
        findViewById(R.id.btn_begin2).setOnClickListener(v -> {
            if (allPostFragment == null) {
                allPostFragment = new AllPostFragment();
                transaction.replace(R.id.fragment_container, allPostFragment);
                transaction.commit();
            }
        });
//        Fragment fragment;
//        fragment = new NewPostFragment();
//
//        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.about_fragment_container, fragment, "new_post");
//        transaction.commit();

    }
}