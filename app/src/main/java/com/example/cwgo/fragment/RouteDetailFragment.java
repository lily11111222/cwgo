package com.example.cwgo.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.amap.api.services.route.WalkPath;
import com.example.cwgo.R;
import com.example.cwgo.adapter.WalkSegmentListAdapter;
import com.example.cwgo.util.MapUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RouteDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RouteDetailFragment extends Fragment {
    private androidx.appcompat.widget.Toolbar toolbar;
    private TextView tvTitle,tvTime;
    private RecyclerView rv;
    private View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RouteDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment RouteDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RouteDetailFragment newInstance(int type,WalkPath wp) {
        RouteDetailFragment fragment = new RouteDetailFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putParcelable("path", wp);
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
        view=inflater.inflate(R.layout.fragment_route_detail, container, false);
        super.onCreate(savedInstanceState);
        //初始化
        initView();
        return view;
    }

    private void initView(){
        toolbar=view.findViewById(R.id.toolbar);
        tvTitle=view.findViewById(R.id.tv_title);
        tvTime=view.findViewById(R.id.tv_time);
        rv=view.findViewById(R.id.rv);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            //按顶部回退
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        Bundle args = getArguments();
        if(args==null){
            return;
        }
        switch (args.getInt("type",0)){
            case 0://步行
                walkDetail(args);
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
     */
    private void walkDetail(Bundle args) {
        tvTitle.setText("步行路线规划");
        //都是总的路线长度（时间）
        WalkPath walkPath = args.getParcelable("path");//Parcelable 是一种 Android 特定的序列化机制，通常用于在组件之间传递复杂的自定义对象。
        String dur = MapUtil.getFriendlyTime((int) walkPath.getDuration());
        String dis = MapUtil.getFriendlyLength((int) walkPath.getDistance());
        tvTime.setText(dur + "(" + dis + ")");


        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new WalkSegmentListAdapter(R.layout.item_segment, walkPath.getSteps()));//获得每一步？
    }

}