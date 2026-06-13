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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_stock);

        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvLowStock = findViewById(R.id.rvLowStock);
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
