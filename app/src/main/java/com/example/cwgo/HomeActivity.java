package com.example.cwgo;

import static com.example.cwgo.util.MapUtil.convertToLatLng;
import static com.example.cwgo.util.MapUtil.convertToLatLonPoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.cwgo.overlay.WalkRouteOverlay;
import com.example.cwgo.util.MapUtil;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import android.Manifest;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements AMapLocationListener,LocationSource,AMap.OnMapClickListener,AMap.OnMapLongClickListener, GeocodeSearch.OnGeocodeSearchListener,EditText.OnKeyListener, RouteSearch.OnRouteSearchListener {

    private static final String TAG="HomeActivity";
    //请求权限码
    private static final int REQUEST_PERMISSIONS = 9527;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private TextView textView;
    private MapView mapView;
    //地图控制器
    private AMap aMap = null;
    //位置更改监听
    private LocationSource.OnLocationChangedListener mListener;
    //定位样式
    private MyLocationStyle myLocationStyle = new MyLocationStyle();
    //定义一个UiSettings对象
    private UiSettings mUiSettings;
    //地理编码搜索
    private GeocodeSearch geocodeSearch;
    //解析成功标识码
    private static final int PARSE_SUCCESS_CODE = 1000;
    //起点、终点
    private EditText etStartAddress, etEndAddress;
    //定位地址
    private String locationAddress;
    //起点地址转坐标标识   1
    private int tag = -1;

    //城市
    private String city;
    //起点
    private LatLonPoint mStartPoint;
    //终点
    private LatLonPoint mEndPoint;
    //路线搜索对象
    private RouteSearch routeSearch;
    //路线规划详情
    private RelativeLayout bottomLayout;
    //花费时间
    private TextView tvTime;
    private Button beginBtn;


    /**
     * 初始化路线
     */
    private void initRoute() {
        try {
            routeSearch = new RouteSearch(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
        routeSearch.setRouteSearchListener((RouteSearch.OnRouteSearchListener) this);
    }




    /**
     * 初始化地图
     * @param savedInstanceState
     */
    private void initMap(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        //一个activity一般只创建一个Bundle对象
        mapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        aMap = mapView.getMap();

        //设置最小缩放等级为16 ，缩放级别范围为[3, 20]
//        aMap.setMinZoomLevel(12);//感觉没必要

        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色  都为0则透明
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        // 自定义精度范围的圆形边框宽度  0 无宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色  都为0则透明
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));

        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);

        //开启室内地图
        aMap.showIndoorMap(true);

        //实例化UiSettings类对象
        mUiSettings = aMap.getUiSettings();
        //隐藏缩放按钮：暂时true因为虚拟机不好操作
        mUiSettings.setZoomControlsEnabled(true);

        // 设置定位监听
        aMap.setLocationSource((LocationSource) this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);

        //设置地图点击事件
        aMap.setOnMapClickListener((AMap.OnMapClickListener) this);
        //设置地图长按事件
        aMap.setOnMapLongClickListener((AMap.OnMapLongClickListener) this);

        try{
            geocodeSearch=new GeocodeSearch(this);
        } catch (AMapException e) {
            throw new RuntimeException(e);
        }
        //设置监听
        geocodeSearch.setOnGeocodeSearchListener((GeocodeSearch.OnGeocodeSearchListener) this);
    }



    /**
     * 初始化定位
     */
    private void initLocation() {
        System.out.println("init");
        //初始化定位
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            Log.d(TAG,"initLocationException");
            e.printStackTrace();
        }
        if (mLocationClient != null) {
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //获取最近3s内精度最高的一次定位结果：
            //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
            mLocationOption.setOnceLocationLatest(true);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置定位请求超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
            mLocationOption.setHttpTimeOut(20000);
            //关闭缓存机制，高精度定位会产生缓存。
            mLocationOption.setLocationCacheEnable(false);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
        }
    }



    /**
     * 检查Android版本
     */
    private void checkingAndroidVersion() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //Android6.0及以上先获取权限再定位
            requestPermission();
        }else {
            //Android6.0以下直接定位
            //启动定位
            mLocationClient.startLocation();
        }
    }

    /**
     * 动态请求权限
     * EasyPermissions好神奇！因为加了这个注释，所以如果是一开始无权限，会去获取权限，获取了权限成功了，一定要成功了才能自动跳到hasPermissions为true的分支
     * 一开始因为一个权限WRITE_EXTERNAL_STORAGE获取不到所以一直不行
     */
    @AfterPermissionGranted(REQUEST_PERMISSIONS)
    private void requestPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE//这个权限能提供的最高版本是32
        };

        if (EasyPermissions.hasPermissions(this, permissions)) {
            //true 有权限 开始定位
            showMsg("已获得权限，可以定位啦！");
            //启动定位
            mLocationClient.startLocation();
        } else {
            Log.d(TAG,"无权限");
            //false 无权限
            EasyPermissions.requestPermissions(this, "需要权限", REQUEST_PERMISSIONS, permissions);
        }
