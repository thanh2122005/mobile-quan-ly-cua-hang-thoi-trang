package com.example.quanlycuahangthoitrang;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.User;
import com.example.quanlycuahangthoitrang.utils.SessionManager;

public class EditProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private EditText edtName, edtEmail, edtPhone, edtAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_edit_profile);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);
        // Khởi tạo trình quản lý phiên đăng nhập
        sessionManager = new SessionManager(this);

        // Ánh xạ view từ XML sang Java
        edtName = findViewById(R.id.edtName);
        // Ánh xạ view từ XML sang Java
        edtEmail = findViewById(R.id.edtEmail);
        // Ánh xạ view từ XML sang Java
        edtPhone = findViewById(R.id.edtPhone);
        // Ánh xạ view từ XML sang Java
        edtAddress = findViewById(R.id.edtAddress);

        // RÀO CẢN: KHÔNG CHO PHÉP ĐỔI EMAIL
        // Email là tài khoản đăng nhập gốc và liên kết với khóa ngoại (Foreign Key)
        // Nên nếu cho sửa thì sẽ gây lỗi đồng bộ dữ liệu. Do đó, khóa ô này lại (Chỉ cho xem).
        edtEmail.setEnabled(false); 

        // Tự động tải thông tin hiện tại lên các ô nhập
        loadUserData();

        // Bắt sự kiện bấm nút quay lại góc trái trên
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // XỬ LÝ SỰ KIỆN KHI BẤM NÚT [LƯU LẠI]
        findViewById(R.id.btnSave).setOnClickListener(v -> {
            // Lấy dữ liệu mới nhất mà khách hàng vừa gõ
            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();

            // KIỂM TRA BẮT BUỘC: Không được để trống bất kỳ trường nào
            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // KIỂM TRA ĐỊNH DẠNG SỐ ĐIỆN THOẠI BẰNG REGEX (BIỂU THỨC CHÍNH QUY)
            // ^0 : Phải bắt đầu bằng số 0
            // \d{9}$ : Theo sau là đúng 9 chữ số nữa (Tổng cộng 10 số)
            if (!phone.matches("^0\\d{9}$")) {
                Toast.makeText(this, "Số điện thoại không hợp lệ (Phải là 10 số và bắt đầu bằng 0)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy lại User cũ từ Database để đảm bảo an toàn dữ liệu
            User currentUser = dbHelper.getUserByEmail(sessionManager.getEmail());
            if (currentUser != null) {
                // Tạo một đối tượng User MỚI, trộn lẫn dữ liệu mới (Tên, SĐT, Địa chỉ) với dữ liệu cũ (ID, Email, Password, Role)
                User updatedUser = new User(
                        currentUser.getId(),
                        name,
                        currentUser.getEmail(), // Giữ nguyên Email cũ
                        currentUser.getPassword(), // Giữ nguyên Mật khẩu cũ
                        phone,
                        address,
                        currentUser.getRole() // Giữ nguyên Quyền (Role) cũ
                );
                
                // Gọi Database để lưu cập nhật
                if (dbHelper.updateUser(updatedUser)) {
                    Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    // Đóng màn hình, quay về trang Hồ Sơ
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Hàm lấy dữ liệu cá nhân từ Session và Database để đổ lên màn hình
    private void loadUserData() {
        // Lấy email đang lưu tạm trong máy (Session)
        String email = sessionManager.getEmail();
        // Lấy toàn bộ thông tin gốc từ CSDL
        User user = dbHelper.getUserByEmail(email);
        
        if (user != null) {
            // Điền chữ vào các ô EditText
            edtName.setText(user.getName());
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getPhone());
            edtAddress.setText(user.getAddress());
        }
    }
}
