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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        TextView tvTitleCategory = findViewById(R.id.tvTitleCategory);
        EditText edtCategoryName = findViewById(R.id.edtCategoryName);
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
                Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                return;
            }

            for (Category c : dbHelper.getAllCategories()) {
                String existingNormalized = com.example.quanlycuahangthoitrang.utils.FormatUtils.normalizeSearchText(c.getName());
                String newNormalized = com.example.quanlycuahangthoitrang.utils.FormatUtils.normalizeSearchText(name);
                if (existingNormalized.equals(newNormalized) && c.getId() != categoryId) {
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
                    Toast.makeText(this, "Cập nhật danh mục thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi khi cập nhật danh mục", Toast.LENGTH_SHORT).show();
                }
            } else {
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
