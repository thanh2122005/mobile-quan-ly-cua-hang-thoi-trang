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

        categoryId = getIntent().getIntExtra("category_id", -1);
        if (categoryId != -1) {
            isEditMode = true;
            tvTitleCategory.setText("Sửa danh mục");
            btnSave.setText("Cập nhật danh mục");

            Category c = dbHelper.getCategoryById(categoryId);
            if (c != null) {
                edtCategoryName.setText(c.getName());
                oldCategoryName = c.getName();
            }
        }

        btnSave.setOnClickListener(v -> {
            String name = edtCategoryName.getText().toString().trim();
            if (name.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                return;
            }

            for (Category c : dbHelper.getAllCategories()) {
                String existingNormalized = com.example.quanlycuahangthoitrang.utils.FormatUtils.normalizeSearchText(c.getName());
                String newNormalized = com.example.quanlycuahangthoitrang.utils.FormatUtils.normalizeSearchText(name);
                if (existingNormalized.equals(newNormalized) && c.getId() != categoryId) {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(this, "Danh mục đã tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (isEditMode) {
                Category c = new Category(categoryId, name, "📁");
                if (dbHelper.updateCategory(c)) {
                    // Update all products holding oldCategoryName to name
                    if (!oldCategoryName.equals(name) && !oldCategoryName.isEmpty()) {
                        List<Product> affectedProducts = dbHelper.getProductsByCategory(oldCategoryName);
                        for (Product p : affectedProducts) {
                            p.setCategory(name);
                            dbHelper.updateProduct(p);
                        }
                    }
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(this, "Cập nhật danh mục thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(this, "Lỗi khi cập nhật danh mục", Toast.LENGTH_SHORT).show();
                }
            } else {
                Category c = new Category(0, name, "📁");
                if (dbHelper.insertCategory(c)) {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(this, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(this, "Lỗi khi thêm danh mục", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
