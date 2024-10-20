package com.example.whack_a_mole;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.SoundPool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class GameActivity extends AppCompatActivity {
    private TextView scoreText;
    private TextView timerText;
    private Button startButton; // 开始按钮
    private int score = 0;
    private int timeRemaining = 30; // 30 seconds
    private boolean isGameActive = false;
    private int maxMolesToShow = 3; // 默认最大显示地鼠数
    private int moleDuration = 1000; // 默认地鼠持续时间（毫秒）
    private ImageView[] moles;
    private Random random = new Random();
    private Handler handler = new Handler();
    private String selectedDifficulty = "medium";
    private SoundPool soundPool;
    private int hitSoundId;
    private int missSoundId;
    private DBHelper dbHelper;
    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);
        startButton = findViewById(R.id.btnStart);

        moles = new ImageView[]{
                findViewById(R.id.mole1),
                findViewById(R.id.mole2),
                findViewById(R.id.mole3),
                findViewById(R.id.mole4),
                findViewById(R.id.mole5),
                findViewById(R.id.mole6),
                findViewById(R.id.mole7),
                findViewById(R.id.mole8),
                findViewById(R.id.mole9),
                findViewById(R.id.mole10),
                findViewById(R.id.mole11),
                findViewById(R.id.mole12),
                findViewById(R.id.mole13),
                findViewById(R.id.mole14),
                findViewById(R.id.mole15),
                findViewById(R.id.mole16),
        };

        startButton.setOnClickListener(v -> startGame());

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        hitSoundId = soundPool.load(this, R.raw.hit_sound, 1);
        missSoundId = soundPool.load(this, R.raw.miss_sound, 1);

        dbHelper = new DBHelper(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        showDifficultyDialog();

        // 创建 LocationHelper 实例
        locationHelper = new LocationHelper(this);
    }


    @Override
    protected void onDestroy() {
        recordGameState("中途退出", "游戏难度：" + selectedDifficulty + ";游戏分数：" + score);
        dbHelper.close();
        if (locationHelper != null) {
            locationHelper.stopLocation();
        }
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    private void showDifficultyDialog() {
        final String[] difficulties = {"beginner", "easy", "medium", "hard", "expert", "nightmare"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择难度")
                .setItems(difficulties, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedDifficulty = difficulties[which];
                    }
                });
        builder.create().show();
    }

    private void startGame() {
        setDifficulty(selectedDifficulty);
        // 禁用开始按钮，防止多次点击
        startButton.setEnabled(false);
        recordGameState("游戏开始", "游戏难度：" + selectedDifficulty);

        score = 0;
        timeRemaining = 30;
        isGameActive = true;
        updateScore();
        updateTimer();
        showMoles();
    }

    private void setDifficulty(String level) {
        switch (level) {
            case "beginner":
                maxMolesToShow = 3;
                moleDuration = 2000;
                break;
            case "easy":
                maxMolesToShow = 4;
                moleDuration = 1500;
                break;
            case "medium":
                maxMolesToShow = 5;
                moleDuration = 1000;
                break;
            case "hard":
                maxMolesToShow = 6;
                moleDuration = 700;
                break;
            case "expert":
                maxMolesToShow = 8;
                moleDuration = 650;
                break;
            case "nightmare":
                maxMolesToShow = 3;
                moleDuration = 170;
                break;
            default:
                maxMolesToShow = 5;
                moleDuration = 1000; // 默认中等难度
                break;
        }
    }

    private void showMoles() {
        if (isGameActive) {
            hideAllMoles();
            int molesToShow = random.nextInt(maxMolesToShow) + 1;

            Set<Integer> selectedIndices = new HashSet<>();
            for (int i = 0; i < molesToShow; i++) {
                int moleIndex;
                do {
                    moleIndex = random.nextInt(moles.length);
                } while (selectedIndices.contains(moleIndex));
                selectedIndices.add(moleIndex);

                ImageView moleButton = moles[moleIndex];
                moleButton.setVisibility(View.VISIBLE);
                moleButton.setTag(false);
            }

            // 设置隐藏时间，如果玩家没有点击则播放“未击中”音效
            handler.postDelayed(() -> {
                for (ImageView mole : moles) {
                    if (mole.getVisibility() == View.VISIBLE && !(boolean) mole.getTag()) {
                        if (soundPool != null) {
                            soundPool.play(missSoundId, 1, 1, 0, 0, 1);
                        }
                    }
                }
                hideAllMoles();
            }, moleDuration);

            // 安排下一次显示地鼠
            handler.postDelayed(this::showMoles, 1000);
        }
    }


    private void hideAllMoles() {
        for (ImageView mole : moles) {
            mole.setVisibility(View.INVISIBLE);
        }
    }

    private void updateScore() {
        scoreText.setText("分数: " + score);
    }

    private void updateTimer() {
        timerText.setText("剩余时间: " + timeRemaining + "s");
        if (timeRemaining > 0) {
            timeRemaining--;
            handler.postDelayed(this::updateTimer, 1000); // 每秒更新一次时间
        } else {
            isGameActive = false; // 游戏结束
            showGameOverDialog(); // 显示游戏结束对话框
        }
    }

    private void showGameOverDialog() {
        recordGameState("游戏结束", "游戏难度：" + selectedDifficulty + ";游戏分数：" + score);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("游戏结束")
                .setMessage("你的最终分数是: " + score)
                .setCancelable(false)
                .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startGame(); // 重新开始游戏
                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish(); // 退出游戏
                    }
                });
        builder.create().show();

        // 重新启用开始按钮
        startButton.setEnabled(true);
    }

    public void onMoleClick(View view) {
        if (isGameActive) {
            score++;
            updateScore();

            // 播放击中音效
            if (soundPool != null) {
                soundPool.play(hitSoundId, 1, 1, 0, 0, 1);
            }

            view.setVisibility(View.INVISIBLE); // 点击后隐藏地鼠
        }
    }

    private void recordGameState(String status, String gameInfo) {
        locationHelper.getLocationInfo(locationData -> {
            double latitude = locationData.getLatitude();
            double longitude = locationData.getLongitude();
            String address = locationData.getAddress();
            String timestamp = String.valueOf(System.currentTimeMillis());

            // 保存到数据库
            dbHelper.saveLocationToDatabase(latitude, longitude, timestamp, address, status, gameInfo);
        });
    }

}