package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.model.CartItem;
import com.example.quanlycuahangthoitrang.utils.CartManager;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

public class CartActivity extends AppCompatActivity {

    private CartAdapter adapter;
    private TextView tvTotalPrice;
    private LinearLayout layoutEmptyCart;
    private RecyclerView rvCartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        rvCartItems = findViewById(R.id.rvCartItems);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(CartManager.getCartItems(), new CartAdapter.OnCartItemInteractionListener() {
            @Override
            public void onIncrease(CartItem item) {
                CartManager.increaseQuantity(item.getProduct().getId());
                refreshCart();
            }

            @Override
            public void onDecrease(CartItem item) {
                CartManager.decreaseQuantity(item.getProduct().getId());
                refreshCart();
            }

            @Override
            public void onRemove(CartItem item) {
                CartManager.removeFromCart(item.getProduct().getId());
                refreshCart();
            }
        });
        rvCartItems.setAdapter(adapter);

        findViewById(R.id.btnContinueShopping).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, UserHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            if (CartManager.getCartItems().isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            }
        });

        refreshCart();
    }

    private void refreshCart() {
        adapter.updateData(CartManager.getCartItems());
        tvTotalPrice.setText(FormatUtils.formatPrice(CartManager.getTotalPrice()));

        if (CartManager.getCartItems().isEmpty()) {
            layoutEmptyCart.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.GONE);
        } else {
            layoutEmptyCart.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.VISIBLE);
        }
    }
}
