package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            startActivity(new Intent(UserProfileActivity.this, EditProfileActivity.class));
        });

        findViewById(R.id.btnOrderHistory).setOnClickListener(v -> {
            startActivity(new Intent(UserProfileActivity.this, UserOrderHistoryActivity.class));
        });

        findViewById(R.id.btnChangePassword).setOnClickListener(v -> {
            startActivity(new Intent(UserProfileActivity.this, ChangePasswordActivity.class));
        });

        findViewById(R.id.btnAbout).setOnClickListener(v -> {
            startActivity(new Intent(UserProfileActivity.this, AboutActivity.class));
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(UserProfileActivity.this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    com.example.quanlycuahangthoitrang.utils.SessionManager session = new com.example.quanlycuahangthoitrang.utils.SessionManager(this);
                    session.logout();
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
            startActivity(new Intent(UserProfileActivity.this, CartActivity.class));
        });
        
        findViewById(R.id.navOrder).setOnClickListener(v -> {
            startActivity(new Intent(UserProfileActivity.this, UserOrderHistoryActivity.class));
        });
    }
}
