package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.User;
import com.example.quanlycuahangthoitrang.utils.SessionManager;

public class AdminProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private TextView tvAdminName, tvAdminEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_admin_profile);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);
        // Khởi tạo trình quản lý phiên đăng nhập
        sessionManager = new SessionManager(this);

        // Ánh xạ view từ XML sang Java
        tvAdminName = findViewById(R.id.tvAdminName);
        // Ánh xạ view từ XML sang Java
        tvAdminEmail = findViewById(R.id.tvAdminEmail);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(AdminProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {
        String email = sessionManager.getEmail();
        User user = dbHelper.getUserByEmail(email);
        if (user != null) {
            tvAdminName.setText(user.getName());
            tvAdminEmail.setText(user.getEmail());
        }
    }
}
