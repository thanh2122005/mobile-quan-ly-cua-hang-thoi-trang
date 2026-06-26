package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_login);

        // Ánh xạ các trường nhập liệu từ XML sang biến Java để xử lý
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        // Ánh xạ các nút bấm
        View btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // ----------------------------------------------------
        // XỬ LÝ SỰ KIỆN KHI NGƯỜI DÙNG BẤM NÚT [ĐĂNG NHẬP]
        // ----------------------------------------------------
        btnLogin.setOnClickListener(v -> {
            // Lấy dữ liệu người dùng gõ vào và cắt bỏ khoảng trắng thừa ở 2 đầu (trim)
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Kiểm tra xem người dùng có để trống ô nào không
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return; // Dừng lại, không chạy code bên dưới nữa
            }

            // Gọi DatabaseHelper để thực hiện truy vấn kiểm tra Email và Password
            com.example.quanlycuahangthoitrang.database.DatabaseHelper db = new com.example.quanlycuahangthoitrang.database.DatabaseHelper(this);
            
            // Nếu hàm checkLogin trả về True tức là Mật khẩu đúng
            if (db.checkLogin(email, password)) {
                // Tiếp tục lấy Quyền (Role) của người dùng này (là "admin" hay "user")
                String role = db.getUserRole(email);
                
                // Lưu trạng thái đăng nhập vào SessionManager để các màn hình khác biết ai đang dùng app
                com.example.quanlycuahangthoitrang.utils.SessionManager session = new com.example.quanlycuahangthoitrang.utils.SessionManager(this);
                session.saveLogin(email, role);

                // KIỂM TRA PHÂN QUYỀN ĐỂ CHUYỂN HƯỚNG MÀN HÌNH
                if ("admin".equals(role)) {
                    // Nếu là Admin -> Chuyển vào Trang quản trị (AdminDashboard)
                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                } else {
                    // Nếu là Khách hàng -> Chuyển vào Trang chủ mua sắm (UserHome)
                    startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                }
                finish(); // Đóng màn hình đăng nhập này lại để không ấn nút Back quay lại được nữa
            } else {
                // Sai email hoặc mật khẩu thì báo lỗi
                Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });

        // Link đăng ký
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // ----------------------------------------------------
        // XỬ LÝ SỰ KIỆN KHI BẤM [QUÊN MẬT KHẨU]
        // ----------------------------------------------------
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> {
                // Mở một hộp thoại (Dialog) để yêu cầu người dùng nhập Email
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Khôi phục mật khẩu");
                builder.setMessage("Vui lòng nhập Email đã đăng ký. Mật khẩu mới sẽ được đặt lại thành '123456'.");

                // Tạo một ô nhập liệu (EditText) ngay bên trong hộp thoại
                final EditText input = new EditText(this);
                input.setHint("Nhập Email của bạn");
                input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setView(input);

                // Cài đặt nút [Khôi phục] trên hộp thoại
                builder.setPositiveButton("Khôi phục", (dialog, which) -> {
                    String emailReset = input.getText().toString().trim();
                    if (emailReset.isEmpty()) {
                        Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    com.example.quanlycuahangthoitrang.database.DatabaseHelper dbReset = new com.example.quanlycuahangthoitrang.database.DatabaseHelper(this);
                    
                    // Kiểm tra xem Email này có tồn tại trong CSDL không
                    if (dbReset.checkUserEmail(emailReset)) {
                        // Gọi hàm resetPassword để đổi pass về cứng "123456"
                        if (dbReset.resetPassword(emailReset, "123456")) {
                            Toast.makeText(this, "Đã đặt lại mật khẩu thành '123456'", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Lỗi khi đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Email không tồn tại trong hệ thống", Toast.LENGTH_SHORT).show();
                    }
                });
                
                // Cài đặt nút [Hủy] để tắt hộp thoại
                builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
                builder.show();
            });
        }
    }
}
