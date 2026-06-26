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

        edtEmail.setEnabled(false); // Email cannot be changed

        loadUserData();

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("^0\\d{9}$")) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Số điện thoại không hợp lệ (Phải là 10 số và bắt đầu bằng 0)", Toast.LENGTH_SHORT).show();
                return;
            }

            User currentUser = dbHelper.getUserByEmail(sessionManager.getEmail());
            if (currentUser != null) {
                User updatedUser = new User(
                        currentUser.getId(),
                        name,
                        currentUser.getEmail(),
                        currentUser.getPassword(),
                        phone,
                        address,
                        currentUser.getRole()
                );
                if (dbHelper.updateUser(updatedUser)) {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadUserData() {
        String email = sessionManager.getEmail();
        User user = dbHelper.getUserByEmail(email);
        if (user != null) {
            edtName.setText(user.getName());
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getPhone());
            edtAddress.setText(user.getAddress());
        }
    }
}
