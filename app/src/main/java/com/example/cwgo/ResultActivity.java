package com.example.cwgo;

import static com.example.cwgo.util.MapUtil.convertToLatLng;
import static com.example.cwgo.util.MapUtil.latLonListToStr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.example.cwgo.bean.MyPath;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private String TAG="ResultActivity";
    private MapView mapView;
    private List<LatLonPoint> receivedLatLonPointList; // 从 Intent 中获取的轨迹点列表
    private Polyline polyline; // 用于绘制轨迹的 Polyline
    private AMap aMap;

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private LocationSource.OnLocationChangedListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mapView=findViewById(R.id.map_view_result);
        mapView.onCreate(savedInstanceState);// 调用地图所必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        // 从 Intent 中获取轨迹点列表
        Intent intent = getIntent();
        if (intent != null) {
            MyPath receivedPath = intent.getParcelableExtra("path");
            receivedLatLonPointList = receivedPath.getPath();
            try {
                Log.d(TAG,"经纬度转json："+latLonListToStr(receivedLatLonPointList));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(convertToLatLng(receivedLatLonPointList.get(0)),20));

        // 绘制轨迹
        drawRoute();
    }



    private void drawRoute() {
        if (receivedLatLonPointList == null || receivedLatLonPointList.size() < 2) {
            // 至少需要两个轨迹点才能绘制轨迹
            return;
        }

        // 创建 PolylineOptions 对象，用于配置 Polyline 的属性
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(10f); // 设置线宽
        polylineOptions.color(Color.BLUE); // 设置线颜色

        // 遍历轨迹点列表，将每个点添加到 PolylineOptions 中
        for (LatLonPoint latLon : receivedLatLonPointList) {
            polylineOptions.add(new LatLng(latLon.getLatitude(), latLon.getLongitude()));
        }

        // 在地图上绘制 Polyline
        polyline = aMap.addPolyline(polylineOptions);
    }



    /**
     * 初始化定位
     */
    private void initAmapLocation() throws Exception {
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
//        //设置定位回调监听
//        mLocationClient.setLocationListener(mAMapLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        // 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，设备定位模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(2000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.startLocation();
        }
    }
    public LocationSource mLocationSource = new LocationSource() {
        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            mListener = onLocationChangedListener;
//            sportMyView = findViewById(R.id.map_view_citywalk);
//            sportMyView.setVisibility(View.INVISIBLE);
//            sportMyView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //初始化定位
//                    initAmapLocation();
//                }
//            });
//            sportMyView.setVisibility(View.VISIBLE);
            if (mLocationClient != null) {
                mLocationClient.startLocation();//启动定位
            }
        }

        @Override
        public void deactivate() {
            mListener = null;
            if (mLocationClient != null) {
                mLocationClient.stopLocation();
                mLocationClient.onDestroy();
            }
            mLocationClient = null;
        }
    };

    // 在 Activity 生命周期方法中管理 MapView 的生命周期
    @Override
    protected void onResume() {
        super.onResume();
        // 如果使用了 MapView，在这里调用 mapView.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果使用了 MapView，在这里调用 mapView.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 如果使用了 MapView，在这里调用 mapView.onDestroy();
        mapView.onDestroy();
    }
}