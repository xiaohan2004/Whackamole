package com.example.whack_a_mole;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;



public class HistoryScoresActivity extends AppCompatActivity {

    private DBHelper dbHelper;
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

        dbHelper = new DBHelper(this);
        tvLocationInfo = findViewById(R.id.tv_location_info);
        textViewStatus = findViewById(R.id.textViewStatus);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        btnJump = findViewById(R.id.btn_jump);
        etJumpTo = findViewById(R.id.et_jump_to);

        loadData();

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
    }

    private void loadData() {
        cursor = dbHelper.getAllLocationDataCursor(); // 使用光标逐个读取数据
        totalRecords = cursor.getCount();
        if (totalRecords > 0) {
            cursor.moveToFirst();
            displayCurrentRecord();
        } else {
            tvLocationInfo.setText("No records found");
            textViewStatus.setText("0/0");
        }
    }

    private void displayCurrentRecord() {
        if (currentPosition < totalRecords && cursor.moveToPosition(currentPosition)) {
            double latitude = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_LONGITUDE));
            String timestamp = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TIMESTAMP));
            String address = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ADDRESS));
            String game_status = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_GAME_STATUS));
            String game_info = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_GAME_INFO));

            tvLocationInfo.setText("Latitude: " + latitude + "\nLongitude: " + longitude + "\nTimestamp: " + timestamp + "\nTime: " + convertTimestampStringToNormalTime(timestamp) + "\nTimeTGDZ: " + convertTimestampToTianGanDiZhi(timestamp) + "\nAddress: " + address + "\nGame_Status: " + game_status + "\nGame_Info: " + game_info);
            textViewStatus.setText((currentPosition + 1) + "/" + totalRecords);
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
        dbHelper.close();
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
        String hourTianGanDiZhi=diZhi[shichenIndex];

        return String.format("%s年%s月%s日%s时", yearTianGanDiZhi, monthTianGanDiZhi, dayTianGanDiZhi, hourTianGanDiZhi);
    }

}