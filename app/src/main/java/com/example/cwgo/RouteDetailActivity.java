package com.example.cwgo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.amap.api.services.route.WalkPath;
import com.example.cwgo.adapter.WalkSegmentListAdapter;
import com.example.cwgo.util.MapUtil;

public class RouteDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tvTitle,tvTime;
    private RecyclerView rv;//RecyclerView 是 Android 中用于显示大量数据集的高效列表视图组件。它取代了旧的 ListView 和 GridView，并提供更灵活、可定制的列表显示方式。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        //初始化
        initView();
    }

    private void initView(){
        toolbar=findViewById(R.id.toolbar);
        tvTitle=findViewById(R.id.tv_title);
        tvTime=findViewById(R.id.tv_time);
        rv=findViewById(R.id.rv);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            //按顶部回退
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent=getIntent();
        if(intent==null){
            return;
        }
        switch (intent.getIntExtra("type",0)){
            case 0://步行
                walkDetail(intent);
                break;
            case 1://骑行

                break;
            case 2://驾车

                break;
            case 3://公交

                break;
            default:
                break;
        }
    }

    /**
     * 步行详情
     * @param intent
     */
    private void walkDetail(Intent intent) {
        tvTitle.setText("步行路线规划");
        //都是总的路线长度（时间）
        WalkPath walkPath = intent.getParcelableExtra("path");//Parcelable 是一种 Android 特定的序列化机制，通常用于在组件之间传递复杂的自定义对象。
        String dur = MapUtil.getFriendlyTime((int) walkPath.getDuration());
        String dis = MapUtil.getFriendlyLength((int) walkPath.getDistance());
        tvTime.setText(dur + "(" + dis + ")");


        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new WalkSegmentListAdapter(R.layout.item_segment, walkPath.getSteps()));//获得每一步？
    }

}