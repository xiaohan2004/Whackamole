package com.example.whack_a_mole;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStartGame = findViewById(R.id.btnStartGame);
        Button btnHistoryScores = findViewById(R.id.btnHistoryScores);
        Button btnSettings = findViewById(R.id.btnSettings);

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
            // 跳转到游戏设置界面
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}