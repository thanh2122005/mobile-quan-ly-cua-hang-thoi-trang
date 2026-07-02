package com.example.quanlycuahangthoitrang;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Category;
import com.example.quanlycuahangthoitrang.model.Product;

import java.util.List;

public class AddCategoryActivity extends AppCompatActivity {

    private boolean isEditMode = false;
    private int categoryId = -1;
    private DatabaseHelper dbHelper;
    private String oldCategoryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_add_category);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        // Ánh xạ view từ XML sang Java
        TextView tvTitleCategory = findViewById(R.id.tvTitleCategory);
        // Ánh xạ view từ XML sang Java
        EditText edtCategoryName = findViewById(R.id.edtCategoryName);
        // Ánh xạ view từ XML sang Java
        TextView btnSave = findViewById(R.id.btnSave);

        // Kiểm tra xem Màn hình này được mở lên để THÊM MỚI hay để SỬA
        // Cách kiểm tra: Thử lấy "category_id" do màn hình trước gửi sang. Nếu không có thì sẽ trả về -1 (Mặc định)
        categoryId = getIntent().getIntExtra("category_id", -1);
        if (categoryId != -1) {
            // Nếu có ID (Khác -1) -> Chuyển sang CHẾ ĐỘ SỬA (Edit Mode)
            isEditMode = true;
            // Đổi tiêu đề và tên nút bấm cho phù hợp
            tvTitleCategory.setText("Sửa danh mục");
            btnSave.setText("Cập nhật danh mục");

            // Móc thông tin danh mục cũ từ Database lên để điền sẵn vào ô nhập
            Category c = dbHelper.getCategoryById(categoryId);
            if (c != null) {
                edtCategoryName.setText(c.getName());
                // Lưu lại Tên Cũ để lát nữa dùng cập nhật lại tên cho các sản phẩm đang dùng danh mục này
                oldCategoryName = c.getName();
            }
        }

        // Bắt sự kiện bấm nút [LƯU] (Dùng chung cho cả Thêm và Sửa)
        btnSave.setOnClickListener(v -> {
            String name = edtCategoryName.getText().toString().trim();
            // RÀO CẢN 1: Bắt buộc nhập
            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                return;
            }

            // RÀO CẢN 2: CHỐNG TRÙNG LẶP TÊN DANH MỤC
            // Duyệt qua tất cả các danh mục đang có trong hệ thống
            for (Category c : dbHelper.getAllCategories()) {
                // Loại bỏ dấu, viết thường hết để so sánh cho chuẩn (VD: "Áo sơ mi" và "ao so mi" là giống nhau)
                String existingNormalized = com.example.quanlycuahangthoitrang.utils.FormatUtils.normalizeSearchText(c.getName());
                String newNormalized = com.example.quanlycuahangthoitrang.utils.FormatUtils.normalizeSearchText(name);
                
                // Nếu Tên mới bị trùng với Tên cũ VÀ KHÔNG PHẢI là chính bản thân danh mục đang sửa
                if (existingNormalized.equals(newNormalized) && c.getId() != categoryId) {
                    Toast.makeText(this, "Danh mục đã tồn tại", Toast.LENGTH_SHORT).show();
                    return; // Chặn lại, không cho lưu
                }
            }

            // --- BẮT ĐẦU LƯU XUỐNG DATABASE ---
            if (isEditMode) {
                // TRƯỜNG HỢP: LƯU SỬA (UPDATE)
                Category c = new Category(categoryId, name, "📁");
                if (dbHelper.updateCategory(c)) {
                    
                    // THUẬT TOÁN ĐỒNG BỘ HÓA DỮ LIỆU (CASCADING UPDATE)
                    // Nếu Admin đổi tên "Quần" -> "Quần Nam"
                    // Thì tất cả sản phẩm đang mang danh mục "Quần" cũng phải được tự động đổi thành "Quần Nam"
                    if (!oldCategoryName.equals(name) && !oldCategoryName.isEmpty()) {
                        // Kéo tất cả sản phẩm thuộc danh mục cũ lên
                        List<Product> affectedProducts = dbHelper.getProductsByCategory(oldCategoryName);
                        for (Product p : affectedProducts) {
                            // Cập nhật lại tên danh mục mới cho từng cái áo/quần
                            p.setCategory(name);
                            dbHelper.updateProduct(p); // Lưu lại xuống CSDL
                        }
                    }
                    
                    Toast.makeText(this, "Cập nhật danh mục thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi khi cập nhật danh mục", Toast.LENGTH_SHORT).show();
                }
            } else {
                // TRƯỜNG HỢP: LƯU THÊM MỚI (INSERT)
                Category c = new Category(0, name, "📁");
                if (dbHelper.insertCategory(c)) {
                    Toast.makeText(this, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi khi thêm danh mục", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
