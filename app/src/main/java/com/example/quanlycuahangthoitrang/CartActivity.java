package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.CartItem;
import com.example.quanlycuahangthoitrang.model.User;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;
import com.example.quanlycuahangthoitrang.utils.SessionManager;


public class CartActivity extends AppCompatActivity {

    // CART ACTIVITY - MÀN HÌNH QUẢN LÝ GIỎ HÀNG

    private CartAdapter adapter;
    private TextView tvTotalPrice;
    private LinearLayout layoutEmptyCart;
    private RecyclerView rvCartItems;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private java.util.Set<String> unselectedKeys = new java.util.HashSet<>();

    // Hàm tạo "Mã riêng biệt" cho từng dòng trong Giỏ hàng.
    // Vì một sản phẩm (ví dụ: Áo phông) có thể được mua 2 lần: 1 cái Đỏ Size M, 1 cái Xanh Size L.
    // Nên phải ghép ID + Màu + Size lại với nhau để tạo thành "chìa khóa" phân biệt.
    private String getItemKey(CartItem item) {
        return item.getProduct().getId() + "_" + item.getSelectedColor() + "_" + item.getSelectedSize();
    }

    // Hàm chạy đầu tiên khi mở màn hình Giỏ hàng
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện giỏ hàng
        setContentView(R.layout.activity_cart);

        // Khởi tạo công cụ kết nối cơ sở dữ liệu và quản lý phiên đăng nhập
        dbHelper = new DatabaseHelper(this);
        // Khởi tạo trình quản lý phiên đăng nhập
        sessionManager = new SessionManager(this);

