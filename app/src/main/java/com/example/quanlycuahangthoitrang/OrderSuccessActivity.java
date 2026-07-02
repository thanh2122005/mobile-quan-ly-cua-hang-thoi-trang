package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Order;

public class OrderSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_order_success);

        // Khởi tạo bộ công cụ thao tác với CSDL
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int orderId = getIntent().getIntExtra("order_id", -1);
        
        Order order = dbHelper.getOrderById(orderId);
        String orderCode = order != null ? order.getCode() : "DH001";
        
        // Ánh xạ view từ XML sang Java
        TextView tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderId.setText("Mã đơn hàng: " + orderCode);

        // Bắt sự kiện khi người dùng bấm nút [XEM ĐƠN HÀNG]
        findViewById(R.id.btnViewOrder).setOnClickListener(v -> {
            // Chuyển sang màn hình Chi tiết Đơn Hàng (UserOrderDetailActivity)
            Intent intent = new Intent(OrderSuccessActivity.this, UserOrderDetailActivity.class);
            // Kẹp theo ID của đơn hàng vừa đặt xong
            intent.putExtra("order_id", orderId);
            startActivity(intent);
            // Đóng màn hình Báo Thành Công này lại (để bấm Back không bị quay lại đây)
            finish();
        });

        // Bắt sự kiện khi người dùng bấm nút [VỀ TRANG CHỦ]
        findViewById(R.id.btnGoHome).setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, UserHomeActivity.class);
            // Cờ FLAG_ACTIVITY_CLEAR_TOP: Xóa toàn bộ các màn hình trung gian (Giỏ hàng, Thanh toán, v.v.)
            // đang mở lơ lửng, đưa ứng dụng về lại trạng thái ban đầu ở Trang Chủ, giúp giải phóng RAM.
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
