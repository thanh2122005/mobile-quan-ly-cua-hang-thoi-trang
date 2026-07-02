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
import com.example.quanlycuahangthoitrang.model.Product;
//tạo
public class AdminProductActivity extends AppCompatActivity {

    private AdminProductAdapter adapter;
    private RecyclerView rvAdminProducts;
    private EditText edtSearch;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Khởi tạo giao diện Màn hình Quản lý Sản phẩm (Góc nhìn của Admin)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);

        // Khởi tạo công cụ giao tiếp với Database (SQLite)
        dbHelper = new DatabaseHelper(this);

        // Nút mũi tên quay lại góc trái trên cùng
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Bắt sự kiện bấm nút hình dấu Cộng (+) màu đỏ ở góc dưới để Thêm sản phẩm mới
        findViewById(R.id.btnAddProduct).setOnClickListener(v -> {
            // Dùng Intent chuyển Admin sang trang Nhập liệu thêm mới sản phẩm
            startActivity(new Intent(AdminProductActivity.this, AddProductActivity.class));
        });

        // Tìm thanh tìm kiếm (Ô nhập chữ trên cùng)
        edtSearch = findViewById(R.id.edtSearch);
        // Tìm Danh sách cuộn (RecyclerView) chứa các sản phẩm
        rvAdminProducts = findViewById(R.id.rvAdminProducts);
        // Cài đặt danh sách cuộn theo chiều dọc từ trên xuống dưới
        rvAdminProducts.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter (Bộ chuyển đổi) để đổ toàn bộ dữ liệu Sản phẩm từ Database vào Danh sách
        adapter = new AdminProductAdapter(dbHelper.getAllProducts(), new AdminProductAdapter.OnProductInteractionListener() {
            
            // Xử lý sự kiện: Khi Admin bấm vào nút [SỬA] hình cây bút
            @Override
            public void onEdit(Product product) {
                // Mở trang EditProductActivity
                Intent intent = new Intent(AdminProductActivity.this, EditProductActivity.class);
                // Đính kèm ID của sản phẩm cần sửa để trang kia biết đường mà móc dữ liệu ra
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            // Xử lý sự kiện: Khi Admin bấm vào nút [XÓA] hình thùng rác
            @Override
            public void onDelete(Product product) {
                // Hiển thị một cái Cảnh báo (Dialog) hỏi lại xem có chắc chắn xóa không
                new AlertDialog.Builder(AdminProductActivity.this)
                        .setTitle("Xóa sản phẩm")
                        .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            // Nếu bấm Xóa -> Gọi hàm Xóa thẳng trong CSDL theo ID
                            dbHelper.deleteProduct(product.getId());
                            // Cập nhật lại giao diện ngay lập tức bằng cách lấy lại danh sách mới nhất (vẫn giữ nguyên bộ lọc tìm kiếm hiện tại)
                            adapter.updateData(dbHelper.searchProducts(edtSearch.getText().toString()));
                            Toast.makeText(AdminProductActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        })
                        // Nếu bấm Hủy -> Đóng cảnh báo, không làm gì cả
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        
        // Gắn Bộ chuyển đổi vào Danh sách giao diện
        rvAdminProducts.setAdapter(adapter);

        // Bắt sự kiện Lắng nghe gõ phím vào ô Tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            // Cứ mỗi lần Admin gõ 1 chữ cái mới vào ô tìm kiếm là hàm này tự chạy
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Lấy chữ vừa gõ (s.toString()) quăng vào hàm tìm kiếm của CSDL, ra kết quả xong thì update luôn giao diện
                adapter.updateData(dbHelper.searchProducts(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Hàm này tự động chạy mỗi khi Admin từ trang khác quay về trang này (VD: Vừa Sửa hoặc Thêm sản phẩm xong)
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            // Tự động làm mới lại toàn bộ danh sách để thấy được dữ liệu vừa Thêm/Sửa
            adapter.updateData(dbHelper.searchProducts(edtSearch.getText().toString()));
        }
    }
}