        // Ánh xạ các thành phần trên giao diện
        tvTotalPrice = findViewById(R.id.tvTotalPrice); // Ô hiển thị Tổng tiền
        // Ánh xạ view từ XML sang Java
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart); // Màn hình báo "Giỏ hàng trống"
        // Ánh xạ view từ XML sang Java
        rvCartItems = findViewById(R.id.rvCartItems); // Danh sách cuộn chứa các mặt hàng

        // Nút quay lại màn hình trước
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Cài đặt cho danh sách cuộn hiển thị theo chiều dọc
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        
        // Khởi tạo Bộ điều hợp (Adapter) để đổ dữ liệu từ Database lên danh sách cuộn
        // Adapter luôn lấy dữ liệu mới nhất từ CSDL thông qua dbHelper.getCartItems()
        adapter = new CartAdapter(dbHelper.getCartItems(getCurrentUserId()), new CartAdapter.OnCartItemInteractionListener() {
            // Bắt sự kiện người dùng bấm nút [CỘNG]
            @Override
            public void onIncrease(CartItem item) {
                // Cập nhật tăng số lượng lên 1 đơn vị vào cơ sở dữ liệu
                boolean updated = dbHelper.updateCartQuantity(getCurrentUserId(), item.getProduct().getId(), item.getQuantity() + 1, item.getSelectedColor(), item.getSelectedSize());
                if (updated) {
                    refreshCart(); // Tải lại danh sách nếu thành công
                } else {
                    // Báo lỗi nếu đã đạt tối đa số lượng tồn kho
                    Toast.makeText(CartActivity.this, "Tổng số lượng của sản phẩm đã đạt giới hạn tồn kho", Toast.LENGTH_SHORT).show();
                }
            }

            // Bắt sự kiện người dùng bấm nút [TRỪ]
            @Override
            public void onDecrease(CartItem item) {
                // Không cho phép trừ nếu số lượng đang là 1
                if (item.getQuantity() <= 1) {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(CartActivity.this, "Số lượng tối thiểu là 1", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Giảm 1 đơn vị trong cơ sở dữ liệu
                boolean updated = dbHelper.updateCartQuantity(getCurrentUserId(), item.getProduct().getId(), item.getQuantity() - 1, item.getSelectedColor(), item.getSelectedSize());
                if (updated) {
                    refreshCart();
                } else {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(CartActivity.this, "Không thể cập nhật số lượng sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            // Bắt sự kiện người dùng bấm nút [XÓA] (Thùng rác)
            @Override
            public void onRemove(CartItem item) {
                // Xóa mặt hàng này khỏi Database
                dbHelper.removeCartItem(getCurrentUserId(), item.getProduct().getId(), item.getSelectedColor(), item.getSelectedSize());
                // Xóa luôn "chìa khóa" của nó khỏi danh sách không chọn
                unselectedKeys.remove(getItemKey(item));
                refreshCart(); // Làm mới lại giỏ hàng
            }

            // Bắt sự kiện bấm vào nút [SỬA THUỘC TÍNH]
            @Override
            public void onEditVariant(CartItem item) {
                // Hiển thị hộp thoại (Dialog) cho phép đổi Màu và Size
                showEditVariantDialog(item);
            }

            // Bắt sự kiện [TÍCH CHỌN] hoặc [BỎ CHỌN] thanh toán
            @Override
            public void onSelectionChanged(CartItem item, boolean isChecked) {
                if (isChecked) {
                    // Nếu Tích chọn -> Loại bỏ khỏi danh sách "Từ chối thanh toán"
                    unselectedKeys.remove(getItemKey(item));
                } else {
                    // Nếu Bỏ chọn -> Thêm vào danh sách "Từ chối thanh toán"
                    unselectedKeys.add(getItemKey(item));
                }
                // Tính toán lại Tổng tiền cần thanh toán
                updateTotalPrice();
            }
        });
        
        // Gắn Adapter vào danh sách hiển thị
        rvCartItems.setAdapter(adapter);

        // Xử lý nút [TIẾP TỤC MUA SẮM] -> Quay lại Trang chủ
        findViewById(R.id.btnContinueShopping).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, UserHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Xử lý nút [MUA HÀNG] -> Chuyển sang màn hình Thanh toán (Checkout)
        findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            java.util.ArrayList<String> selectedList = new java.util.ArrayList<>();
            
            // Quét qua toàn bộ giỏ hàng, chỉ lấy những mặt hàng đang được "Tích chọn"
            for (CartItem item : dbHelper.getCartItems(getCurrentUserId())) {
                if (!unselectedKeys.contains(getItemKey(item))) {
                    selectedList.add(getItemKey(item)); // Thêm "chìa khóa" vào danh sách chờ thanh toán
                }
            }

            // Nếu không chọn món nào thì báo lỗi, không cho sang trang thanh toán
            if (selectedList.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            } else {
                // Đóng gói danh sách các món được chọn và gửi sang màn hình CheckoutActivity
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putStringArrayListExtra("selected_items", selectedList);
                startActivity(intent);
            }
        });

        // Tải toàn bộ dữ liệu giỏ hàng hiển thị lên màn hình lần đầu
        refreshCart();
    }

    // Hàm Tính tổng tiền của các món hàng đang được khách hàng TÍCH CHỌN trong Giỏ hàng
    private void updateTotalPrice() {
        int total = 0; // Biến lưu trữ tổng tiền tạm tính ban đầu là 0

        // Lấy danh sách toàn bộ các món hàng đang có trong Giỏ từ Bộ điều hợp (Adapter)
        java.util.List<CartItem> items = adapter.getItems();

        // Vòng lặp: Duyệt qua từng món hàng một
        for (CartItem item : items) {
            // Lấy "chìa khóa" đặc trưng của món hàng này (Ghép từ ID_MÀU_SIZE)
            String key = getItemKey(item);

            // Kiểm tra: Nếu món hàng này KHÔNG NẰM TRONG danh sách "Bị bỏ chọn" (unselectedKeys)
            // Tức là món hàng này đang được khách hàng Tích xanh (Chọn để mua)
            if (!unselectedKeys.contains(key)) {
                
                // Lấy Giá tiền của 1 sản phẩm nhân với Số lượng khách muốn mua
                int itemTotal = item.getProduct().getPrice() * item.getQuantity();
                
                // Cộng dồn vào Tổng tiền
                total += itemTotal;
            }
        }

        // Sau khi đã cộng dồn xong, hiển thị số tiền đó lên màn hình và định dạng cho đẹp (Ví dụ: 100.000đ)
        tvTotalPrice.setText(FormatUtils.formatPrice(total));
    }

    // Hàm tải lại giỏ hàng (Sau khi Thêm, Sửa, Xóa thì gọi hàm này để giao diện cập nhật)
    private void refreshCart() {
        // Lấy danh sách hàng hóa từ Database
        java.util.List<CartItem> items = dbHelper.getCartItems(getCurrentUserId());
        
        // Cập nhật trạng thái "Tích chọn" (Checkbox) cho từng món
        for (CartItem item : items) {
            item.setSelected(!unselectedKeys.contains(getItemKey(item)));
        }
        
        // Bơm danh sách mới vào Adapter để vẽ lên màn hình
        adapter.updateData(items);
        
        // Nhớ tính lại Tổng tiền
        updateTotalPrice();

        // Kiểm tra xem giỏ hàng có trống không
        if (items.isEmpty()) {
            // Nếu trống: Hiện thông báo "Giỏ hàng trống" và ẩn danh sách cuộn
            layoutEmptyCart.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.GONE);
        } else {
            // Nếu có hàng: Hiện danh sách cuộn và ẩn thông báo
            layoutEmptyCart.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.VISIBLE);
        }
    }

    // Các biến tạm thời để lưu giá trị Màu sắc và Kích thước khi người dùng chọn trong Dialog Sửa
    private String tempSelectedColor = "";
    private String tempSelectedSize = "";

    // Hàm bật Hộp thoại (Dialog) để thay đổi Thuộc tính mặt hàng (VD: Đổi áo Đỏ thành áo Đen)
    // Nếu đổi sang thuộc tính đã tồn tại trong giỏ, CSDL sẽ tự động cộng dồn số lượng.
    private void showEditVariantDialog(CartItem cartItem) {
        // Tạo một hộp thoại trống mờ màn hình
        android.app.Dialog dialog = new android.app.Dialog(this);
        // Nạp giao diện từ file XML
        dialog.setContentView(R.layout.dialog_edit_variant); // Gắn giao diện vào hộp thoại
        
        // Điều chỉnh cho hộp thoại hiển thị ngang toàn màn hình và tự động cao theo nội dung
        dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tvProductName = dialog.findViewById(R.id.tvProductName);
        TextView tvProductPrice = dialog.findViewById(R.id.tvProductPrice);
        android.widget.ImageView ivProductImage = dialog.findViewById(R.id.ivProductImage);
        LinearLayout llColors = dialog.findViewById(R.id.llColors);
        LinearLayout llSizes = dialog.findViewById(R.id.llSizes);
        TextView tvSizeLabel = dialog.findViewById(R.id.tvSizeLabel);
        android.widget.HorizontalScrollView svSizes = dialog.findViewById(R.id.svSizes);

        com.example.quanlycuahangthoitrang.model.Product product = cartItem.getProduct();
        tvProductName.setText(product.getName());
        tvProductPrice.setText(FormatUtils.formatPrice(product.getPrice()));
        com.example.quanlycuahangthoitrang.utils.ImageLoader.load(ivProductImage, product.getMainImage());

        tempSelectedColor = cartItem.getSelectedColor();
        tempSelectedSize = cartItem.getSelectedSize();

        // ----------------------------------------------------
        // XỬ LÝ MÀU SẮC (COLORS)
        // ----------------------------------------------------
        if (product.getColor() != null && !product.getColor().isEmpty()) {
            // Tách chuỗi các màu sắc bằng dấu phẩy (VD: "Đỏ, Xanh, Đen" -> ["Đỏ", "Xanh", "Đen"])
            String[] colors = product.getColor().split(",");
            for (String color : colors) {
                String c = color.trim();
                if (c.isEmpty()) continue; // Bỏ qua nếu có khoảng trắng dư thừa
                
                // Hàm createChip() dùng để tạo ra một ô chữ nhật bo góc nhỏ chứa tên Màu
                TextView tv = createChip(c);
                
                // Nếu đây là màu người dùng đang chọn, tô nền màu cam cho nó
                if (c.equals(tempSelectedColor)) {
                    tv.setBackgroundResource(R.drawable.bg_chip_selected);
                    tv.setTextColor(getResources().getColor(R.color.white));
                }
                
                // Sự kiện khi người dùng bấm vào một màu khác
                tv.setOnClickListener(v -> {
                    tempSelectedColor = c; // Lưu lại màu mới
                    updateChips(llColors, tv); // Hàm này giúp làm mờ các nút khác và làm sáng nút vừa bấm
                });
                
                // Thêm ô vuông màu sắc này vào màn hình (vào danh sách các màu)
                llColors.addView(tv);
            }
        }

        // ----------------------------------------------------
        // XỬ LÝ KÍCH THƯỚC (SIZES)
        // ----------------------------------------------------
        // Đặc biệt: Phụ kiện, Túi, Kính thì không có Size, ẩn phần chọn Size đi
        if (product.getCategory().equalsIgnoreCase("Phụ kiện") || 
            product.getCategory().equalsIgnoreCase("Túi") || 
            product.getCategory().equalsIgnoreCase("Kính")) {
            tvSizeLabel.setVisibility(View.GONE);
            svSizes.setVisibility(View.GONE);
            tempSelectedSize = "Freesize"; // Tự động set là Freesize
        } else {
            // Đối với Quần/Áo/Giày: Lấy danh sách size và vẽ ra tương tự như vẽ Màu sắc
            if (product.getSizes() != null && !product.getSizes().isEmpty()) {
                String[] sizes = product.getSizes().split(",");
                for (String size : sizes) {
                    String s = size.trim();
                    if (s.isEmpty()) continue;
                    TextView tv = createChip(s);
                    if (s.equals(tempSelectedSize)) {
                        tv.setBackgroundResource(R.drawable.bg_chip_selected);
                        tv.setTextColor(getResources().getColor(R.color.white));
                    }
                    tv.setOnClickListener(v -> {
                        tempSelectedSize = s;
                        updateChips(llSizes, tv);
                    });
                    llSizes.addView(tv);
                }
            }
        }

        // Nút HỦY - Đóng hộp thoại không làm gì cả
        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        
        // Nút CẬP NHẬT - Lưu lại thay đổi
        dialog.findViewById(R.id.btnUpdate).setOnClickListener(v -> {
            // Kiểm tra xem đã chọn màu chưa
            if (tempSelectedColor.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng chọn màu sắc", Toast.LENGTH_SHORT).show();
                return;
            }
            // Kiểm tra xem đã chọn size chưa
            if (tempSelectedSize.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng chọn kích cỡ", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Nếu người dùng không hề đổi màu và đổi size mà bấm Cập nhật thì đóng hộp thoại luôn
            if (tempSelectedColor.equals(cartItem.getSelectedColor()) && tempSelectedSize.equals(cartItem.getSelectedSize())) {
                dialog.dismiss();
                return;
            }
            
            // Chạy lệnh thay đổi trong CSDL: Thay MÀU/SIZE cũ thành MÀU/SIZE mới
            // Hàm updateCartItemVariant sẽ tự xử lý nếu Màu/Size mới bị trùng với món khác thì nó gộp chung lại
            if (dbHelper.updateCartItemVariant(getCurrentUserId(), product.getId(), cartItem.getSelectedColor(), cartItem.getSelectedSize(), tempSelectedColor, tempSelectedSize)) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Đã cập nhật tùy chọn", Toast.LENGTH_SHORT).show();
                refreshCart(); // Tải lại giỏ hàng
                dialog.dismiss(); // Đóng hộp thoại
            } else {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private TextView createChip(String text) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 16, 0);
        tv.setLayoutParams(params);
        tv.setText(text);
        tv.setPadding(32, 16, 32, 16);
        tv.setBackgroundResource(R.drawable.bg_chip_unselected);
        tv.setTextColor(getResources().getColor(R.color.text_secondary));
        return tv;
    }

    // Chỉ phục vụ UI: highlight chip đang chọn trong dialog biến thể.
    private void updateChips(LinearLayout layout, TextView selectedTv) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            TextView tv = (TextView) layout.getChildAt(i);
            tv.setBackgroundResource(R.drawable.bg_chip_unselected);
            tv.setTextColor(getResources().getColor(R.color.text_secondary));
        }
        selectedTv.setBackgroundResource(R.drawable.bg_chip_selected);
        selectedTv.setTextColor(getResources().getColor(R.color.white));
    }

    // Resolve user hiện tại từ session trước khi thao tác cart/checkout.
    private int getCurrentUserId() {
        User currentUser = dbHelper.getUserByEmail(sessionManager.getEmail());
        return currentUser != null ? currentUser.getId() : -1;
    }
}
