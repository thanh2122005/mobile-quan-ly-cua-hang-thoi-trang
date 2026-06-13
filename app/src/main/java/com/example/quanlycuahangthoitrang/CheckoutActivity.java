package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.utils.CartManager;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

public class CheckoutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        TextView tvTotalQuantity = findViewById(R.id.tvTotalQuantity);
        TextView tvTotalPriceSummary = findViewById(R.id.tvTotalPriceSummary);
        TextView tvOrderTotal = findViewById(R.id.tvOrderTotal);

        int totalQty = CartManager.getTotalQuantity();
        int totalPrice = CartManager.getTotalPrice();

        tvTotalQuantity.setText("Tạm tính (" + totalQty + " sản phẩm)");
        tvTotalPriceSummary.setText(FormatUtils.formatPrice(totalPrice));
        tvOrderTotal.setText(FormatUtils.formatPrice(totalPrice));

        EditText edtName = findViewById(R.id.edtName);
        EditText edtPhone = findViewById(R.id.edtPhone);
        EditText edtAddress = findViewById(R.id.edtAddress);

        findViewById(R.id.btnConfirmOrder).setOnClickListener(v -> {
            if (CartManager.getCartItems().isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
            intent.putExtra("order_id", "DH001");
            startActivity(intent);
            finish();
        });
    }
}
