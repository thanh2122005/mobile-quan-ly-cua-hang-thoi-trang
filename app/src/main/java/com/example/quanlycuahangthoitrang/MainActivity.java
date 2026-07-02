package com.example.quanlycuahangthoitrang;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        
        // Bật chế độ hiển thị tràn viền (Edge-to-Edge) cho ứng dụng
        EdgeToEdge.enable(this);
        
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_main);
        
        // Lắng nghe sự kiện để đẩy giao diện xuống, không bị che khuất bởi thanh trạng thái (Status Bar) hoặc thanh điều hướng (Navigation Bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}