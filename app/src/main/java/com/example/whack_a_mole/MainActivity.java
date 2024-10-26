package com.example.whack_a_mole;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String USERNAME_KEY = "username";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStartGame = findViewById(R.id.btnStartGame);
        Button btnHistoryScores = findViewById(R.id.btnHistoryScores);
        Button btnSettings = findViewById(R.id.btnSettings);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(USERNAME_KEY, null);

        if (savedUsername != null) {
            // 用户名已存在，弹出对话框询问是否修改
            new AlertDialog.Builder(this)
                    .setTitle("用户名已存在")
                    .setMessage("您已经使用用户名 \"" + savedUsername + "\"。是否要修改它？")
                    .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showUsernameInputDialog(); // 弹出输入框让用户输入新用户名
                        }
                    })
                    .setNegativeButton("继续使用", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "欢迎回来, " + savedUsername + "!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        } else {
            // 用户名不存在，可以让用户输入
            showUsernameInputDialog();
        }

        btnStartGame.setOnClickListener(v -> {
            // 跳转到游戏界面
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });

        btnHistoryScores.setOnClickListener(v -> {
            // 跳转到历史分数界面
            Intent intent = new Intent(MainActivity.this, HistoryScoresActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            // 跳转到更多功能界面
            Intent intent = new Intent(MainActivity.this, MoreFeatures.class);
            startActivity(intent);
        });
    }

    private void showUsernameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入用户名");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String username = input.getText().toString().trim();
            if (!username.isEmpty()) {
                saveUsername(username);
                Toast.makeText(this, "欢迎，" + username, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                showUsernameInputDialog();
            }
        });
        builder.setCancelable(false); // 设置为不可取消
        builder.show();
    }

    private void saveUsername(String username) {
        if (sharedPreferences != null) { // 检查 sharedPreferences 是否不为 null
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username);
            editor.apply();
            Log.d("SharedPreferences", "Saved username: " + username);
        } else {
            Log.e("MainActivity", "SharedPreferences is null");
        }
    }
}