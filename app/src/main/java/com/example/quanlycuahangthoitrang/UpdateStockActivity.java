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
    
    private java.util.List<android.widget.EditText> variantInputs = new java.util.ArrayList<>();
    private java.util.List<String[]> variantCombos = new java.util.ArrayList<>();

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
        
        android.widget.LinearLayout llVariantContainer = findViewById(R.id.llVariantContainer);
        
        if (product.getColor() != null && !product.getColor().isEmpty() && product.getSizes() != null && !product.getSizes().isEmpty()) {
            edtNewStock.setVisibility(android.view.View.GONE);
            llVariantContainer.setVisibility(android.view.View.VISIBLE);
            
            String[] colors = product.getColor().split(",");
            String[] sizes = product.getSizes().split(",");
            
            for (String c : colors) {
                String color = c.trim();
                for (String s : sizes) {
                    String size = s.trim();
                    if (color.isEmpty() || size.isEmpty()) continue;
                    
                    android.widget.TextView tvLabel = new android.widget.TextView(this);
                    tvLabel.setText("Màu " + color + " - Size " + size);
                    tvLabel.setPadding(0, 24, 0, 8);
                    tvLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                    
                    android.widget.EditText input = new android.widget.EditText(this);
                    input.setHint("Nhập số lượng...");
                    input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                    input.setBackgroundResource(R.drawable.bg_input);
                    input.setPadding(32, 24, 32, 24);
                    
                    int currentStock = dbHelper.getVariantStock(product.getId(), color, size);
                    if (currentStock == -1) currentStock = 0;
                    input.setText(String.valueOf(currentStock));
                    
                    llVariantContainer.addView(tvLabel);
                    llVariantContainer.addView(input);
                    
                    variantInputs.add(input);
                    variantCombos.add(new String[]{color, size});
                }
            }
        }

        // Bắt sự kiện bấm nút [CẬP NHẬT]
        findViewById(R.id.btnUpdate).setOnClickListener(v -> {
            if (variantInputs.isEmpty()) {
                // Lấy số lượng mới mà Admin vừa gõ vào
                String newStockStr = ((EditText) findViewById(R.id.edtNewStock)).getText().toString().trim();
                
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
            } else {
                int totalStock = 0;
                boolean valid = true;
                for (int i = 0; i < variantInputs.size(); i++) {
                    String val = variantInputs.get(i).getText().toString().trim();
                    if (val.isEmpty()) val = "0";
                    try {
                        int st = Integer.parseInt(val);
                        if (st < 0) throw new NumberFormatException();
                        totalStock += st;
                    } catch (Exception e) {
                        Toast.makeText(this, "Số lượng tại " + variantCombos.get(i)[0] + "-" + variantCombos.get(i)[1] + " không hợp lệ!", Toast.LENGTH_SHORT).show();
                        valid = false;
                        break;
                    }
                }
                
                if (!valid) return;
                
                // Cập nhật tổng
                product.setStock(totalStock);
                if (dbHelper.updateProduct(product)) {
                    // Cập nhật biến thể
                    for (int i = 0; i < variantInputs.size(); i++) {
                        String val = variantInputs.get(i).getText().toString().trim();
                        if (val.isEmpty()) val = "0";
                        int st = Integer.parseInt(val);
                        dbHelper.updateVariantStock(product.getId(), variantCombos.get(i)[0], variantCombos.get(i)[1], st);
                    }
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
