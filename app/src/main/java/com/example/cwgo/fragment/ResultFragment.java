package com.example.cwgo.fragment;

import static com.example.cwgo.util.MapUtil.convertToLatLng;
import static com.example.cwgo.util.MapUtil.latLonListToStr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.cwgo.R;
import com.example.cwgo.bean.MyPath;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment {
    private String TAG = "ResultFragment";
    private MapView mapView;
    private List<LatLonPoint> receivedLatLonPointList; // 从 Intent 中获取的轨迹点列表
    private Polyline polyline; // 用于绘制轨迹的 Polyline
    private AMap aMap;

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private LocationSource.OnLocationChangedListener mListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;


    public ResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultFragment newInstance(MyPath myPath) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putParcelable("path", myPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_result, container, false);
        super.onCreate(savedInstanceState);

        mapView = view.findViewById(R.id.map_view_result);
        mapView.onCreate(savedInstanceState);// 调用地图所必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        Bundle args = getArguments();
        // 从 Intent 中获取轨迹点列表
        if (args != null) {
            MyPath receivedPath = args.getParcelable("path");
            receivedLatLonPointList = receivedPath.getPath();
            try {
                Log.d(TAG, "经纬度转json：" + latLonListToStr(receivedLatLonPointList));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(convertToLatLng(receivedLatLonPointList.get(0)),18));

        // 绘制轨迹
        drawRoute();
        return view;

    }


    private void drawRoute() {
        if (receivedLatLonPointList == null || receivedLatLonPointList.size() < 2) {
            // 至少需要两个轨迹点才能绘制轨迹
            return;
        }

        // 创建 PolylineOptions 对象，用于配置 Polyline 的属性
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(10f); // 设置线宽
        polylineOptions.color(Color.GREEN); // 设置线颜色

        // 遍历轨迹点列表，将每个点添加到 PolylineOptions 中
        for (LatLonPoint latLon : receivedLatLonPointList) {
            polylineOptions.add(new LatLng(latLon.getLatitude(), latLon.getLongitude()));
        }

        // 在地图上绘制 Polyline
        polyline = aMap.addPolyline(polylineOptions);
    }


    // 在 Activity 生命周期方法中管理 MapView 的生命周期
    @Override
    public void onResume() {
        super.onResume();
        // 如果使用了 MapView，在这里调用 mapView.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 如果使用了 MapView，在这里调用 mapView.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 如果使用了 MapView，在这里调用 mapView.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}