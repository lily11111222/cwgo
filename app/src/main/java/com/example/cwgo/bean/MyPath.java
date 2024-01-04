package com.example.cwgo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.amap.api.services.core.LatLonPoint;

import java.util.ArrayList;
import java.util.List;

public class MyPath implements Parcelable {
    private List<LatLonPoint> path;

    public MyPath(List<LatLonPoint> path){
        this.path=path;
    }

    public List<LatLonPoint> getPath() {
        return path;
    }

    protected MyPath(Parcel in) {
        // 读取数据时保持写入的顺序
        path = new ArrayList<>();
        in.readList(path, LatLonPoint.class.getClassLoader());
    }

    public static final Creator<MyPath> CREATOR = new Creator<MyPath>() {
        @Override
        public MyPath createFromParcel(Parcel in) {
            return new MyPath(in);
        }

        @Override
        public MyPath[] newArray(int size) {
            return new MyPath[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeList(path);
    }
}
