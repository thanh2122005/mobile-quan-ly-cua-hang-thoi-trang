package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Product;
import com.example.quanlycuahangthoitrang.model.User;
import com.example.quanlycuahangthoitrang.utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private ProductUserAdapter adapter;
    private List<Product> currentList;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_product_list);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);
        // Khởi tạo trình quản lý phiên đăng nhập
        sessionManager = new SessionManager(this);

        // Lấy tên Danh mục được truyền từ trang chủ sang (Ví dụ: "Áo", "Quần")
        String categoryExtra = getIntent().getStringExtra("category");
        final String category = categoryExtra == null ? "Tất cả" : categoryExtra;

        TextView tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        
        // Logic lọc sản phẩm:
        // Nếu người dùng chọn "Tất cả" -> Gọi hàm getAllProducts lấy toàn bộ kho
        // Nếu người dùng chọn danh mục cụ thể -> Gọi getProductsByCategory để lọc riêng
        if (category.equals("Tất cả")) {
            tvCategoryTitle.setText("Tất cả sản phẩm");
            currentList = dbHelper.getAllProducts();
        } else {
            tvCategoryTitle.setText("Sản phẩm: " + category);
            currentList = dbHelper.getProductsByCategory(category);
        }

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Ánh xạ view từ XML sang Java
        RecyclerView rvProductsList = findViewById(R.id.rvProductsList);
        rvProductsList.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductUserAdapter(currentList, new ProductUserAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onAddToCartClick(Product product) {
                int userId = getCurrentUserId();
                if (userId <= 0) {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(ProductListActivity.this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
                    return;
                }

                String defaultColor = "";
                String defaultSize = "";
                if (product.getColor() != null && !product.getColor().isEmpty()) {
                    defaultColor = product.getColor().split(",")[0].trim();
                }
                if (product.getSizes() != null && !product.getSizes().isEmpty()) {
                    defaultSize = product.getSizes().split(",")[0].trim();
                }
                if (dbHelper.addToCart(userId, product.getId(), 1, defaultColor, defaultSize)) {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(ProductListActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(ProductListActivity.this, "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rvProductsList.setAdapter(adapter);

        // Search logic
        android.widget.EditText edtSearch = findViewById(R.id.edtSearch);
        
        // Setup initial search if passed from intent
        String searchQuery = getIntent().getStringExtra("search_query");
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            edtSearch.setText(searchQuery);
            filterProducts(searchQuery);
        }

        edtSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Sorting
        findViewById(R.id.chipSortNew).setOnClickListener(v -> {
            if (category.equals("Tất cả")) {
                currentList = dbHelper.getAllProducts();
            } else {
                currentList = dbHelper.getProductsByCategory(category);
            }
            adapter.setProducts(currentList);
            updateChipStyles(R.id.chipSortNew);
        });

        findViewById(R.id.chipSortLow).setOnClickListener(v -> {
            Collections.sort(currentList, Comparator.comparingInt(Product::getPrice));
            adapter.notifyDataSetChanged();
            updateChipStyles(R.id.chipSortLow);
        });

        findViewById(R.id.chipSortHigh).setOnClickListener(v -> {
            Collections.sort(currentList, (p1, p2) -> Integer.compare(p2.getPrice(), p1.getPrice()));
            adapter.notifyDataSetChanged();
            updateChipStyles(R.id.chipSortHigh);
        });
    }

    private void updateChipStyles(int selectedId) {
        // Ánh xạ view từ XML sang Java
        TextView chipSortNew = findViewById(R.id.chipSortNew);
        // Ánh xạ view từ XML sang Java
        TextView chipSortLow = findViewById(R.id.chipSortLow);
        // Ánh xạ view từ XML sang Java
        TextView chipSortHigh = findViewById(R.id.chipSortHigh);

        chipSortNew.setBackgroundResource(R.drawable.bg_chip_normal);
        chipSortNew.setTextColor(getResources().getColor(R.color.text_secondary));
        
        chipSortLow.setBackgroundResource(R.drawable.bg_chip_normal);
        chipSortLow.setTextColor(getResources().getColor(R.color.text_secondary));
        
        chipSortHigh.setBackgroundResource(R.drawable.bg_chip_normal);
        chipSortHigh.setTextColor(getResources().getColor(R.color.text_secondary));

        TextView selected = findViewById(selectedId);
        selected.setBackgroundResource(R.drawable.bg_chip_selected);
        selected.setTextColor(getResources().getColor(R.color.white));
    }

    private void filterProducts(String query) {
        String lowerQuery = query.toLowerCase().trim();
        List<Product> filteredList = new ArrayList<>();
        
        List<Product> baseList;
        String category = getIntent().getStringExtra("category");
        if (category == null || category.equals("Tất cả")) {
            baseList = dbHelper.getAllProducts();
        } else {
            baseList = dbHelper.getProductsByCategory(category);
        }

        for (Product p : baseList) {
            if (p.getName().toLowerCase().contains(lowerQuery)) {
                filteredList.add(p);
            }
        }
        currentList = filteredList;
        adapter.setProducts(filteredList);
    }

    private int getCurrentUserId() {
        User currentUser = dbHelper.getUserByEmail(sessionManager.getEmail());
        return currentUser != null ? currentUser.getId() : -1;
    }
}
