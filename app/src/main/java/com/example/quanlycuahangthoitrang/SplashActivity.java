package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1500; // 1.5 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Khởi tạo Database để trigger onCreate/onUpgrade
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase(); // Chạy tạo database nếu chưa có

        // Chuyển sang màn hình tiếp theo sau 1.5 giây
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            com.example.quanlycuahangthoitrang.utils.SessionManager session = new com.example.quanlycuahangthoitrang.utils.SessionManager(this);
            if (session.isLoggedIn()) {
                if ("admin".equals(session.getRole())) {
                    startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, UserHomeActivity.class));
                }
            } else {
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}
