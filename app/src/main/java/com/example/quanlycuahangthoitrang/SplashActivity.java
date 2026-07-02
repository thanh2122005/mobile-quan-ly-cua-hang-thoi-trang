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
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_splash);

        // Khởi tạo Database để trigger onCreate/onUpgrade
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase(); // Chạy tạo database nếu chưa có

        // SỬ DỤNG HANDLER ĐỂ TẠO ĐỘ TRỄ (DELAY)
        // Hệ thống sẽ đợi đúng 1.5 giây (1500 mili-giây) rồi mới chạy đoạn code bên trong ngoặc nhọn
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            
            // Bước 1: Gọi Trình quản lý phiên (SessionManager) để xem khách đã từng đăng nhập trước đó chưa
            com.example.quanlycuahangthoitrang.utils.SessionManager session = new com.example.quanlycuahangthoitrang.utils.SessionManager(this);
            
            // Bước 2: KIỂM TRA TRẠNG THÁI ĐĂNG NHẬP
            if (session.isLoggedIn()) {
                // NẾU ĐÃ ĐĂNG NHẬP (Lưu mật khẩu thành công ở lần trước)
                // -> Kiểm tra xem cái tài khoản đang lưu đó là Admin hay Khách hàng
                if ("admin".equals(session.getRole())) {
                    // Nếu là Admin -> Mở thẳng trang Tổng quan Quản trị (Không bắt đăng nhập lại)
                    startActivity(new Intent(SplashActivity.this, AdminDashboardActivity.class));
                } else {
                    // Nếu là Khách hàng -> Mở thẳng trang Chủ mua sắm
                    startActivity(new Intent(SplashActivity.this, UserHomeActivity.class));
                }
            } else {
                // NẾU CHƯA ĐĂNG NHẬP (Hoặc vừa Đăng xuất xong)
                // -> Mở trang Chào mừng (WelcomeActivity) để khách có thể Đăng ký / Đăng nhập
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            }
            
            // Bước 3: Đóng hẳn màn hình Splash chờ này lại để khách không bấm Back lùi về được nữa
            finish();
            
        }, SPLASH_DELAY);
    }
}
