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

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(UserProfileActivity.this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
        });

        // Bottom nav
        findViewById(R.id.navHome).setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, UserHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        
        findViewById(R.id.navCart).setOnClickListener(v -> {
            // Chuyển sang màn hình tương ứng
            startActivity(new Intent(UserProfileActivity.this, CartActivity.class));
        });
        
        findViewById(R.id.navOrder).setOnClickListener(v -> {
            // Chuyển sang màn hình tương ứng
            startActivity(new Intent(UserProfileActivity.this, UserOrderHistoryActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        String email = sessionManager.getEmail();
        User user = dbHelper.getUserByEmail(email);
        if (user != null) {
            tvUserName.setText(user.getName());
            tvUserEmail.setText(user.getEmail());
        }
    }
}
