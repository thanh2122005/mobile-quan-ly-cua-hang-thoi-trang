package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.User;
import com.example.quanlycuahangthoitrang.utils.SessionManager;

public class UserProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private TextView tvUserName, tvUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_user_profile);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);
        // Khởi tạo trình quản lý phiên đăng nhập
        sessionManager = new SessionManager(this);

        // Ánh xạ view từ XML sang Java
        tvUserName = findViewById(R.id.tvUserName);
        // Ánh xạ view từ XML sang Java
        tvUserEmail = findViewById(R.id.tvUserEmail);

        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            // Chuyển sang màn hình tương ứng
            startActivity(new Intent(UserProfileActivity.this, EditProfileActivity.class));
        });

        findViewById(R.id.btnOrderHistory).setOnClickListener(v -> {
            // Chuyển sang màn hình tương ứng
            startActivity(new Intent(UserProfileActivity.this, UserOrderHistoryActivity.class));
        });

        findViewById(R.id.btnChangePassword).setOnClickListener(v -> {
            // Chuyển sang màn hình tương ứng
            startActivity(new Intent(UserProfileActivity.this, ChangePasswordActivity.class));
        });

        findViewById(R.id.btnAbout).setOnClickListener(v -> {
            // Chuyển sang màn hình tương ứng
            startActivity(new Intent(UserProfileActivity.this, AboutActivity.class));
        });

        // XỬ LÝ ĐĂNG XUẤT (LOGOUT)
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            // Khởi tạo một Hộp thoại (Dialog) hỏi khách hàng xem có chắc muốn thoát không
            new android.app.AlertDialog.Builder(UserProfileActivity.this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    // Nếu khách bấm Đồng ý:
                    // 1. Xóa sạch thông tin lưu trong Session (Đưa trạng thái về chưa đăng nhập)
                    sessionManager.logout();
                    
                    // 2. Chuẩn bị mở màn hình Login
                    Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                    
                    // 3. XÓA SẠCH LỊCH SỬ MÀN HÌNH (FLAG_ACTIVITY_CLEAR_TASK)
                    // Tại sao phải làm vậy? Để khi ra tới Login, khách bấm phím Back (Quay lại) trên điện thoại
                    // thì sẽ thoát luôn app chứ không bị chui ngược lại vào trong app khi đã đăng xuất
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    
                    startActivity(intent);
                    finish(); // Đóng hẳn Activity này lại
                })
                .setNegativeButton("Hủy", null) // Bấm Hủy thì tắt hộp thoại, không làm gì cả
                .show();
        });

        // NÚT ĐẶC BIỆT: CHỈ ADMIN MỚI NHÌN THẤY (Chuyển về trang Quản trị)
        TextView btnReturnAdmin = findViewById(R.id.btnReturnAdmin);
        btnReturnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, AdminDashboardActivity.class);
            // Cờ này giúp tránh việc mở chồng chéo quá nhiều màn hình Admin (Tối ưu RAM)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // ----------------------------------------------------
        // XỬ LÝ THANH ĐIỀU HƯỚNG BÊN DƯỚI (BOTTOM NAVIGATION)
        // ----------------------------------------------------
        findViewById(R.id.navHome).setOnClickListener(v -> {
            // Bấm vào nút Trang Chủ -> Mở UserHomeActivity
            Intent intent = new Intent(UserProfileActivity.this, UserHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        
        findViewById(R.id.navCart).setOnClickListener(v -> {
            // Chuyển sang màn hình Giỏ hàng
            startActivity(new Intent(UserProfileActivity.this, CartActivity.class));
        });
        
        findViewById(R.id.navOrder).setOnClickListener(v -> {
            // Chuyển sang màn hình Lịch sử đơn hàng
            startActivity(new Intent(UserProfileActivity.this, UserOrderHistoryActivity.class));
        });
    }

    // Tự động chạy mỗi khi màn hình này hiện lên (Đảm bảo thông tin luôn mới nhất nếu khách vừa Sửa Hồ Sơ)
    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    // Hàm lấy thông tin cá nhân từ Database hiển thị lên giao diện
    private void loadUserProfile() {
        // Lấy Email từ Session
        String email = sessionManager.getEmail();
        // Lấy User từ DB dựa theo Email
        User user = dbHelper.getUserByEmail(email);
        
        if (user != null) {
            // Điền Tên và Email lên màn hình
            tvUserName.setText(user.getName());
            tvUserEmail.setText(user.getEmail());
            
            // XỬ LÝ PHÂN QUYỀN HIỂN THỊ NÚT ADMIN
            TextView btnReturnAdmin = findViewById(R.id.btnReturnAdmin);
            if ("admin".equals(user.getRole())) {
                // Nếu là Admin thì HIỆN (VISIBLE) nút "Về trang Quản trị"
                btnReturnAdmin.setVisibility(android.view.View.VISIBLE);
            } else {
                // Nếu là Khách hàng thường thì GIẤU LUÔN (GONE) nút đó đi, không cho thấy
                btnReturnAdmin.setVisibility(android.view.View.GONE);
            }
        }
    }
}
