package com.example.cwgo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.cwgo.bean.MyPath;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CityWalkActivity extends AppCompatActivity {
//    private static final String TAG="CityWalkActivity";
//    //请求权限码
//    private static final int REQUEST_PERMISSIONS = 9527;
//    private MapView mapView;
//    private AMapLocationClient mLocationClient=null;
//    public AMapLocationClientOption mLocationOption = null;
//    //地图控制器
//    private AMap aMap = null;
//    //定位样式
//    private MyLocationStyle myLocationStyle = new MyLocationStyle();
//    //定义一个UiSettings对象
//    private UiSettings mUiSettings;
//    //地理编码搜索
//    private GeocodeSearch geocodeSearch;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_city_walk);
//        mapView=findViewById(R.id.map_view_citywalk);
//        mapView.onCreate(savedInstanceState);
//        //初始化地图
//        initMap(savedInstanceState);
//        //初始化定位
//        initLocation();
//    }
//
//    /**
//     * 初始化定位
//     */
//    private void initLocation() {
//        System.out.println("init");
//        //初始化定位
//        try {
//            mLocationClient = new AMapLocationClient(getApplicationContext());
//        } catch (Exception e) {
//            Log.d(TAG,"initLocationException");
//            e.printStackTrace();
//        }
//        if (mLocationClient != null) {
//            //设置定位回调监听
//            mLocationClient.setLocationListener((AMapLocationListener) this);
//            //初始化AMapLocationClientOption对象
//            mLocationOption = new AMapLocationClientOption();
//            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//            //获取最近3s内精度最高的一次定位结果：
//            //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
//            mLocationOption.setOnceLocationLatest(true);
//            //设置是否返回地址信息（默认返回地址信息）
//            mLocationOption.setNeedAddress(true);
//            //设置定位请求超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
//            mLocationOption.setHttpTimeOut(20000);
//            //关闭缓存机制，高精度定位会产生缓存。
//            mLocationOption.setLocationCacheEnable(false);
//            //给定位客户端对象设置定位参数
//            mLocationClient.setLocationOption(mLocationOption);
//            mLocationClient.stopLocation();
//            mLocationClient.startLocation();
//        }
//    }
//
//    /**
//     * 检查Android版本
//     */
//    private void checkingAndroidVersion() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            //Android6.0及以上先获取权限再定位
//            requestPermission();
//        }else {
//            //Android6.0以下直接定位
//            //启动定位
//            mLocationClient.startLocation();
//        }
//    }
//
//    /**
//     * 动态请求权限
//     * EasyPermissions好神奇！因为加了这个注释，所以如果是一开始无权限，会去获取权限，获取了权限成功了，一定要成功了才能自动跳到hasPermissions为true的分支
//     * 一开始因为一个权限WRITE_EXTERNAL_STORAGE获取不到所以一直不行
//     */
//    @AfterPermissionGranted(REQUEST_PERMISSIONS)
//    private void requestPermission() {
//        String[] permissions = {
//                android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.READ_PHONE_STATE,
////                Manifest.permission.WRITE_EXTERNAL_STORAGE//这个权限能提供的最高版本是32
//        };
//
//        if (EasyPermissions.hasPermissions(this, permissions)) {
//            //true 有权限 开始定位
//            showMsg("已获得权限，可以定位啦！");
//            //启动定位
//            mLocationClient.startLocation();
//        } else {
//            Log.d(TAG,"无权限");
//            //false 无权限
//            EasyPermissions.requestPermissions(this, "需要权限", REQUEST_PERMISSIONS, permissions);
//        }
////        mLocationClient.startLocation();//源代码中没有这个？其实还是没有再去判断是否取得权限
//    }
//    private void showMsg(String msg) {
//        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
//    }
//
//    /**
//     * 请求权限结果
//     * @param requestCode
//     * @param permissions
//     * @param grantResults
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        //设置权限请求结果
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }
//
//    /**
//     * 初始化地图
//     * @param savedInstanceState
//     */
//    private void initMap(Bundle savedInstanceState) {
//        mapView = findViewById(R.id.map_view);
//        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
//        //一个activity一般只创建一个Bundle对象
//        mapView.onCreate(savedInstanceState);
//        //初始化地图控制器对象
//        aMap = mapView.getMap();
//
//        //设置最小缩放等级为16 ，缩放级别范围为[3, 20]
////        aMap.setMinZoomLevel(12);//感觉没必要
//
//        // 自定义定位蓝点图标
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
//        // 自定义精度范围的圆形边框颜色  都为0则透明
//        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
//        // 自定义精度范围的圆形边框宽度  0 无宽度
//        myLocationStyle.strokeWidth(0);
//        // 设置圆形的填充颜色  都为0则透明
//        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
//
//        //设置定位蓝点的Style
//        aMap.setMyLocationStyle(myLocationStyle);
//
//        //开启室内地图
//        aMap.showIndoorMap(true);
//
//        //实例化UiSettings类对象
//        mUiSettings = aMap.getUiSettings();
//        //隐藏缩放按钮：暂时true因为虚拟机不好操作
//        mUiSettings.setZoomControlsEnabled(true);
//
//        // 设置定位监听
//        aMap.setLocationSource((LocationSource) this);
//        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        aMap.setMyLocationEnabled(true);
//
//        //设置地图点击事件
//        aMap.setOnMapClickListener((AMap.OnMapClickListener) this);
//        //设置地图长按事件
//        aMap.setOnMapLongClickListener((AMap.OnMapLongClickListener) this);
//
//        try{
//            geocodeSearch=new GeocodeSearch(this);
//        } catch (AMapException e) {
//            throw new RuntimeException(e);
//        }
//        //设置监听
//        geocodeSearch.setOnGeocodeSearchListener((GeocodeSearch.OnGeocodeSearchListener) this);
//    }
//
//
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        if (mLocationClient != null) {
//            mLocationClient.onDestroy();
//        }
//        mapView.onDestroy();
//    }
//
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause(){
//        super.onPause();
//        mapView.onPause();
//    }
private static final String TAG = "CityWalkActivity";
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
    private Button pauseBtn;
    private AMapLocation startLocation;
    private AMapLocation endLocation;

    private SimpleDateFormat startTime;
    private SimpleDateFormat endTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_walk);
//        //初始化DBFlow
//        FlowManager.init(new FlowConfig.Builder(this).build());
//        FlowManager.init(this);
        mapView = findViewById(R.id.map_view_citywalk);
        mapView.onCreate(savedInstanceState);// 调用地图所必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        stopBtn=findViewById(R.id.stop);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationClient.stopLocation();
                Intent intent=new Intent(CityWalkActivity.this,ResultActivity.class);
                intent.putExtra("path", new MyPath(points));
                startActivity(intent);
            }
        });
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.setMapTextZIndex(2);
        setUpMap();
        try {
            initAmapLocation();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        init();
//        insertModel();
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

    /**
     * 进入历史详情和地图管理页面
     */
//    private void init() {
//
//        offlineMap = findViewById(R.id.offlineMap);
//        offlineMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SportActivity.this, OfflineMapActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        //历史记录
//        record = findViewById(R.id.record);
//        record.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(CityWalkActivity.this, RecordActivity.class);
//                startActivity(intent);
//            }
//        });
//    }

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
        mLocationClient = new AMapLocationClient(this);
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
                        Toast.makeText(CityWalkActivity.this, "经纬度"+totalDistance+"M",Toast.LENGTH_SHORT).show();

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

//    /**
//     * 向数据库中传递数据
//     */
//    private void insertModel() {
//        MyDataBase myDatabase = new MyDataBase();
//        myDatabase.averagespeed = "100";
//        myDatabase.duration = "23";
//        myDatabase.distance = distance;
//        myDatabase.time = "2018-10-23";
//        //myDatabase.save();
//        Log.e("TEST", String.valueOf(myDatabase.averagespeed));
//        Log.e("Sunday", String.valueOf(myDatabase.id));
//        Log.e("Location", String.valueOf(myDatabase.location));
//
//    }

    /**
     * 必须重写的方法
     */
    @Override
    protected void onDestroy() {
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
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    /**
     * 必须重写的方法
     */
    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}