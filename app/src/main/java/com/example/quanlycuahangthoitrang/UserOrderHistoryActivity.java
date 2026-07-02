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

    // Hàm lấy danh sách đơn hàng của khách hàng từ Database và hiển thị lên màn hình
    private void loadOrders() {
        // Bước 1: Lấy Email của người dùng đang đăng nhập từ SessionManager
        String email = sessionManager.getEmail();
        
        // Bước 2: Dùng Email đó tìm trong bảng Users để lấy ra đối tượng User (chứa ID thật)
        User currentUser = dbHelper.getUserByEmail(email);

        // Rào cản: Nếu tìm thấy User (Tức là khách hàng hợp lệ)
        if (currentUser != null) {
            // Bước 3: Đưa ID của User vào hàm getOrdersByUser để lọc ra TẤT CẢ đơn hàng CỦA RIÊNG KHÁCH HÀNG NÀY
            List<Order> userOrders = dbHelper.getOrdersByUser(currentUser.getId());
            
            // Bước 4: Kiểm tra xem khách đã mua hàng bao giờ chưa
            if (userOrders.isEmpty()) {
                // Nếu List bị rỗng (Chưa mua bao giờ) -> Hiện dòng chữ "Bạn chưa có đơn hàng nào"
                tvEmptyOrders.setVisibility(View.VISIBLE);
                // Giấu cái Danh sách cuộn đi để không bị lỗi giao diện
                rvUserOrders.setVisibility(View.GONE);
            } else {
                // Nếu List có dữ liệu (Đã từng mua) -> Giấu dòng chữ trống đi
                tvEmptyOrders.setVisibility(View.GONE);
                // Hiện danh sách cuộn lên
                rvUserOrders.setVisibility(View.VISIBLE);
                
                // Khởi tạo Adapter mới để đổ dữ liệu vào danh sách
                adapter = new UserOrderAdapter(userOrders, order -> {
                    // Bắt sự kiện: Khách bấm vào 1 Đơn hàng cụ thể -> Mở trang Chi tiết đơn hàng
                    Intent intent = new Intent(UserOrderHistoryActivity.this, UserOrderDetailActivity.class);
                    // Gửi kèm ID của đơn hàng đó qua trang kia
                    intent.putExtra("order_id", order.getId());
                    startActivity(intent);
                });
                
                // Gắn Adapter vào Danh sách
                rvUserOrders.setAdapter(adapter);
            }
        } else {
            // Lỗi không tìm thấy người dùng
            tvEmptyOrders.setVisibility(View.VISIBLE);
            rvUserOrders.setVisibility(View.GONE);
        }
    }

    // Hàm tự động chạy khi người dùng quay lại màn hình này từ màn hình Chi tiết (VD: Sau khi ấn Hủy đơn xong)
    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại toàn bộ lịch sử đơn hàng để thấy được trạng thái mới nhất ("Đã hủy")
        loadOrders();
    }
}
