package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.data.MockData;
import com.example.quanlycuahangthoitrang.model.Product;
import com.example.quanlycuahangthoitrang.utils.CartManager;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

public class ProductDetailActivity extends AppCompatActivity {

    private int quantity = 1;
    private Product currentProduct;
    private TextView tvQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        int productId = getIntent().getIntExtra("product_id", -1);
        if (productId != -1) {
            currentProduct = MockData.getProductById(productId);
        }

        if (currentProduct == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        android.widget.ImageView ivProductImage = findViewById(R.id.ivProductImage);
        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvProductPrice = findViewById(R.id.tvProductPrice);
        TextView tvProductCategory = findViewById(R.id.tvProductCategory);
        TextView tvProductDesc = findViewById(R.id.tvProductDesc);
        
        tvQuantity = findViewById(R.id.tvQuantity);

        ivProductImage.setImageResource(currentProduct.getImageResId());
        tvProductName.setText(currentProduct.getName());
        tvProductPrice.setText(FormatUtils.formatPrice(currentProduct.getPrice()));
        tvProductCategory.setText(currentProduct.getCategory());
        tvProductDesc.setText(currentProduct.getDescription());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnMinus).setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        findViewById(R.id.btnPlus).setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        findViewById(R.id.btnAddToCart).setOnClickListener(v -> {
            CartManager.addToCart(currentProduct, quantity);
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnBuyNow).setOnClickListener(v -> {
            CartManager.addToCart(currentProduct, quantity);
            startActivity(new Intent(ProductDetailActivity.this, CheckoutActivity.class));
        });
    }
}
