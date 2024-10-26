package com.example.whack_a_mole;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GameRecordDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "game_data.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "location_data";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_NAME = "username";
    public static final String COLUMN_LATITUDE = "latitude"; // 经度
    public static final String COLUMN_LONGITUDE = "longitude";// 纬度
    public static final String COLUMN_TIMESTAMP = "timestamp"; // 时间戳
    public static final String COLUMN_ADDRESS = "address"; // 地址列
    public static final String COLUMN_GAME_STATUS = "game_status"; // 游戏状态列
    public static final String COLUMN_GAME_INFO = "game_info"; // 游戏信息列

    public GameRecordDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_LATITUDE + " REAL,"
                + COLUMN_LONGITUDE + " REAL,"
                + COLUMN_TIMESTAMP + " TEXT,"
                + COLUMN_ADDRESS + " TEXT," // 创建地址列
                + COLUMN_GAME_STATUS + " TEXT," // 创建游戏状态列
                + COLUMN_GAME_INFO + " TEXT" + // 创建游戏信息列
                ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void deleteAllLocationData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        Log.d("DeleteData", "GameRecordDB所有数据已删除！");
        db.close();
    }

    public void saveLocationToDatabase(String username, double latitude, double longitude, String timestamp, String address, String gameStatus, String gameInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, username);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_TIMESTAMP, timestamp);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_GAME_STATUS, gameStatus);
        values.put(COLUMN_GAME_INFO, gameInfo);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Cursor getAllLocationDataCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public void sendDataToRemoteServer(String username, double latitude, double longitude, String timestamp, String address, String gameStatus, String gameInfo) {
        RetrofitClient retrofitClient = new RetrofitClient();
        ActInfo actInfo = new ActInfo(username, latitude, longitude, Long.parseLong(timestamp), address, gameStatus, gameInfo);
        retrofitClient.insertData(actInfo, new RetrofitClient.DataCallback<ActInfo>() {
            @Override
            public void onSuccess(ActInfo data) {
                // 打印成功插入的数据
                Log.d("InsertData", "数据插入成功: " + data.toString());
            }

            @Override
            public void onError(Throwable throwable) {
                // 打印错误信息
                Log.e("InsertData", "插入数据时发生错误: " + throwable.getMessage(), throwable);
            }
        });
    }
}
