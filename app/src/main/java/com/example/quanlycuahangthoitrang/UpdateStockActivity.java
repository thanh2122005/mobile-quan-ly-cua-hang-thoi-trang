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

        // Bước 1: Lấy ID của sản phẩm cần nhập hàng do màn hình trước gửi sang
        productId = getIntent().getIntExtra("product_id", -1);
        
        // Bước 2: Dùng ID chọc xuống Database để lấy ra đầy đủ thông tin (Tên, Ảnh, Tồn kho cũ...)
        product = dbHelper.getProductById(productId);

        if (product != null) {
            // Bước 3: Đổ thông tin cũ lên giao diện để Admin nhìn thấy
            tvProductName.setText(product.getName());
            // Mã sản phẩm được làm đẹp bằng cách thêm số 0 đằng trước (Ví dụ: ID=5 -> "SP005")
            tvProductCode.setText("Mã SP: SP" + String.format("%03d", product.getId()));
            tvCurrentStock.setText("Tồn kho hiện tại: " + product.getStock());
        } else {
            // Lỗi không tìm thấy sản phẩm -> Thoát luôn
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bắt sự kiện bấm nút [CẬP NHẬT]
        findViewById(R.id.btnUpdate).setOnClickListener(v -> {
            // Lấy số lượng mới mà Admin vừa gõ vào
            String newStockStr = edtNewStock.getText().toString().trim();
            
            // RÀO CẢN 1: Không được để trống
            if (newStockStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng mới", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                // RÀO CẢN 2: Ép kiểu chuỗi sang số nguyên
                // Nếu Admin cố tình gõ chữ (Ví dụ "abc") thì vòng try-catch sẽ ném thẳng xuống catch bên dưới
                int newStock = Integer.parseInt(newStockStr);
                
                // RÀO CẢN 3: Không cho nhập số âm
                if (newStock < 0) {
                    Toast.makeText(this, "Số lượng không được âm", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Cập nhật lại số lượng tồn kho mới vào biến product
                product.setStock(newStock);
                
                // BƯỚC CUỐI: Đưa product mới lưu đè xuống Database
                if (dbHelper.updateProduct(product)) {
                    Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    // Đóng màn hình, tự động quay về trang Cảnh báo hết hàng
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
