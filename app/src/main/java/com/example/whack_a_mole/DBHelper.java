package com.example.whack_a_mole;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "game_data.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "location_data";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LATITUDE = "latitude"; // 经度
    public static final String COLUMN_LONGITUDE = "longitude";// 纬度
    public static final String COLUMN_TIMESTAMP = "timestamp"; // 时间戳
    public static final String COLUMN_ADDRESS = "address"; // 地址列
    public static final String COLUMN_GAME_STATUS = "game_status"; // 游戏状态列
    public static final String COLUMN_GAME_INFO = "game_info"; // 游戏信息列

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
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

    public void saveLocationToDatabase(double latitude, double longitude, String timestamp, String address, String gameStatus, String gameInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
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

}
