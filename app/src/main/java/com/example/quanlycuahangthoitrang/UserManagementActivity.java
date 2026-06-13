package com.example.quanlycuahangthoitrang;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UserManagementActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        if(findViewById(R.id.btnDeleteUser) != null) {
            findViewById(R.id.btnDeleteUser).setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn muốn xóa người dùng này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
        }
    }
}
