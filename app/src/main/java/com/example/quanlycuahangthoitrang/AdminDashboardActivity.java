package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_admin_dashboard);

        // Gắn click listener cho các chức năng
        setupFunction(R.id.funcProducts, AdminProductActivity.class);
        setupFunction(R.id.funcCategories, CategoryActivity.class);
        setupFunction(R.id.funcOrders, AdminOrderActivity.class);
        setupFunction(R.id.funcStatistics, StatisticActivity.class);
        setupFunction(R.id.funcUsers, UserManagementActivity.class);
        setupFunction(R.id.funcLowStock, LowStockActivity.class);
        setupFunction(R.id.funcVouchers, VoucherManagementActivity.class);
        setupFunction(R.id.funcSwitchUser, UserHomeActivity.class);

        // Chức năng đăng xuất
        View funcLogout = findViewById(R.id.funcLogout);
        funcLogout.setOnClickListener(v -> {
            com.example.quanlycuahangthoitrang.utils.SessionManager session = new com.example.quanlycuahangthoitrang.utils.SessionManager(this);
            session.logout();
            // Quay về LoginActivity
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    // Thiết lập chức năng cho từng nút bấm trên giao diện
    private void setupFunction(int viewId, Class<?> targetActivity) {
        // Tìm view theo ID
        View view = findViewById(viewId);
        if (view != null) {
            // Khi click vào view thì chuyển hướng sang màn hình tương ứng
            view.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, targetActivity)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Ánh xạ view từ XML sang Java
        android.widget.TextView tvStatProducts = findViewById(R.id.tvStatProducts);
        // Ánh xạ view từ XML sang Java
        android.widget.TextView tvStatOrders = findViewById(R.id.tvStatOrders);
        // Ánh xạ view từ XML sang Java
        android.widget.TextView tvStatRevenue = findViewById(R.id.tvStatRevenue);
        // Ánh xạ view từ XML sang Java
        android.widget.TextView tvStatLowStock = findViewById(R.id.tvStatLowStock);

        com.example.quanlycuahangthoitrang.database.DatabaseHelper dbHelper = new com.example.quanlycuahangthoitrang.database.DatabaseHelper(this);

        // Đếm tổng số sản phẩm trong hệ thống
        int totalProducts = dbHelper.getAllProducts().size();
        
        // BƯỚC 1: TÍNH TỔNG SỐ ĐƠN HÀNG
        // Chỉ tính đơn do Khách tự đặt (UserOrder)
        int totalOrders = dbHelper.getUserOrderCount();
        
        // BƯỚC 2: TÍNH DOANH THU
        // Chỉ tính doanh thu online (Order)
        int totalRevenue = dbHelper.getUserOrderRevenue();
        
        // BƯỚC 3: KIỂM TRA SẢN PHẨM SẮP HẾT HÀNG
        // Tham số '5' nghĩa là: Lấy danh sách các sản phẩm có số lượng Tồn kho <= 5
        int lowStock = dbHelper.getLowStockProducts(5).size();

        // Gắn số liệu vừa tính được lên màn hình giao diện
        if (tvStatProducts != null) tvStatProducts.setText(String.valueOf(totalProducts));
        if (tvStatOrders != null) tvStatOrders.setText(String.valueOf(totalOrders));
        if (tvStatRevenue != null) tvStatRevenue.setText(com.example.quanlycuahangthoitrang.utils.FormatUtils.formatPrice(totalRevenue));
        if (tvStatLowStock != null) tvStatLowStock.setText(String.valueOf(lowStock));
    }
}
