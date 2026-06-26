package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtPhone, edtPassword, edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_register);

        // Ánh xạ các trường nhập liệu
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        // Ánh xạ các nút bấm
        View btnBack = findViewById(R.id.btnBack);
        View btnRegister = findViewById(R.id.btnRegister);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // ----------------------------------------------------
        // XỬ LÝ SỰ KIỆN KHI BẤM NÚT [ĐĂNG KÝ]
        // ----------------------------------------------------
        btnRegister.setOnClickListener(v -> {
            // Lấy toàn bộ dữ liệu người dùng nhập và xóa khoảng trắng thừa ở hai đầu
            String fullName = edtFullName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            // BƯỚC 1: Kiểm tra rỗng
            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()
                    || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return; // Dừng lại, bắt nhập lại
            }

            // BƯỚC 2: Kiểm tra định dạng Email (Dùng Regex của Android)
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // BƯỚC 3: Kiểm tra định dạng Số điện thoại (Bắt đầu bằng số 0 và có đúng 10 số)
            if (!phone.matches("^0\\d{9}$")) {
                Toast.makeText(this, "Số điện thoại không hợp lệ (Phải là 10 số và bắt đầu bằng 0)", Toast.LENGTH_SHORT).show();
                return;
            }

            // BƯỚC 4: Kiểm tra mật khẩu gõ lại có khớp không
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

        // BƯỚC 5: Đã bỏ bước Đồng ý điều khoản

            // BƯỚC 6: Kiểm tra xem Email này đã có ai dùng đăng ký trước đó chưa
            com.example.quanlycuahangthoitrang.database.DatabaseHelper db = new com.example.quanlycuahangthoitrang.database.DatabaseHelper(this);
            if (db.checkUserEmail(email)) {
                Toast.makeText(this, "Email này đã được đăng ký", Toast.LENGTH_SHORT).show();
                return;
            }

            // BƯỚC 7: TẠO TÀI KHOẢN MỚI
            // Tài khoản tạo từ màn hình này mặc định sẽ có quyền "user" (Khách hàng)
            com.example.quanlycuahangthoitrang.model.User newUser = new com.example.quanlycuahangthoitrang.model.User(
                    0, fullName, email, password, phone, "", "user"
            );

            // Ghi dữ liệu vào Database
            if (db.insertUser(newUser)) {
                Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                
                // Đăng ký xong thì chuyển thẳng sang màn hình Đăng nhập
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Đóng trang đăng ký
            } else {
                Toast.makeText(this, "Lỗi khi đăng ký", Toast.LENGTH_SHORT).show();
            }
        });

        // Link đăng nhập
        tvLoginLink.setOnClickListener(v -> finish());
    }
}
