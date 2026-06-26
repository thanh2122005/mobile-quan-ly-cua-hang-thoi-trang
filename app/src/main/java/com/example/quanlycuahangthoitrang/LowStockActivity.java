package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Product;

import java.util.List;

public class LowStockActivity extends AppCompatActivity {

    private LowStockAdapter adapter;
    private RecyclerView rvLowStock;
    private TextView tvLowStockSummary;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_low_stock);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Ánh xạ view từ XML sang Java
        rvLowStock = findViewById(R.id.rvLowStock);
        // Ánh xạ view từ XML sang Java
        tvLowStockSummary = findViewById(R.id.tvLowStockSummary);
        
        rvLowStock.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LowStockAdapter(dbHelper.getLowStockProducts(3), new LowStockAdapter.OnProductClickListener() {
            @Override
            public void onUpdateClick(Product product) {
                Intent intent = new Intent(LowStockActivity.this, UpdateStockActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }
        });
        rvLowStock.setAdapter(adapter);

        updateSummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.updateData(dbHelper.getLowStockProducts(3));
            updateSummary();
        }
    }

    private void updateSummary() {
        int count = dbHelper.getLowStockProducts(3).size();
        tvLowStockSummary.setText("Có " + count + " sản phẩm dưới mức tồn kho (<= 3).");
    }
}
