package com.example.quanlycuahangthoitrang;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_about);

        // Bắt sự kiện bấm nút quay lại
        // Lệnh finish() sẽ đóng màn hình About này lại và quay trở về màn hình trước đó
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
