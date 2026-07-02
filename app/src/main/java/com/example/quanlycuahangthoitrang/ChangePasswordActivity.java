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

        // Bắt sự kiện bấm nút [CẬP NHẬT MẬT KHẨU]
        findViewById(R.id.btnUpdate).setOnClickListener(v -> {
            // Lấy dữ liệu mật khẩu cũ và mới từ 3 ô nhập
            String current = edtCurrentPassword.getText().toString().trim();
            String newPass = edtNewPassword.getText().toString().trim();
            String confirm = edtConfirmPassword.getText().toString().trim();

            // RÀO CẢN 1: Bắt buộc điền đủ 3 ô
            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return; // Thoát ra ngay, không chạy tiếp
            }

            // RÀO CẢN 2: Chống gõ nhầm mật khẩu mới
            if (!newPass.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // RÀO CẢN 3: Quy tắc bảo mật mật khẩu
            if (newPass.length() < 6) {
                Toast.makeText(this, "Mật khẩu mới phải từ 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            // Móc email của người dùng đang đăng nhập (Tài khoản hiện tại)
            String email = sessionManager.getEmail();

            // GỌI HÀM CỦA DATABASE ĐỂ KIỂM TRA & ĐỔI MẬT KHẨU CÙNG LÚC
            // Hàm dbHelper.changePassword sẽ làm 2 việc:
            // 1. Kiểm tra xem mật khẩu "current" gõ vào có đúng với CSDL không.
            // 2. Nếu đúng thì lưu đè "newPass" vào và trả về true.
            if (dbHelper.changePassword(email, current, newPass)) {
                Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                // Đổi thành công thì đóng màn hình lại
                finish();
            } else {
                // Nếu trả về false nghĩa là khách gõ sai mật khẩu hiện tại
                Toast.makeText(this, "Mật khẩu hiện tại không đúng hoặc lỗi cập nhật", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
