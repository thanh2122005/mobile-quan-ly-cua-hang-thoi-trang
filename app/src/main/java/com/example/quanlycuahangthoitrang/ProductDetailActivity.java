package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Product;
import com.example.quanlycuahangthoitrang.model.User;

import com.example.quanlycuahangthoitrang.utils.FormatUtils;
import com.example.quanlycuahangthoitrang.utils.SessionManager;

public class ProductDetailActivity extends AppCompatActivity {

    private int quantity = 1;
    private Product currentProduct;
    private TextView tvQuantity;
    private DatabaseHelper dbHelper;
    private String selectedColor = "";
    private String selectedSize = "";
    private int currentStockLimit = 0;
    private android.widget.LinearLayout llColors;
    private android.widget.LinearLayout llSizes;
    private SessionManager sessionManager;
    private TextView tvCartBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);
        // Khởi tạo trình quản lý phiên đăng nhập
        sessionManager = new SessionManager(this);

        // Lấy thông tin ID sản phẩm được gửi từ màn hình trước (Trang chủ) sang
        int productId = getIntent().getIntExtra("product_id", -1);
        if (productId != -1) {
            // Truy vấn toàn bộ thông tin chi tiết của sản phẩm từ Database
            currentProduct = dbHelper.getProductById(productId);
        }

        if (currentProduct == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các giao diện để chuẩn bị đổ dữ liệu lên
        androidx.viewpager2.widget.ViewPager2 vpProductImage = findViewById(R.id.vpProductImage);
        android.widget.LinearLayout llIndicators = findViewById(R.id.llIndicators);
        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvProductPrice = findViewById(R.id.tvProductPrice);
        TextView tvProductDesc = findViewById(R.id.tvProductDesc);
        
        tvQuantity = findViewById(R.id.tvQuantity);
        llColors = findViewById(R.id.llColors);
        llSizes = findViewById(R.id.llSizes);
        tvCartBadge = findViewById(R.id.tvCartBadge);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Bắt sự kiện bấm nút giỏ hàng
        findViewById(R.id.btnCartTop).setOnClickListener(v -> {
            startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
        });

        // Đổ danh sách Màu sắc ra màn hình để người dùng chọn
        populateColors();
        
        // Kiểm tra logic danh mục: Nếu là Phụ kiện, Túi, Kính thì không cần chọn Size
        if (currentProduct.getCategory().equalsIgnoreCase("Phụ kiện") || 
            currentProduct.getCategory().equalsIgnoreCase("Túi") || 
            currentProduct.getCategory().equalsIgnoreCase("Kính")) {
            // Ẩn vùng chọn Size đi
            findViewById(R.id.tvSizeLabel).setVisibility(android.view.View.GONE);
            llSizes.setVisibility(android.view.View.GONE);
            // Gán cứng size là Freesize để lưu vào giỏ hàng khỏi bị lỗi null
            selectedSize = "Freesize";
        } else {
            // Các đồ như Quần, Áo, Giày thì đổ danh sách Size bình thường
            populateSizes();
        }

        ProductGalleryAdapter adapter = new ProductGalleryAdapter(currentProduct.getImages());
        vpProductImage.setAdapter(adapter);
        
        setupIndicators(llIndicators, adapter.getItemCount());
        vpProductImage.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(llIndicators, position);
            }
        });

        tvProductName.setText(currentProduct.getName());
        tvProductPrice.setText(FormatUtils.formatPrice(currentProduct.getPrice()));
        tvProductDesc.setText(currentProduct.getDescription());

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Ánh xạ view từ XML sang Java
        TextView btnAddToCart = findViewById(R.id.btnAddToCart);
        // Ánh xạ view từ XML sang Java
        TextView btnBuyNow = findViewById(R.id.btnBuyNow);

        currentStockLimit = currentProduct.getStock();

        if (currentStockLimit <= 0) {
            quantity = 0;
            tvQuantity.setText("0");
            btnAddToCart.setText("Sản phẩm hết hàng");
            btnBuyNow.setText("Sản phẩm hết hàng");
            btnAddToCart.setEnabled(false);
            btnBuyNow.setEnabled(false);
            findViewById(R.id.btnPlus).setEnabled(false);
            findViewById(R.id.btnMinus).setEnabled(false);
        } else {
            quantity = 1;
            tvQuantity.setText("1");
        }

        findViewById(R.id.btnMinus).setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        findViewById(R.id.btnPlus).setOnClickListener(v -> {
            if (quantity < currentStockLimit) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Đã đạt số lượng tồn kho tối đa", Toast.LENGTH_SHORT).show();
            }
        });

        // ----------------------------------------------------
        // XỬ LÝ NÚT [THÊM VÀO GIỎ HÀNG]
        // ----------------------------------------------------
        btnAddToCart.setOnClickListener(v -> {
            if (currentStockLimit <= 0) {
                Toast.makeText(this, "Phân loại này đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Lấy ID người dùng đang đăng nhập
            int userId = getCurrentUserId();
            if (userId <= 0) {
                Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Bắt buộc phải chọn Màu sắc
            if (selectedColor.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn màu sắc", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Bắt buộc phải chọn Kích cỡ (Trừ khi là đồ phụ kiện Freesize)
            if (selectedSize.isEmpty()) {
                if (currentProduct.getCategory().equalsIgnoreCase("Phụ kiện") || currentProduct.getCategory().equalsIgnoreCase("Túi") || currentProduct.getCategory().equalsIgnoreCase("Kính")) {
                    selectedSize = "Freesize"; // Gán bù để tránh lỗi lưu Database
                } else {
                    Toast.makeText(this, "Vui lòng chọn kích cỡ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            
            // Tiến hành Lưu món hàng này vào Database Giỏ Hàng
            if (dbHelper.addToCart(userId, currentProduct.getId(), quantity, selectedColor, selectedSize)) {
                Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                updateCartBadge();
            } else {
                Toast.makeText(this, "Lỗi thêm giỏ hàng. Có thể vượt quá tồn kho.", Toast.LENGTH_SHORT).show();
            }
        });


        btnBuyNow.setOnClickListener(v -> {
            if (currentStockLimit <= 0) return;
            int userId = getCurrentUserId();
            if (userId <= 0) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedColor.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng chọn màu sắc", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedSize.isEmpty()) {
                if (currentProduct.getCategory().equalsIgnoreCase("Phụ kiện") || currentProduct.getCategory().equalsIgnoreCase("Túi") || currentProduct.getCategory().equalsIgnoreCase("Kính")) {
                    selectedSize = "Freesize";
                } else {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(this, "Vui lòng chọn kích cỡ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (dbHelper.addToCart(userId, currentProduct.getId(), quantity, selectedColor, selectedSize)) {
                Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
                java.util.ArrayList<String> selectedList = new java.util.ArrayList<>();
                selectedList.add(currentProduct.getId() + "_" + selectedColor + "_" + selectedSize);
                intent.putStringArrayListExtra("selected_items", selectedList);
                startActivity(intent);
            } else {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });

        loadReviews();
    }

    private void loadReviews() {
        java.util.List<com.example.quanlycuahangthoitrang.model.Review> reviews = dbHelper.getProductReviews(currentProduct.getId());
        // Ánh xạ view từ XML sang Java
        TextView tvRatingAverage = findViewById(R.id.tvRatingAverage);
        // Ánh xạ view từ XML sang Java
        android.widget.LinearLayout llReviews = findViewById(R.id.llReviews);
        // Ánh xạ view từ XML sang Java
        TextView tvEmptyReviews = findViewById(R.id.tvEmptyReviews);

        llReviews.removeAllViews();

        if (reviews.isEmpty()) {
            tvRatingAverage.setText("⭐ 0.0 (0)");
            tvEmptyReviews.setVisibility(android.view.View.VISIBLE);
            return;
        }

        tvEmptyReviews.setVisibility(android.view.View.GONE);

        int totalStars = 0;
        for (com.example.quanlycuahangthoitrang.model.Review r : reviews) {
            totalStars += r.getRating();
            
            android.view.View reviewView = getLayoutInflater().inflate(R.layout.item_review, llReviews, false);
            TextView tvReviewerName = reviewView.findViewById(R.id.tvReviewerName);
            TextView tvReviewDate = reviewView.findViewById(R.id.tvReviewDate);
            android.widget.RatingBar rbReviewStars = reviewView.findViewById(R.id.rbReviewStars);
            TextView tvReviewComment = reviewView.findViewById(R.id.tvReviewComment);

            android.database.Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT name FROM users WHERE id=?", new String[]{String.valueOf(r.getUserId())});
            if (cursor.moveToFirst()) {
                tvReviewerName.setText(cursor.getString(0));
            } else {
                tvReviewerName.setText("Khách hàng");
            }
            cursor.close();
            
            tvReviewDate.setText(r.getCreatedAt());
            rbReviewStars.setRating(r.getRating());
            tvReviewComment.setText(r.getComment());

            llReviews.addView(reviewView);
        }

        float avgRating = (float) totalStars / reviews.size();
        tvRatingAverage.setText(String.format("⭐ %.1f (%d)", avgRating, reviews.size()));
    }

    private void populateColors() {
        if (currentProduct.getColor() == null || currentProduct.getColor().isEmpty()) return;
        String[] colors = currentProduct.getColor().split(",");
        for (String color : colors) {
            String c = color.trim();
            if (c.isEmpty()) continue;
            TextView tv = createChip(c);
            tv.setOnClickListener(v -> {
                selectedColor = c;
                updateChips(llColors, tv);
                updateVariantStockUI();
            });
            llColors.addView(tv);
        }
    }

    private void populateSizes() {
        if (currentProduct.getSizes() == null || currentProduct.getSizes().isEmpty()) return;
        String[] sizes = currentProduct.getSizes().split(",");
        for (String size : sizes) {
            String s = size.trim();
            if (s.isEmpty()) continue;
            TextView tv = createChip(s);
            tv.setOnClickListener(v -> {
                selectedSize = s;
                updateChips(llSizes, tv);
                updateVariantStockUI();
            });
            llSizes.addView(tv);
        }
    }

    private TextView createChip(String text) {
        TextView tv = new TextView(this);
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 16, 0);
        tv.setLayoutParams(params);
        tv.setBackgroundResource(R.drawable.bg_chip_normal);
        tv.setPadding(32, 16, 32, 16);
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.text_secondary));
        return tv;
    }

    private void updateChips(android.widget.LinearLayout parent, TextView selectedTv) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            TextView tv = (TextView) parent.getChildAt(i);
            tv.setBackgroundResource(R.drawable.bg_chip_normal);
            tv.setTextColor(getResources().getColor(R.color.text_secondary));
        }
        selectedTv.setBackgroundResource(R.drawable.bg_chip_selected);
        selectedTv.setTextColor(getResources().getColor(R.color.white));
    }

    private void setupIndicators(android.widget.LinearLayout parent, int count) {
        parent.removeAllViews();
        for (int i = 0; i < count; i++) {
            android.view.View indicator = new android.view.View(this);
            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            indicator.setLayoutParams(params);
            indicator.setBackgroundResource(R.drawable.indicator_inactive);
            parent.addView(indicator);
        }
        if (count > 0) updateIndicators(parent, 0);
    }

    private void updateIndicators(android.widget.LinearLayout parent, int position) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            android.view.View indicator = parent.getChildAt(i);
            if (i == position) {
                indicator.setBackgroundResource(R.drawable.indicator_active);
            } else {
                indicator.setBackgroundResource(R.drawable.indicator_inactive);
            }
        }
    }

    private int getCurrentUserId() {
        User currentUser = dbHelper.getUserByEmail(sessionManager.getEmail());
        return currentUser != null ? currentUser.getId() : -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    public void updateCartBadge() {
        if (tvCartBadge != null) {
            String email = sessionManager.getEmail();
            if (email != null && !email.isEmpty()) {
                User u = dbHelper.getUserByEmail(email);
                if (u != null) {
                    java.util.List<com.example.quanlycuahangthoitrang.model.CartItem> cartItems = dbHelper.getCartItems(u.getId());
                    int count = cartItems.size();
                    if (count > 0) {
                        tvCartBadge.setText(String.valueOf(count));
                        tvCartBadge.setVisibility(android.view.View.VISIBLE);
                    } else {
                        tvCartBadge.setVisibility(android.view.View.GONE);
                    }
                }
            } else {
                tvCartBadge.setVisibility(android.view.View.GONE);
            }
        }
    }

    // Cập nhật giao diện tồn kho khi khách chọn màu/size
    private void updateVariantStockUI() {
        if (!selectedColor.isEmpty() && !selectedSize.isEmpty()) {
            int variantStock = dbHelper.getVariantStock(currentProduct.getId(), selectedColor, selectedSize);
            currentStockLimit = variantStock;
            
            TextView btnAddToCart = findViewById(R.id.btnAddToCart);
            TextView btnBuyNow = findViewById(R.id.btnBuyNow);
            
            if (currentStockLimit <= 0) {
                btnAddToCart.setText("Hết hàng (Màu/Size này)");
                btnBuyNow.setText("Hết hàng");
                btnAddToCart.setEnabled(false);
                btnBuyNow.setEnabled(false);
                quantity = 0;
                tvQuantity.setText("0");
            } else {
                btnAddToCart.setText("Thêm vào giỏ hàng");
                btnBuyNow.setText("Mua ngay");
                btnAddToCart.setEnabled(true);
                btnBuyNow.setEnabled(true);
                if (quantity > currentStockLimit) {
                    quantity = currentStockLimit;
                }
                if (quantity <= 0) quantity = 1;
                tvQuantity.setText(String.valueOf(quantity));
            }
        }
    }
}
