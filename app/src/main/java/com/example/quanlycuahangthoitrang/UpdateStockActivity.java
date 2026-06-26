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
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_update_stock);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        // Ánh xạ view từ XML sang Java
        TextView tvProductName = findViewById(R.id.tvProductName);
        // Ánh xạ view từ XML sang Java
        TextView tvProductCode = findViewById(R.id.tvProductCode);
        // Ánh xạ view từ XML sang Java
        TextView tvCurrentStock = findViewById(R.id.tvCurrentStock);
        // Ánh xạ view từ XML sang Java
        EditText edtNewStock = findViewById(R.id.edtNewStock);

        productId = getIntent().getIntExtra("product_id", -1);
        product = dbHelper.getProductById(productId);

        if (product != null) {
            tvProductName.setText(product.getName());
            tvProductCode.setText("Mã SP: SP" + String.format("%03d", product.getId()));
            tvCurrentStock.setText("Tồn kho hiện tại: " + product.getStock());
        } else {
            // Hiện thông báo (Toast) cho người dùng
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        findViewById(R.id.btnUpdate).setOnClickListener(v -> {
            String newStockStr = edtNewStock.getText().toString().trim();
            if (newStockStr.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng nhập số lượng mới", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int newStock = Integer.parseInt(newStockStr);
                if (newStock < 0) {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(this, "Số lượng không được âm", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                product.setStock(newStock);
                if (dbHelper.updateProduct(product)) {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
