package com.example.whack_a_mole;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.core.LatLonPoint;

public class LocationHelper implements AMapLocationListener {

    private AMapLocationClient locationClient;
    private GeocodeSearch geocoderSearch;
    private AMapLocationClientOption locationOption;
    private double latitude;
    private double longitude;
    private String address;

    public LocationHelper(Context context) {
        AMapLocationClient.setApiKey("0855dd4dbeeebc4def5e0a4482df49a5");  // 替换为你的高德API密钥
        AMapLocationClient.updatePrivacyShow(context.getApplicationContext(), true, true);
        AMapLocationClient.updatePrivacyAgree(context.getApplicationContext(), true);

        // 初始化高德定位客户端
        try {
            locationClient = new AMapLocationClient(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClient.setLocationOption(option);
        locationClient.setLocationListener(this);
        locationClient.startLocation();

        // 初始化地理编码
        try {
            geocoderSearch = new GeocodeSearch(context);
        } catch (AMapException e) {
            throw new RuntimeException(e);
        }

        locationOption = new AMapLocationClientOption();

        // 设置高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            latitude = aMapLocation.getLatitude();
            longitude = aMapLocation.getLongitude();
            getAddressFromLatLng(latitude, longitude);
        } else {
            Log.e("LocationHelper", "Location Error: " + (aMapLocation != null ? aMapLocation.getErrorInfo() : "Null location"));
        }
    }

    private void getAddressFromLatLng(double latitude, double longitude) {
        LatLonPoint point = new LatLonPoint(latitude, longitude);
        RegeocodeQuery query = new RegeocodeQuery(point, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
        geocoderSearch.setOnGeocodeSearchListener(new com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                if (rCode == 1000) {
                    address = result.getRegeocodeAddress().getFormatAddress();
                } else {
                    Log.e("LocationHelper", "Geocode Error: " + rCode);
                }
            }

            @Override
            public void onGeocodeSearched(com.amap.api.services.geocoder.GeocodeResult result, int rCode) {
                // 不需要处理
            }
        });
    }

    public double[] getCoordinates() {
        return new double[]{latitude, longitude};
    }

    public String getAddress() {
        return address;
    }

    public void stopLocation() {
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
    }

    public void getLocationInfo(final LocationCallback callback) {
        new Handler().postDelayed(() -> {
            // 获取经纬度
            double[] coordinates = this.getCoordinates();
            latitude = coordinates[0];
            longitude = coordinates[1];

            // 获取地址
            address = this.getAddress();

            // 创建 LocationData 对象并返回
            LocationData locationData = new LocationData(latitude, longitude, address);
            callback.onLocationRetrieved(locationData);
        }, 5000); // 延迟5秒，等待定位完成
    }
}