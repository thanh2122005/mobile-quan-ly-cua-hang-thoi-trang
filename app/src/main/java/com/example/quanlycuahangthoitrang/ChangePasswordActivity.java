package com.example.quanlycuahangthoitrang;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.User;
import com.example.quanlycuahangthoitrang.utils.SessionManager;

public class ChangePasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_change_password);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Khởi tạo bộ công cụ thao tác với CSDL
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        // Khởi tạo trình quản lý phiên đăng nhập
        SessionManager sessionManager = new SessionManager(this);

        // Ánh xạ view từ XML sang Java
        EditText edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        // Ánh xạ view từ XML sang Java
        EditText edtNewPassword = findViewById(R.id.edtNewPassword);
        // Ánh xạ view từ XML sang Java
        EditText edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        findViewById(R.id.btnUpdate).setOnClickListener(v -> {
            String current = edtCurrentPassword.getText().toString().trim();
            String newPass = edtNewPassword.getText().toString().trim();
            String confirm = edtConfirmPassword.getText().toString().trim();

            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirm)) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 6) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Mật khẩu mới phải từ 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = sessionManager.getEmail();

            if (dbHelper.changePassword(email, current, newPass)) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Mật khẩu hiện tại không đúng hoặc lỗi cập nhật", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
