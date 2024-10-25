package com.example.whack_a_mole;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amap.api.maps.MapView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoreFeatures extends AppCompatActivity {

    private MapView mapView;
    private TextView textViewLocationInfo;
    private Button buttonStartLocation;
    private AMap aMap;
    private boolean isLocating = false; // 标记当前定位状态
    private LocationDBHelper locationDBHelper; // 数据库帮助类
    private LocationHelper locationHelper;
    private boolean isTracking = false;
    private final int LOCATION_UPDATE_INTERVAL = 15000; // 30秒更新一次
    private Handler handler = new Handler(); // 用于定时更新
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_features);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        username = getUsername();

        // 初始化视图组件
        mapView = findViewById(R.id.map);
        textViewLocationInfo = findViewById(R.id.textViewLocationInfo);
        buttonStartLocation = findViewById(R.id.buttonStartLocation);

        // 初始化地图
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();

        // 初始化数据库
        locationDBHelper = new LocationDBHelper(this);

        // 创建 LocationHelper 实例
        locationHelper = new LocationHelper(this);

        // 按钮点击事件
        buttonStartLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocating) {
                    stopLocation();
                } else {
                    startLocation();
                }
            }
        });
    }

    private void startLocation() {
        isTracking = true;
        isLocating = true;
        buttonStartLocation.setText("停止定位");

        // 每隔 30 秒更新一次位置信息
        updateLocation();
    }

    private void stopLocation() {
        isTracking = false;
        isLocating = false;
        buttonStartLocation.setText("开始定位");
        locationHelper.stopLocation(); // 停止定位

        // 移除更新位置的回调
        handler.removeCallbacksAndMessages(null);
    }

    private void updateLocation() {
        locationHelper.getLocationInfo(locationData -> {
            runOnUiThread(() -> {
                double latitude = locationData.getLatitude();
                double longitude = locationData.getLongitude();
                String address = locationData.getAddress();
                String timestamp = String.valueOf(System.currentTimeMillis());

                // 更新文本信息
                textViewLocationInfo.setText("时间戳: " + timestamp + "\n时间: " + convertTimestampStringToNormalTime(timestamp) + "\n经度: " + latitude + "\n纬度: " + longitude + "\n地址: " + address);
                showCurrentLocation(latitude, longitude, address);
                locationDBHelper.saveLocationToDatabase(username, latitude, longitude, timestamp, address);
            });
        });

        // 每 30 秒再次调用更新位置
        if (isTracking) {
            handler.postDelayed(this::updateLocation, LOCATION_UPDATE_INTERVAL);
        }
    }

    private void showCurrentLocation(double latitude, double longitude, String address) {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        // 清除之前的标记
        aMap.clear();

        // 添加当前位置标记
        aMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(address)
                .snippet("Latitude: " + latitude + "\nLongitude: " + longitude)
                .draggable(true)); // 如果需要可拖动，可以设置为 true

        // 移动摄像头到当前位置
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15)); // 15 是缩放级别
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationDBHelper != null) {
            locationDBHelper.close(); // 关闭数据库
        }
        if (locationHelper != null) {
            locationHelper.stopLocation();
        }
        handler.removeCallbacksAndMessages(null); // 停止所有回调
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private String getUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("username", null); // 如果没有找到则返回 null
    }

    public String convertTimestampStringToNormalTime(String timestampStr) {
        long timestamp = Long.parseLong(timestampStr);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置为你所在的时区，比如中国时区 "Asia/Shanghai"
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}
