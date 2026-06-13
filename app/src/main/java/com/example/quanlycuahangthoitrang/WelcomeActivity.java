package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        View btnStart = findViewById(R.id.btnStart);
        View btnSkip = findViewById(R.id.btnSkip);

        // Nút "Bắt đầu" -> mở Onboarding
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, OnboardingActivity.class);
            startActivity(intent);
            finish();
        });

        // Nút "Bỏ qua" -> mở LoginActivity trực tiếp
        btnSkip.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
