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
        // Hàm chạy đầu tiên khi mở màn hình Quản lý Danh mục (Chỉ Admin mới được vào)
        super.onCreate(savedInstanceState);
        
        // Nạp giao diện từ file activity_category.xml
        setContentView(R.layout.activity_category);

        // Khởi tạo công cụ giao tiếp với Database (SQLite)
        dbHelper = new DatabaseHelper(this);

        // Bắt sự kiện bấm nút mũi tên Quay lại -> Đóng màn hình này
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Bắt sự kiện bấm nút Thêm Danh Mục mới (Hình dấu +)
        findViewById(R.id.btnAddCategory).setOnClickListener(v -> {
            // Chuyển sang màn hình Nhập liệu Thêm mới danh mục
            startActivity(new Intent(CategoryActivity.this, AddCategoryActivity.class));
        });

        // Tìm thanh tìm kiếm trên màn hình
        edtSearchCategory = findViewById(R.id.edtSearchCategory);
        
        // Tìm Danh sách cuộn (RecyclerView) chứa các Danh mục
        rvCategories = findViewById(R.id.rvCategories);
        // Đặt hướng cuộn là theo chiều dọc
        rvCategories.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter (Bộ chuyển đổi) để đưa dữ liệu từ Database lên Giao diện
        // Truyền kèm theo toàn bộ danh sách Danh mục lấy từ dbHelper.getAllCategories()
        adapter = new CategoryAdapter(dbHelper.getAllCategories(), dbHelper, new CategoryAdapter.OnCategoryInteractionListener() {
            
            // Xử lý sự kiện: Admin bấm vào nút [SỬA] hình cây bút
            @Override
            public void onEdit(Category category) {
                // Mở lại trang AddCategoryActivity nhưng dùng để Sửa (Bằng cách truyền ID danh mục sang)
                Intent intent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
                intent.putExtra("category_id", category.getId());
                startActivity(intent);
            }

            // Xử lý sự kiện: Admin bấm vào nút [XÓA] hình thùng rác
            @Override
            public void onDelete(Category category) {
                // RÀO CẢN BẢO MẬT DỮ LIỆU: Kiểm tra xem trong Danh mục này có đang chứa sản phẩm nào không
                if (dbHelper.getProductCountByCategory(category.getName()) > 0) {
                    // Nếu đang có Áo/Quần bên trong thì KHÔNG CHO XÓA (Tránh lỗi mất dữ liệu mồ côi)
                    Toast.makeText(CategoryActivity.this, "Không thể xóa danh mục đang có sản phẩm", Toast.LENGTH_SHORT).show();
                    return; // Dừng lại ngay lập tức
                }
                
                // Nếu danh mục trống -> Hiện hộp thoại hỏi lại cho chắc chắn
                new AlertDialog.Builder(CategoryActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa danh mục này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            // Gọi lệnh Xóa thẳng trong CSDL
                            dbHelper.deleteCategory(category.getId());
                            // Cập nhật lại danh sách hiển thị trên màn hình
                            adapter.updateData(dbHelper.searchCategories(edtSearchCategory.getText().toString()));
                            Toast.makeText(CategoryActivity.this, "Đã xóa danh mục", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", null) // Không làm gì cả
                        .show();
            }
        });
        
        // Gắn Adapter vào danh sách
        rvCategories.setAdapter(adapter);

        // Tính năng TÌM KIẾM NHANH bằng cách gõ phím
        edtSearchCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            // Mỗi khi Admin gõ 1 chữ cái (Ví dụ: gõ chữ "Á", rồi "o") -> Hàm này tự kích hoạt
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Chuyển cụm từ vừa gõ (s.toString()) vào Database để tìm kiếm và trả về danh sách lọc
                adapter.updateData(dbHelper.searchCategories(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Hàm tự động chạy khi Admin đi từ màn hình khác quay về màn hình này
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            // Tải lại dữ liệu mới nhất (Trường hợp Admin vừa Thêm/Sửa danh mục)
            adapter.updateData(dbHelper.searchCategories(edtSearchCategory.getText().toString()));
        }
    }
}
