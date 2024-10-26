package com.example.whack_a_mole;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MoreFeatures extends AppCompatActivity {

    private Button btnRealTimeLocation;
    private Button btnSyncDatabase;
    private AlertDialog syncingDialog;
    String username;
    LocationDBHelper locationDBHelper;
    GameRecordDBHelper gameRecordDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_more_features);

        username = getUsername();

        // 获取按钮
        btnRealTimeLocation = findViewById(R.id.btnRealTimeLocation);
        btnSyncDatabase = findViewById(R.id.btnSyncDatabase);

        locationDBHelper = new LocationDBHelper(this);
        gameRecordDBHelper = new GameRecordDBHelper(this);

        // 设置按钮点击事件
        btnRealTimeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到实时定位界面
                Intent intent = new Intent(MoreFeatures.this, RealtimeLocation.class);
                startActivity(intent);
            }
        });

        btnSyncDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 同步数据库逻辑
                syncDatabase();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void syncDatabase() {
        // 显示正在同步的对话框
        syncingDialog = new AlertDialog.Builder(this)
                .setTitle("正在同步")
                .setMessage("请稍候，正在同步数据库...")
                .setCancelable(false) // 不可取消
                .create();

        syncingDialog.show();

        // 在后台线程中执行同步操作
        new Thread(() -> {
            try {
                performDatabaseSync(); // 同步方法
            } catch (Exception e) {
                Log.e("MoreFeatures", "数据库同步失败：" + e.getMessage());
                handleSyncFailure("数据库同步过程中发生异常：" + e.getMessage());
            }
        }).start();
    }

    private void performDatabaseSync() {
        RetrofitClient retrofitClient = new RetrofitClient();
        retrofitClient.getByUsername(username, new RetrofitClient.DataCallback<List<ActInfo>>() {
//        retrofitClient.getAll(new RetrofitClient.DataCallback<List<ActInfo>>() {
            @Override
            public void onSuccess(List<ActInfo> data) {
                if (data != null) {
                    int gameRecordDB = 0, locationDB = 0;
                    locationDBHelper.deleteAllLocationData();
                    gameRecordDBHelper.deleteAllLocationData();
                    for (ActInfo actInfo : data) {
                        long id = actInfo.getId();
                        String userName = actInfo.getUserName();
                        long uid = actInfo.getUid();
                        String status = actInfo.getGame();
                        String timestamp = String.valueOf(actInfo.getTime());
                        String gameInfo = actInfo.getMessage();
                        double latitude = actInfo.getLatitude();
                        double longitude = actInfo.getLongitude();
                        double altitude = actInfo.getAltitude();
                        String address = actInfo.getAddress();

                        if (uid == 1) {
                            gameRecordDBHelper.saveLocationToDatabase(username, latitude, longitude, timestamp, address, status, gameInfo);
                            gameRecordDB++;
                        } else if (uid == 2) {
                            locationDBHelper.saveLocationToDatabase(username, latitude, longitude, timestamp, address);
                            locationDB++;
                        }
//                        if (userName.equals(username)) {
//                            if (uid == 1) {
//                                gameRecordDBHelper.saveLocationToDatabase(username, latitude, longitude, timestamp, address, status, gameInfo);
//                                gameRecordDB++;
//                            } else if (uid == 2) {
//                                locationDBHelper.saveLocationToDatabase(username, latitude, longitude, timestamp, address);
//                                locationDB++;
//                            }
//                        }
                    }
                    // 打印成功插入的数据
                    Log.d("DatabaseSync", "同步数据成功:" + gameRecordDB + "条记录插入gameRecordDB, " + locationDB + "条记录插入locationDB, data中共有"+ data.size() + "条记录");
                    // 同步完成，更新UI在主线程中执行
                    runOnUiThread(() -> {
                        if (syncingDialog != null && syncingDialog.isShowing()) {
                            syncingDialog.dismiss(); // 关闭同步对话框
                        }
                        new AlertDialog.Builder(MoreFeatures.this)
                                .setTitle("同步完成")
                                .setMessage("数据库同步完成！")
                                .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                                .create()
                                .show();
                    });
                } else {
                    Log.e("getByUsername", "data为null!!!!!!!!!!!!!!!!!!!!!");
                    handleSyncFailure("数据获取异常！");
                }
            }

            @Override
            public void onError(Throwable throwable) {
                // 打印错误信息
                Log.e("getByUsername", "同步数据时发生错误: " + throwable.getMessage(), throwable);
            }
        });
    }

    private void handleSyncFailure(String message) {
        runOnUiThread(() -> {
            syncingDialog.dismiss(); // 关闭对话框
            new AlertDialog.Builder(MoreFeatures.this)
                    .setTitle("同步失败")
                    .setMessage("数据库同步失败: " + message)
                    .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });
    }

    private String getUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("username", null); // 如果没有找到则返回 null
    }
}