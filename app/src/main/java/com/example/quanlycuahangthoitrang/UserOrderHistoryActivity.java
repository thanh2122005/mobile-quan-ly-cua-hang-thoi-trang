package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Order;
import com.example.quanlycuahangthoitrang.model.User;
import com.example.quanlycuahangthoitrang.utils.SessionManager;

import java.util.List;

public class UserOrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvUserOrders;
    private TextView tvEmptyOrders;
    private UserOrderAdapter adapter;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_user_order_history);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);
        // Khởi tạo trình quản lý phiên đăng nhập
        sessionManager = new SessionManager(this);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Ánh xạ view từ XML sang Java
        rvUserOrders = findViewById(R.id.rvUserOrders);
        // Ánh xạ view từ XML sang Java
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);

        rvUserOrders.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();
    }

    private void loadOrders() {
        String email = sessionManager.getEmail();
        User currentUser = dbHelper.getUserByEmail(email);

        if (currentUser != null) {
            List<Order> userOrders = dbHelper.getOrdersByUser(currentUser.getId());
            if (userOrders.isEmpty()) {
                tvEmptyOrders.setVisibility(View.VISIBLE);
                rvUserOrders.setVisibility(View.GONE);
            } else {
                tvEmptyOrders.setVisibility(View.GONE);
                rvUserOrders.setVisibility(View.VISIBLE);
                
                adapter = new UserOrderAdapter(userOrders, order -> {
                    Intent intent = new Intent(UserOrderHistoryActivity.this, UserOrderDetailActivity.class);
                    intent.putExtra("order_id", order.getId());
                    startActivity(intent);
                });
                rvUserOrders.setAdapter(adapter);
            }
        } else {
            tvEmptyOrders.setVisibility(View.VISIBLE);
            rvUserOrders.setVisibility(View.GONE);
        }
    }
}
