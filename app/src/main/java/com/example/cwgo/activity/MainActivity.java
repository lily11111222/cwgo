package com.example.cwgo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cwgo.MyApplication;
import com.example.cwgo.bean.MyImageView;
import com.example.cwgo.bean.User;
import com.example.cwgo.bean.UserData;
import com.example.cwgo.fragment.HomeFragment;
import com.example.cwgo.fragment.AllPostFragment;
import com.example.cwgo.fragment.MyFragment;
import com.example.cwgo.fragment.MyFragment2;
import com.example.cwgo.fragment.MyFragment3;
import com.example.cwgo.R;
import com.example.cwgo.utils.MyselfUtil;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //UI Object
    private TextView txt_topbar;
    private TextView txt_walk;
    private TextView txt_channel;
    private TextView txt_postmessage;
    //private TextView txt_setting;
    private FrameLayout ly_content;//该页面Fragment的内容？

    //Fragment Object
    private HomeFragment walklF;
    private AllPostFragment channelF;
    private MyFragment3 announceF;
    //private MyFragment4 settingF;
    private FragmentManager fManager;

    //侧滑菜单栏
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    ImageView menu;

    private MyImageView user_image;
    private TextView user_name;
    private TextView user_sign;
    private User mydata;

    private String hostEmail;//这里用的是id
    private MyApplication mApp = MyApplication.getInstance();

    Handler handlerPra = new Handler(){
        @Override
        public void handleMessage(Message msg){
            mydata = (User) msg.obj;
            user_image.setImageURL(mydata.getAvatar());
            user_name.setText(mydata.getUserName());
            user_sign.setText(mydata.getSignature());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        // 从Intent中获取传输的字符串数据，键值对的形式，这里的"STRING_KEY"是之前放入Intent中的键
        hostEmail = intent.getStringExtra("STRING_KEY");
        mApp.getUser().setEmail(hostEmail);

        walklF = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.ly_content,walklF).commit();
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        fManager = getSupportFragmentManager();
        bindViews();
        txt_walk.performClick();

        initView();

    }

    //侧滑菜单
    private void initView(){
        drawerLayout = findViewById(R.id.activity_na);
        navigationView = findViewById(R.id.nav);
        menu = findViewById(R.id.iv_menu);

        View headerView = navigationView.getHeaderView(0);
        user_image = headerView.findViewById(R.id.iv_menu_user);
        user_name = headerView.findViewById(R.id.tv_menu_user);
        user_sign = headerView.findViewById(R.id.tv_menu_usersign);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    MyselfUtil rec = new MyselfUtil();
                    //hostID指的是什么？？
                    mydata = rec.httpGet(hostEmail);
                    mApp.setUser(mydata);
                    handlerPra.sendMessage(handlerPra.obtainMessage(22,mydata));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();



        //获取头布局
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击菜单，跳出侧滑菜单
                if (drawerLayout.isDrawerOpen(navigationView)){
                    drawerLayout.closeDrawer(navigationView);
                }else{
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            //菜单中我的收藏，我的喜欢，我的评论和我的动态
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_item4){
                    Intent intent4  = new Intent(MainActivity.this,MyPostActivity.class);
                    startActivity(intent4);
                } else if (item.getItemId() == R.id.menu_item5) {
                    Intent intent5 = new Intent(MainActivity.this, MySettingActivity.class);
                    startActivity(intent5);
                }
//                switch(item.getItemId()){
//                    case R.id.menu_item1:
//                        Intent intent  = new Intent(MainActivity.this,MyCollectActivity.class);
//                        startActivity(intent);
//                        break;
//                    case R.id.menu_item2:
//                        Intent intent2  = new Intent(MainActivity.this,MyLikeActivity.class);
//                        startActivity(intent2);
//                        break;
//                    case R.id.menu_item3:
//                        Intent intent3  = new Intent(MainActivity.this,CommentActivity.class);
//                        startActivity(intent3);
//                        break;
//                    case R.id.menu_item4:
//                        Intent intent4  = new Intent(MainActivity.this,MyMomentActivity.class);
//                        startActivity(intent4);
//                        break;
//
//                }
                return true;
            }

        });

    }


    private void bindViews() {
        txt_topbar = (TextView)findViewById(R.id.txt_topbar);
        txt_walk = (TextView)findViewById(R.id.txt_walk);
        txt_channel = (TextView)findViewById(R.id.txt_channel);
        txt_postmessage = (TextView)findViewById(R.id.txt_postmessage);
        //txt_setting = (TextView)findViewById(R.id.txt_setting);

        ly_content = (FrameLayout) findViewById(R.id.ly_content);

        txt_walk.setOnClickListener(this);
        txt_channel.setOnClickListener(this);
        txt_postmessage.setOnClickListener(this);
        //txt_setting.setOnClickListener(this);
    }

    private void setSelected(){
        txt_walk.setSelected(false);
        txt_channel.setSelected(false);
        txt_postmessage.setSelected(false);
        //txt_setting.setSelected(false);
    }


    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(walklF != null)fragmentTransaction.hide(walklF);
        if(channelF != null)fragmentTransaction.hide(channelF);
        if(announceF != null)fragmentTransaction.hide(announceF);
        //if(settingF != null)fragmentTransaction.hide(settingF);
    }


    @Override
    public void onClick(View view) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        if(view.getId() == R.id.txt_walk){
            setSelected();
            txt_topbar.setText(R.string.tab_menu_walk);
            txt_walk.setSelected(true);
            if(walklF == null){
                walklF = new HomeFragment();
                fTransaction.add(R.id.ly_content,walklF);
            }else{
                fTransaction.show(walklF);
            }
        } else if (view.getId() == R.id.txt_channel) {
            setSelected();
            txt_topbar.setText(R.string.tab_menu_normal);
            txt_channel.setSelected(true);
            if(channelF == null){
                channelF = new AllPostFragment();
                fTransaction.add(R.id.ly_content,channelF);
            }else{
                fTransaction.show(channelF);
            }
        } else if (view.getId() == R.id.txt_postmessage) {
            setSelected();
            txt_topbar.setText(R.string.tab_menu_message);
            txt_postmessage.setSelected(true);
            if(announceF == null){
                announceF = new MyFragment3();
                fTransaction.add(R.id.ly_content,announceF);
            }else{
                fTransaction.show(announceF);
            }
        }
        /*
        else if (view.getId() == R.id.txt_setting) {
            setSelected();
            txt_topbar.setText(R.string.tab_menu_setting);
            txt_setting.setSelected(true);
            if(settingF == null){
                settingF = new MyFragment4();
                fTransaction.add(R.id.ly_content,settingF);
            }else{
                fTransaction.show(settingF);
            }
        }
        */
        fTransaction.commit();

    }


}
