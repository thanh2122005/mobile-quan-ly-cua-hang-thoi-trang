package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.CartItem;
import com.example.quanlycuahangthoitrang.model.Product;
import com.example.quanlycuahangthoitrang.model.User;
import com.example.quanlycuahangthoitrang.utils.SessionManager;

import java.util.List;


public class UserHomeActivity extends AppCompatActivity {

    private TextView tvCartBadge;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_user_home);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);
        // Khởi tạo trình quản lý phiên đăng nhập
        sessionManager = new SessionManager(this);
        // Ánh xạ view từ XML sang Java
        tvCartBadge = findViewById(R.id.tvCartBadge);

        // Navigation
        setupNavigation();

        // Banner & View More
        findViewById(R.id.btnExplore).setOnClickListener(v -> openProductList("Tất cả"));
        findViewById(R.id.tvViewMore).setOnClickListener(v -> openProductList("Tất cả"));

        // Chips
        findViewById(R.id.chipCategory1).setOnClickListener(v -> openProductList("Tất cả"));
        findViewById(R.id.chipCategory2).setOnClickListener(v -> openProductList("Áo"));
        findViewById(R.id.chipCategory3).setOnClickListener(v -> openProductList("Quần"));
        findViewById(R.id.chipCategory4).setOnClickListener(v -> openProductList("Giày"));
        findViewById(R.id.chipCategory5).setOnClickListener(v -> openProductList("Phụ kiện"));

        // Tìm danh sách hiển thị sản phẩm trên giao diện
        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        
        // Tạo giao diện dạng lưới (Grid) với 2 cột (Giống Shopee)
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                // Tắt tính năng cuộn riêng của danh sách để dùng chung cuộn của toàn màn hình
                return false; 
            }
        };
        // Áp dụng giao diện lưới vào danh sách
        rvProducts.setLayoutManager(layoutManager);
        
        // Lấy TOÀN BỘ sản phẩm từ cơ sở dữ liệu lên
        List<Product> allProducts = dbHelper.getAllProducts();
        // Lọc lấy 6 sản phẩm đầu tiên để hiển thị ở trang chủ cho đỡ nặng
        List<Product> displayProducts = allProducts.size() > 6 ? allProducts.subList(0, 6) : allProducts;
        
        ProductUserAdapter adapter = new ProductUserAdapter(displayProducts, new ProductUserAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(UserHomeActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            // Xử lý sự kiện bấm nút [Thêm vào giỏ] NHANH ngay trên trang chủ
            @Override
            public void onAddToCartClick(Product product) {
                int userId = getCurrentUserId();
                if (userId <= 0) {
                    Toast.makeText(UserHomeActivity.this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Vì là Thêm Nhanh không mở Trang Chi Tiết, nên hệ thống sẽ Tự Động Lấy Màu/Size đầu tiên
                String defaultColor = "";
                String defaultSize = "";
                
                // Cắt chuỗi màu bằng dấu phẩy và lấy phần tử số 0 (Màu đầu tiên)
                if (product.getColor() != null && !product.getColor().isEmpty()) {
                    defaultColor = product.getColor().split(",")[0].trim();
                }
                
                // Cắt chuỗi Size bằng dấu phẩy và lấy phần tử số 0 (Size đầu tiên)
                if (product.getSizes() != null && !product.getSizes().isEmpty()) {
                    defaultSize = product.getSizes().split(",")[0].trim();
                }
                
                // Gọi DB để nhét vào Giỏ, mặc định số lượng = 1
                if (dbHelper.addToCart(userId, product.getId(), 1, defaultColor, defaultSize)) {
                    Toast.makeText(UserHomeActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    updateCartBadge(); // Cập nhật lại cái chấm đỏ hiển thị số món trên Giỏ
                } else {
                    Toast.makeText(UserHomeActivity.this, "Lỗi thêm giỏ hàng. Có thể hết hàng.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rvProducts.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void updateCartBadge() {
        if (tvCartBadge != null) {
            int userId = getCurrentUserId();
            java.util.List<CartItem> cartItems = dbHelper.getCartItems(userId);
            int total = cartItems.size();
            
            if (total > 0) {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadge.setText(String.valueOf(total));
            } else {
                tvCartBadge.setVisibility(View.GONE);
            }
        }
    }

    private int getCurrentUserId() {
        User currentUser = dbHelper.getUserByEmail(sessionManager.getEmail());
        return currentUser != null ? currentUser.getId() : -1;
    }

    private void openProductList(String category) {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void setupNavigation() {
        // Ánh xạ view từ XML sang Java
        View navCart = findViewById(R.id.navCart);
        // Ánh xạ view từ XML sang Java
        View navOrders = findViewById(R.id.navOrders);
        // Ánh xạ view từ XML sang Java
        View navAccount = findViewById(R.id.navAccount);

        // Search logic
        android.widget.EditText edtHomeSearch = findViewById(R.id.edtHomeSearch);
        edtHomeSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.getAction() == android.view.KeyEvent.ACTION_DOWN && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) {
                String query = edtHomeSearch.getText().toString().trim();
                Intent intent = new Intent(UserHomeActivity.this, ProductListActivity.class);
                intent.putExtra("search_query", query);
                startActivity(intent);
                return true;
            }
            return false;
        });

        if (navCart != null) {
            // Chuyển sang màn hình tương ứng
            navCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        }

        if (navOrders != null) {
            // Chuyển sang màn hình tương ứng
            navOrders.setOnClickListener(v -> startActivity(new Intent(this, UserOrderHistoryActivity.class)));
        }

        if (navAccount != null) {
            // Chuyển sang màn hình tương ứng
            navAccount.setOnClickListener(v -> startActivity(new Intent(this, UserProfileActivity.class)));
        }

        // Ánh xạ view từ XML sang Java
        View btnCartTop = findViewById(R.id.btnCartTop);
        if (btnCartTop != null) {
            // Chuyển sang màn hình tương ứng
            btnCartTop.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        }
    }
}
