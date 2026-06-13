package com.example.quanlycuahangthoitrang;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class UserOrderDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_detail);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
