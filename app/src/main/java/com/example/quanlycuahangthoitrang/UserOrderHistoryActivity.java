package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class UserOrderHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_history);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        if(findViewById(R.id.btnOrderDetail) != null) {
            findViewById(R.id.btnOrderDetail).setOnClickListener(v -> {
                startActivity(new Intent(UserOrderHistoryActivity.this, UserOrderDetailActivity.class));
            });
        }
    }
}
