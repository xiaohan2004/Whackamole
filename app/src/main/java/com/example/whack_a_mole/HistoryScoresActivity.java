package com.example.whack_a_mole;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;


public class HistoryScoresActivity extends AppCompatActivity {

    private SQLiteOpenHelper DBHelper;
    private String DB;
    private MapView mapView;
    private AMap aMap;
    private TextView tvLocationInfo, textViewStatus;
    private Button btnPrevious, btnNext, btnJump;
    private EditText etJumpTo;

    private Cursor cursor;
    private int currentPosition = 0;
    private int totalRecords = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_scores);

        tvLocationInfo = findViewById(R.id.tv_location_info);
        textViewStatus = findViewById(R.id.textViewStatus);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        btnJump = findViewById(R.id.btn_jump);
        etJumpTo = findViewById(R.id.et_jump_to);

        // 检查权限
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // 初始化地图
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap(); // 获取 AMap 实例

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousRecord();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextRecord();
            }
        });

        btnJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToRecord();
            }
        });

        showDatabaseSelectionDialog();
    }

    private void showDatabaseSelectionDialog() {
        // 定义选择项
        String[] options = {"GameRecordDBHelper", "LocationDBHelper"};

        // 创建AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择数据库");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        DBHelper = new GameRecordDBHelper(HistoryScoresActivity.this);
                        DB = "GameRecordDBHelper";
                        loadData();
                        break;
                    case 1:
                        DBHelper = new LocationDBHelper(HistoryScoresActivity.this);
                        DB = "LocationDBHelper";
                        loadData();
                        break;
                }
            }
        });
        builder.setCancelable(false); // 设置为不可取消
        builder.create().show();
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


    private void loadData() {
        if (DBHelper != null) {
            cursor = null;
            if (DBHelper instanceof GameRecordDBHelper) {
                cursor = ((GameRecordDBHelper) DBHelper).getAllLocationDataCursor();
            } else if (DBHelper instanceof LocationDBHelper) {
                cursor = ((LocationDBHelper) DBHelper).getAllLocationDataCursor();
            }

            if (cursor != null) {
                totalRecords = cursor.getCount();
                if (totalRecords > 0) {
                    cursor.moveToFirst();
                    displayCurrentRecord();
                } else {
                    tvLocationInfo.setText("No records found");
                    textViewStatus.setText("0/0");
                }
            }
        } else {
            tvLocationInfo.setText("Database Helper is not initialized");
        }
    }

    private void displayCurrentRecord() {
        if (DB.equals("GameRecordDBHelper")) {
            if (currentPosition < totalRecords && cursor.moveToPosition(currentPosition)) {
                @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex(GameRecordDBHelper.COLUMN_LATITUDE));
                @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex(GameRecordDBHelper.COLUMN_LONGITUDE));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(GameRecordDBHelper.COLUMN_TIMESTAMP));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(GameRecordDBHelper.COLUMN_ADDRESS));
                @SuppressLint("Range") String game_status = cursor.getString(cursor.getColumnIndex(GameRecordDBHelper.COLUMN_GAME_STATUS));
                @SuppressLint("Range") String game_info = cursor.getString(cursor.getColumnIndex(GameRecordDBHelper.COLUMN_GAME_INFO));

                tvLocationInfo.setText("Latitude: " + latitude + "\nLongitude: " + longitude + "\nTimestamp: " + timestamp + "\nTime: " + convertTimestampStringToNormalTime(timestamp) + "\nTimeTGDZ: " + convertTimestampToTianGanDiZhi(timestamp) + "\nAddress: " + address + "\nGame_Status: " + game_status + "\nGame_Info: " + game_info);
                textViewStatus.setText((currentPosition + 1) + "/" + totalRecords);
                showCurrentLocation(latitude, longitude, address); // 更新地图
            }
        } else {
            if (currentPosition < totalRecords && cursor.moveToPosition(currentPosition)) {
                @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex(GameRecordDBHelper.COLUMN_LATITUDE));
                @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex(GameRecordDBHelper.COLUMN_LONGITUDE));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(GameRecordDBHelper.COLUMN_TIMESTAMP));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(GameRecordDBHelper.COLUMN_ADDRESS));

                tvLocationInfo.setText("Latitude: " + latitude + "\nLongitude: " + longitude + "\nTimestamp: " + timestamp + "\nTime: " + convertTimestampStringToNormalTime(timestamp) + "\nTimeTGDZ: " + convertTimestampToTianGanDiZhi(timestamp) + "\nAddress: " + address);
                textViewStatus.setText((currentPosition + 1) + "/" + totalRecords);
                showCurrentLocation(latitude, longitude, address); // 更新地图}
            }
        }
    }

    private void showPreviousRecord() {
        if (currentPosition > 0) {
            currentPosition--;
            displayCurrentRecord();
        }
    }

    private void showNextRecord() {
        if (currentPosition < totalRecords - 1) {
            currentPosition++;
            displayCurrentRecord();
        }
    }

    private void jumpToRecord() {
        String input = etJumpTo.getText().toString();
        int jumpTo = Integer.parseInt(input) - 1; // 输入的条数从 1 开始

        if (jumpTo >= 0 && jumpTo < totalRecords) {
            currentPosition = jumpTo;
            displayCurrentRecord();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
        DBHelper.close();
    }

    public String convertTimestampStringToNormalTime(String timestampStr) {
        long timestamp = Long.parseLong(timestampStr);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置为你所在的时区，比如中国时区 "Asia/Shanghai"
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public String convertTimestampToTianGanDiZhi(String timestampStr) {
        long timestamp = Long.parseLong(timestampStr);
        String[] tianGan = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
        String[] diZhi = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置为你所在的时区，比如中国时区 "Asia/Shanghai"
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // 计算天干地支
        int tgIndex = (year - 4) % 10; // 天干
        int dzIndex = (year - 4) % 12; // 地支
        String yearTianGanDiZhi = tianGan[tgIndex] + diZhi[dzIndex];

        tgIndex = (month + 1) % 10; // 月份天干
        dzIndex = (month + 1) % 12; // 月份地支
        String monthTianGanDiZhi = tianGan[tgIndex] + diZhi[dzIndex];

        tgIndex = (day + 1) % 10; // 日份天干
        dzIndex = (day + 1) % 12; // 日份地支
        String dayTianGanDiZhi = tianGan[tgIndex] + diZhi[dzIndex];

        // 计算时辰
        int shichenIndex = (hour + 1) / 2 % 12; // 时辰，0-11
        String hourTianGanDiZhi = diZhi[shichenIndex];

        return String.format("%s年%s月%s日%s时", yearTianGanDiZhi, monthTianGanDiZhi, dayTianGanDiZhi, hourTianGanDiZhi);
    }

}