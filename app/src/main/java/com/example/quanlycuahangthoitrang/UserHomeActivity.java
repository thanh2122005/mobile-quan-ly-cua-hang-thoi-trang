package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.data.MockData;
import com.example.quanlycuahangthoitrang.model.Product;
import com.example.quanlycuahangthoitrang.utils.CartManager;

public class UserHomeActivity extends AppCompatActivity {

    private TextView tvCartBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        tvCartBadge = findViewById(R.id.tvCartBadge);

        // Navigation
        setupNavigation();

        // Banner & View More
        findViewById(R.id.btnExplore).setOnClickListener(v -> openProductList("Tất cả"));
        findViewById(R.id.tvViewMore).setOnClickListener(v -> openProductList("Tất cả"));

        // Chips
        findViewById(R.id.chipCategory1).setOnClickListener(v -> openProductList("Tất cả"));
        findViewById(R.id.chipCategory2).setOnClickListener(v -> openProductList("Áo"));
        findViewById(R.id.chipCategory3).setOnClickListener(v -> openProductList("Quần"));
        findViewById(R.id.chipCategory4).setOnClickListener(v -> openProductList("Giày"));
        findViewById(R.id.chipCategory5).setOnClickListener(v -> openProductList("Phụ kiện"));

        // RecyclerView
        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false; // Disable to let ScrollView handle scrolling
            }
        };
        rvProducts.setLayoutManager(layoutManager);
        
        java.util.List<Product> allProducts = MockData.getProducts();
        java.util.List<Product> displayProducts = allProducts.size() > 6 ? allProducts.subList(0, 6) : allProducts;
        
        ProductUserAdapter adapter = new ProductUserAdapter(displayProducts, new ProductUserAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(UserHomeActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onAddToCartClick(Product product) {
                CartManager.addToCart(product, 1);
                Toast.makeText(UserHomeActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                updateCartBadge();
            }
        });
        rvProducts.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void updateCartBadge() {
        if (tvCartBadge != null) {
            int total = CartManager.getTotalQuantity();
            if (total > 0) {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadge.setText(String.valueOf(total));
            } else {
                tvCartBadge.setVisibility(View.GONE);
            }
        }
    }

    private void openProductList(String category) {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void setupNavigation() {
        View navCart = findViewById(R.id.navCart);
        View navOrders = findViewById(R.id.navOrders);
        View navAccount = findViewById(R.id.navAccount);
        View btnCartTop = findViewById(R.id.btnCartTop);

        if (navCart != null) {
            navCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        }

        if (navOrders != null) {
            navOrders.setOnClickListener(v -> startActivity(new Intent(this, UserOrderHistoryActivity.class)));
        }

        if (navAccount != null) {
            navAccount.setOnClickListener(v -> startActivity(new Intent(this, UserProfileActivity.class)));
        }

        if (btnCartTop != null) {
            btnCartTop.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        }
    }
}