//        mLocationClient.startLocation();//源代码中没有这个？其实还是没有再去判断是否取得权限
    }

    /**
     * 请求权限结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //设置权限请求结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    private void showMsg(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        System.out.println("yesMain");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mapView=findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        etStartAddress=findViewById(R.id.et_start_address);
        //键盘按键监听
        etStartAddress.setOnKeyListener((View.OnKeyListener) this);
        etEndAddress=findViewById(R.id.et_end_address);
        etEndAddress.setOnKeyListener(this);
        bottomLayout=findViewById(R.id.bottom_layout);
        tvTime=findViewById(R.id.tv_time);
        beginBtn=findViewById(R.id.begin);
        beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,CityWalkActivity.class);
                startActivity(intent);
            }
        });

        //初始化定位
        initLocation();
        //初始化地图
        initMap(savedInstanceState);
        //初始化路线
        initRoute();
        //检查安卓版本
        checkingAndroidVersion();
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient != null) {
            mLocationClient.startLocation();//启动定位
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        mapView.onDestroy();
    }


    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 接受异步返回的定位结果
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //地址
                locationAddress = aMapLocation.getAddress();
                //设置当前所在地
                etStartAddress.setText(locationAddress);
//                etStartAddress.setEnabled(false);//禁用输入

                city=aMapLocation.getCity();
                Log.d(TAG,locationAddress);
                showMsg(locationAddress);
                double latitude=aMapLocation.getLatitude();
                double longitude=aMapLocation.getLongitude();
                mStartPoint=convertToLatLonPoint(new LatLng(latitude,longitude));
                //textView.setText(address==null?"无地址":address);
                //停止定位后，本地定位服务并不会被销毁
                mLocationClient.stopLocation();
                System.out.println("address:"+locationAddress);
                if(mListener!=null){
                    mListener.onLocationChanged(aMapLocation);
                }
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
//        showMsg("点击了地图，经度："+latLng.longitude+",纬度："+latLng.latitude);
        latlonToAddress(latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
//        showMsg("点击了地图，经度："+latLng.longitude+",纬度："+latLng.latitude);
        latlonToAddress(latLng);
    }

    /**
     * 坐标转地址
     * @param regeocodeResult
     * @param rCode
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
        //解析result获取地址描述信息
        if(rCode == PARSE_SUCCESS_CODE){
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            //显示解析后的地址
            showMsg("地址："+regeocodeAddress.getFormatAddress());
        }else {
            showMsg("获取地址失败");
        }

    }

    /**
     * 地址转坐标
     * @param geocodeResult
     * @param rCode
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
        if (rCode == PARSE_SUCCESS_CODE) {
            List<GeocodeAddress> geocodeAddressList = geocodeResult.getGeocodeAddressList();
            if(geocodeAddressList!=null && geocodeAddressList.size()>0){
                //判断是不是起点的搜索
                if (tag == 1) {
                    //起点
                    mStartPoint = geocodeAddressList.get(0).getLatLonPoint();
                } else {
                    //终点
                    mEndPoint = geocodeAddressList.get(0).getLatLonPoint();
                }

                if (mStartPoint != null && mEndPoint != null) {
                    //开始路线搜索
                    startRouteSearch();
                }
                else Log.d(TAG,"起点终点为空");
            }

        } else {
            showMsg("获取坐标失败");
        }
    }

    private void latlonToAddress(LatLng latLng){
        //位置点  通过经纬度进行构建
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        //逆编码查询  第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 20, GeocodeSearch.AMAP);
        //异步获取地址信息
        geocodeSearch.getFromLocationAsyn(query);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_ENTER&&event.getAction()==KeyEvent.ACTION_UP){
            //获取输入框的值
            String startAddress=etStartAddress.getText().toString().trim();
            String endAddress = etEndAddress.getText().toString().trim();
            //判断出发地是否有值  不管这个值是定位还是手动输入
            if (startAddress.isEmpty()) {
                showMsg("请输入当前的出发地");
                return false;
            }
            //判断目的地是否有值
            if (endAddress.isEmpty()) {
                showMsg("请输入要前往的目的地");
                return false;
            }
            //当出发地输入框有值的时候，判断这个值是否是定位的地址，是则说明你没有更改过，则不需要进行地址转坐标，不是则需要转换。
            if (!locationAddress.equals(startAddress)) {
                tag = 1;
                GeocodeQuery startQuery = new GeocodeQuery(startAddress, city);
                geocodeSearch.getFromLocationNameAsyn(startQuery);
            } else {
                tag = -1;
            }

//            if (endAddress == null || endAddress.isEmpty()) {
//                showMsg("请输入要前往的目的地地址");
//            }else {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                // name表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode
                GeocodeQuery query = new GeocodeQuery(endAddress, city);
                geocodeSearch.getFromLocationNameAsyn(query);
//            }
            return true;
        }
        return false;
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    //在startRouteSearch后如果搜索到路线就调用这个方法嘛
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int code) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (code == AMapException.CODE_AMAP_SUCCESS) {
            if (walkRouteResult != null && walkRouteResult.getPaths() != null) {
                if (walkRouteResult.getPaths().size() > 0) {
                    final WalkPath walkPath = walkRouteResult.getPaths().get(0);//拿到路线
                    if (walkPath == null) {
                        return;
                    }
                    //绘制路线
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            walkRouteResult.getStartPos(),
                            walkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();

                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = MapUtil.getFriendlyTime(dur) + "(" + MapUtil.getFriendlyLength(dis) + ")";
                    Log.d(TAG, des);
                    //显示步行花费时间
                    tvTime.setText(des);

                    bottomLayout.setVisibility(View.VISIBLE);
                    //跳转到路线详情页面
                    bottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                Intent intent = new Intent(HomeActivity.this,
                                        RouteDetailActivity.class);//转发
                                intent.putExtra("type",0);
                                intent.putExtra("path", walkPath);
                                startActivity(intent);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    });

                } else if (walkRouteResult.getPaths() == null) {
                    showMsg("对不起，没有搜索到相关数据！");
                }
            } else {
                showMsg("对不起，没有搜索到相关数据！");
            }
        } else {
            showMsg("错误码；" + code);
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    /**
     * 开始路线搜索
     */
    private void startRouteSearch() {
        //在地图上添加起点Marker
        aMap.addMarker(new MarkerOptions()
                .position(convertToLatLng(mStartPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point)));
        //在地图上添加终点Marker
        aMap.addMarker(new MarkerOptions()
                .position(convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));

        //搜索路线 构建路径的起终点
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        //构建步行路线搜索对象
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
        // 异步路径规划步行模式查询
        routeSearch.calculateWalkRouteAsyn(query);
    }

}