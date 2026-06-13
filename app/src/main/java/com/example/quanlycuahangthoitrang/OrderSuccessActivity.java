package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.utils.CartManager;

public class OrderSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        String orderId = getIntent().getStringExtra("order_id");
        if (orderId == null) {
            orderId = "DH001";
        }
        
        TextView tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderId.setText("Mã đơn hàng: " + orderId);

        findViewById(R.id.btnViewOrder).setOnClickListener(v -> {
            startActivity(new Intent(OrderSuccessActivity.this, UserOrderDetailActivity.class));
            finish();
        });

        findViewById(R.id.btnGoHome).setOnClickListener(v -> {
            CartManager.clearCart();
            Intent intent = new Intent(OrderSuccessActivity.this, UserHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
