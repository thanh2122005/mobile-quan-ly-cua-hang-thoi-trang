package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_welcome);

        // Ánh xạ view từ XML sang Java
        View btnStart = findViewById(R.id.btnStart);
        // Ánh xạ view từ XML sang Java
        View btnSkip = findViewById(R.id.btnSkip);

        // =========================================================================
        // XỬ LÝ NÚT [BẮT ĐẦU]
        // Người dùng mới tải App sẽ bấm nút này để xem màn hình Giới thiệu (Onboarding)
        // =========================================================================
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, OnboardingActivity.class);
            startActivity(intent);
            // Dùng hàm finish() để đóng luôn màn hình Welcome này lại
            // Tác dụng: Để khi sang màn hình sau, bấm nút Back trên điện thoại sẽ không bị lùi về Welcome nữa
            finish();
        });

        // =========================================================================
        // XỬ LÝ NÚT [BỎ QUA]
        // Dành cho người dùng cũ, không muốn xem Giới thiệu mà muốn nhảy thẳng vào Đăng nhập
        // =========================================================================
        btnSkip.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Tương tự, đóng Welcome lại để ngăn quay ngược
        });
    }
}
