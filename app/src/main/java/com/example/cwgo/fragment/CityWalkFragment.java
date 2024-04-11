package com.example.cwgo.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.example.cwgo.CityWalkActivity;
import com.example.cwgo.R;
import com.example.cwgo.bean.MyPath;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CityWalkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CityWalkFragment extends Fragment {
    private View view;
    private static final String TAG = "CityWalkFragment";
    private MapView mapView;
    private AMap aMap;
    private TextView offlineMap, record;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocation privLocation;
    //    private SportMyView sportMyView;
    private double distance;
    private double totalDistance;
    List<LatLonPoint> points = new ArrayList<LatLonPoint>();
    private int i=0;
    private Button stopBtn;
    private AMapLocation startLocation;
    private AMapLocation endLocation;

    private SimpleDateFormat startTime;
    private SimpleDateFormat endTime;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CityWalkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CityWalkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CityWalkFragment newInstance(String param1, String param2) {
        CityWalkFragment fragment = new CityWalkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_city_walk, container, false);
        super.onCreate(savedInstanceState);
//        //初始化DBFlow
//        FlowManager.init(new FlowConfig.Builder(this).build());
//        FlowManager.init(this);
        mapView = view.findViewById(R.id.map_view_citywalk);
        mapView.onCreate(savedInstanceState);// 调用地图所必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        stopBtn=view.findViewById(R.id.stop);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationClient.stopLocation();
//                Intent intent=new Intent(CityWalkActivity.this,ResultActivity.class);
//                Intent intent=new Intent(getActivity(), NewPostFragment.class);
//                intent.putExtra("path", new MyPath(points));
//                startActivity(intent);
                getActivity().getSupportFragmentManager().beginTransaction().hide(CityWalkFragment.this);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fl,NewPostFragment.newInstance(new MyPath(points))).commit();
            }
        });
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        aMap.setMapTextZIndex(2);
        setUpMap();
        try {
            initAmapLocation();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    private void setUpMap() {
        /**
         * 设置一些amap的属性
         */
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setCompassEnabled(true);// 设置指南针是否显示
        uiSettings.setZoomControlsEnabled(true);// 设置缩放按钮是否显示
        uiSettings.setScaleControlsEnabled(true);// 设置比例尺是否显示
        uiSettings.setRotateGesturesEnabled(true);// 设置地图旋转是否可用
        uiSettings.setTiltGesturesEnabled(true);// 设置地图倾斜是否可用
        uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

        /** 自定义系统定位小蓝点
         *
         */
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(mLocationSource);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false//开启室内地图
        aMap.showIndoorMap(true);
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

    /**
     * 初始化定位
     */
    private void initAmapLocation() throws Exception {
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity());
        //设置定位回调监听
        mLocationClient.setLocationListener(mAMapLocationListener);
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

    /**
     * 定位回调每1秒调用一次
     */
    public AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {//定位成功
                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点,不写这一句无法显示到当前位置
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    Log.e(TAG, "获取经纬度集合" + privLocation);//打Log记录点是否正确

                    amapLocation.getAccuracy();//获取精度信息
                    amapLocation.getBearing();//获取方向角信息
                    amapLocation.getSpeed();//获取速度信息  单位：米/秒
                    amapLocation.getLocationType();//查看是什么类型的点
                    Log.e(TAG, "获取点的类型" + amapLocation.getLocationType());
                    if (amapLocation.getLocationType() == 1) {//表示结果来源于网络定位

//                        Location location = new Location();
                        double latitude = amapLocation.getLatitude();
                        double longitude = amapLocation.getLongitude();
                        points.add(i,new LatLonPoint(latitude,longitude));
                        i+=1;
                        Log.d(TAG, "这是经纬度latlon"+String.valueOf(new LatLonPoint(latitude,longitude)));
                        Log.d(TAG,String.valueOf(points));
                        drawLines(amapLocation);//一边定位一边连线
                        totalDistance += distance;
                        Toast.makeText(getActivity(), "已经走了"+totalDistance+"M",Toast.LENGTH_SHORT).show();

                        Log.e("DDDDDDDDD", String.valueOf(distance));
                        Log.e(TAG, "获取点的类型" + amapLocation.getLocationType());
                        Log.e("LLLLL", String.valueOf(latitude));
                        Log.e("LLLLLLLL", String.valueOf(longitude));
                    }
                    //获取定位时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    sdf.format(date);
                    if(privLocation==null){
                        startTime=sdf;
                    }else{
                        endTime=sdf;
                    }
                    Log.d(TAG, String.valueOf(sdf));
                    privLocation = amapLocation;
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
                }
            }
        }
    };

    /**
     * 绘制运动路线
     *
     * @param curLocation
     */
    public void drawLines(AMapLocation curLocation) {

        if (null == privLocation) {
            return;
        }
        PolylineOptions options = new PolylineOptions();
        //上一个点的经纬度
        options.add(new LatLng(privLocation.getLatitude(), privLocation.getLongitude()));
        //当前的经纬度
        options.add(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()));
        options.width(10).geodesic(true).color(Color.GREEN);
        aMap.addPolyline(options);
        //距离的计算
        distance = AMapUtils.calculateLineDistance(new LatLng(privLocation.getLatitude(),
                privLocation.getLongitude()), new LatLng(curLocation.getLatitude(),
                curLocation.getLongitude()));

    }
    /**
     * 必须重写的方法
     */
    @Override
    public void onDestroy() {
        mapView.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        super.onDestroy();
    }

    /**
     * 必须重写的方法
     */
    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    /**
     * 必须重写的方法
     */
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}