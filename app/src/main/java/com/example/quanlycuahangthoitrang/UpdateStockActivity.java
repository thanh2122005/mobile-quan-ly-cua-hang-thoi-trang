package com.example.quanlycuahangthoitrang;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Product;

public class UpdateStockActivity extends AppCompatActivity {
    
    private DatabaseHelper dbHelper;
    private int productId = -1;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_stock);

        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvProductCode = findViewById(R.id.tvProductCode);
        TextView tvCurrentStock = findViewById(R.id.tvCurrentStock);
        EditText edtNewStock = findViewById(R.id.edtNewStock);

        productId = getIntent().getIntExtra("product_id", -1);
        product = dbHelper.getProductById(productId);

        if (product != null) {
            tvProductName.setText(product.getName());
            tvProductCode.setText("Mã SP: SP" + String.format("%03d", product.getId()));
            tvCurrentStock.setText("Tồn kho hiện tại: " + product.getStock());
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        findViewById(R.id.btnUpdate).setOnClickListener(v -> {
            String newStockStr = edtNewStock.getText().toString().trim();
            if (newStockStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng mới", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int newStock = Integer.parseInt(newStockStr);
                if (newStock < 0) {
                    Toast.makeText(this, "Số lượng không được âm", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                product.setStock(newStock);
                if (dbHelper.updateProduct(product)) {
                    Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
