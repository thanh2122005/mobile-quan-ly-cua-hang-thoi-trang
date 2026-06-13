package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Gắn click listener cho các chức năng
        setupFunction(R.id.funcProducts, AdminProductActivity.class);
        setupFunction(R.id.funcCategories, CategoryActivity.class);
        setupFunction(R.id.funcCreateInvoice, CreateInvoiceActivity.class);
        setupFunction(R.id.funcInvoiceHistory, InvoiceHistoryActivity.class);
        setupFunction(R.id.funcStatistics, StatisticActivity.class);
        setupFunction(R.id.funcUsers, UserManagementActivity.class);
        setupFunction(R.id.funcLowStock, LowStockActivity.class);

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

    private void setupFunction(int viewId, Class<?> activityClass) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(v -> {
                startActivity(new Intent(AdminDashboardActivity.this, activityClass));
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        android.widget.TextView tvStatProducts = findViewById(R.id.tvStatProducts);
        android.widget.TextView tvStatOrders = findViewById(R.id.tvStatOrders);
        android.widget.TextView tvStatRevenue = findViewById(R.id.tvStatRevenue);
        android.widget.TextView tvStatLowStock = findViewById(R.id.tvStatLowStock);

        com.example.quanlycuahangthoitrang.database.DatabaseHelper dbHelper = new com.example.quanlycuahangthoitrang.database.DatabaseHelper(this);

        int totalProducts = dbHelper.getAllProducts().size();
        int totalOrders = dbHelper.getInvoiceCount();
        int totalRevenue = dbHelper.getTotalRevenue();
        int lowStock = dbHelper.getLowStockProducts(3).size();

        if (tvStatProducts != null) tvStatProducts.setText(String.valueOf(totalProducts));
        if (tvStatOrders != null) tvStatOrders.setText(String.valueOf(totalOrders));
        if (tvStatRevenue != null) tvStatRevenue.setText(com.example.quanlycuahangthoitrang.utils.FormatUtils.formatPrice(totalRevenue));
        if (tvStatLowStock != null) tvStatLowStock.setText(String.valueOf(lowStock));
    }
}
