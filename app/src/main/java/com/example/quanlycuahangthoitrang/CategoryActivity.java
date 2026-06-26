package com.example.quanlycuahangthoitrang;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Category;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private CategoryAdapter adapter;
    private RecyclerView rvCategories;
    private EditText edtSearchCategory;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_category);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnAddCategory).setOnClickListener(v -> {
            // Chuyển sang màn hình tương ứng
            startActivity(new Intent(CategoryActivity.this, AddCategoryActivity.class));
        });

        // Ánh xạ view từ XML sang Java
        edtSearchCategory = findViewById(R.id.edtSearchCategory);
        // Ánh xạ view từ XML sang Java
        rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CategoryAdapter(dbHelper.getAllCategories(), dbHelper, new CategoryAdapter.OnCategoryInteractionListener() {
            @Override
            public void onEdit(Category category) {
                Intent intent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
                intent.putExtra("category_id", category.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(Category category) {
                if (dbHelper.getProductCountByCategory(category.getName()) > 0) {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(CategoryActivity.this, "Không thể xóa danh mục đang có sản phẩm", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(CategoryActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa danh mục này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            dbHelper.deleteCategory(category.getId());
                            adapter.updateData(dbHelper.searchCategories(edtSearchCategory.getText().toString()));
                            // Hiện thông báo (Toast) cho người dùng
                            Toast.makeText(CategoryActivity.this, "Đã xóa danh mục", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        rvCategories.setAdapter(adapter);

        edtSearchCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.updateData(dbHelper.searchCategories(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.updateData(dbHelper.searchCategories(edtSearchCategory.getText().toString()));
        }
    }
}
