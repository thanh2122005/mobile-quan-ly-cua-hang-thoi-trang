package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.CartItem;
import com.example.quanlycuahangthoitrang.model.User;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;
import com.example.quanlycuahangthoitrang.utils.SessionManager;

public class CheckoutActivity extends AppCompatActivity {

    // CHECKOUT ACTIVITY - MÀN HÌNH THANH TOÁN

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int subtotal = 0;
    private int shippingFee = 30000;
    private int discountAmount = 0;
    
    private com.example.quanlycuahangthoitrang.model.Voucher selectedFreeshipVoucher = null;
    private com.example.quanlycuahangthoitrang.model.Voucher selectedDiscountVoucher = null;

    private TextView tvOrderTotal;
    private TextView tvShippingFee;
    private TextView tvDiscountAmount;
    private TextView tvSelectedVoucher;
    
    private java.util.List<CartItem> checkoutItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_checkout);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);
        // Khởi tạo trình quản lý phiên đăng nhập
        sessionManager = new SessionManager(this);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Ánh xạ view từ XML sang Java
        TextView tvTotalQuantity = findViewById(R.id.tvTotalQuantity);
        // Ánh xạ view từ XML sang Java
        TextView tvTotalPriceSummary = findViewById(R.id.tvTotalPriceSummary);
        // Ánh xạ view từ XML sang Java
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        // Ánh xạ view từ XML sang Java
        tvShippingFee = findViewById(R.id.tvShippingFee);
        // Ánh xạ view từ XML sang Java
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        // Ánh xạ view từ XML sang Java
        tvSelectedVoucher = findViewById(R.id.tvSelectedVoucher);

        // Lấy danh sách các "chìa khóa" (ItemKey) mà người dùng đã đánh dấu Tích chọn bên màn Giỏ hàng
        java.util.ArrayList<String> selectedItemKeys = getIntent().getStringArrayListExtra("selected_items");
        checkoutItems = new java.util.ArrayList<>();
        
        int currentUserId = getCurrentUserId();
        // Kiểm tra bảo mật: Nếu chưa đăng nhập thì đuổi ra ngoài
        if (currentUserId <= 0) {
            // Hiện thông báo (Toast) cho người dùng
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        int totalQty = 0; // Biến đếm tổng số lượng áo/quần
        subtotal = 0; // Biến đếm tổng tiền hàng (chưa có Ship)
        
        // Quét toàn bộ Giỏ hàng của người dùng này trong CSDL
        for (CartItem item : dbHelper.getCartItems(currentUserId)) {
            // Tái tạo lại "chìa khóa" (ID + Màu + Size) để đối chiếu
            String key = item.getProduct().getId() + "_" + item.getSelectedColor() + "_" + item.getSelectedSize();
            
            // Nếu chìa khóa này có nằm trong danh sách gửi sang từ trang trước -> Bắt đầu tính tiền
            if (selectedItemKeys == null || selectedItemKeys.contains(key)) {
                checkoutItems.add(item); // Đưa vào danh sách chờ Thanh toán
                totalQty += item.getQuantity(); // Cộng dồn số cái
                subtotal += (item.getQuantity() * item.getProduct().getPrice()); // Cộng dồn tiền hàng
            }
        }

        tvTotalQuantity.setText("Tạm tính (" + totalQty + " sản phẩm)");
        tvTotalPriceSummary.setText(FormatUtils.formatPrice(subtotal));

        updateTotalUI();

        findViewById(R.id.btnSelectVoucher).setOnClickListener(v -> showVoucherDialog());

        // Ánh xạ view từ XML sang Java
        EditText edtName = findViewById(R.id.edtName);
        // Ánh xạ view từ XML sang Java
        EditText edtPhone = findViewById(R.id.edtPhone);
        // Ánh xạ view từ XML sang Java
        EditText edtAddress = findViewById(R.id.edtAddress);
        // Ánh xạ view từ XML sang Java
        EditText edtNote = findViewById(R.id.edtNote);

        // -------------------------------------------------------------------
        // TỰ ĐỘNG ĐIỀN THÔNG TIN GIAO HÀNG
        // -------------------------------------------------------------------
        // Lấy thông tin cá nhân (Tên, SĐT, Địa chỉ) từ tài khoản đang đăng nhập
        String email = sessionManager.getEmail();
        User currentUser = dbHelper.getUserByEmail(email);
        if (currentUser != null) {
            // Điền sẵn vào các ô chữ trên màn hình để người dùng đỡ phải gõ lại
            edtName.setText(currentUser.getName());
            edtPhone.setText(currentUser.getPhone());
            edtAddress.setText(currentUser.getAddress());
        }

        // -------------------------------------------------------------------
        // SỰ KIỆN KHI BẤM NÚT [ĐẶT HÀNG]
        // -------------------------------------------------------------------
        findViewById(R.id.btnConfirmOrder).setOnClickListener(v -> {
            // Kiểm tra an toàn: Không có hàng thì không cho đặt
            if (checkoutItems.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Không có sản phẩm nào để thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra Tồn kho trước khi cho đi tiếp
            for (CartItem item : checkoutItems) {
                if (item.getQuantity() > item.getProduct().getStock()) {
                    // Hiện thông báo (Toast) cho người dùng
                    Toast.makeText(this, "Sản phẩm " + item.getProduct().getName() + " không đủ số lượng (Còn " + item.getProduct().getStock() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String note = edtNote != null ? edtNote.getText().toString().trim() : "";

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // -------------------------------------------------------------------
            // BƯỚC TÍNH TIỀN VÀ GHI CHÚ
            // -------------------------------------------------------------------
            int baseShippingFee = 30000; // Cố định phí ship là 30k
            
            // Tính số tiền được trừ nhờ mã Miễn Phí Vận Chuyển (Tối đa là bằng phí ship)
            int shippingDiscountAmount = selectedFreeshipVoucher != null ? Math.min(baseShippingFee, selectedFreeshipVoucher.getValue()) : 0;
            
            // Tạo chuỗi Ghi chú lưu vết lại lịch sử dùng Voucher để người quản lý đọc
            StringBuilder finalNote = new StringBuilder();
            if (!note.isEmpty()) {
                finalNote.append("Ghi chú: ").append(note).append("\n"); // Ghi chú của khách
            }
            finalNote.append("[Phí ship gốc: ").append(FormatUtils.formatPrice(baseShippingFee)).append("]");
            
            // Nếu dùng Voucher Freeship -> Ghi vào nhật ký
            if (selectedFreeshipVoucher != null) {
                finalNote.append(" [Mã freeship: ").append(selectedFreeshipVoucher.getCode()).append(" - Giảm: ").append(FormatUtils.formatPrice(shippingDiscountAmount)).append("]");
            }
            // Nếu dùng Voucher Giảm Giá -> Ghi vào nhật ký
            if (selectedDiscountVoucher != null) {
                finalNote.append(" [Mã giảm giá: ").append(selectedDiscountVoucher.getCode()).append(" - Giảm: ").append(FormatUtils.formatPrice(discountAmount)).append("]");
            }

            // Tính TỔNG TIỀN CUỐI CÙNG = Tiền hàng + Phí ship - Tiền trừ Freeship - Tiền trừ Giảm giá
            int finalTotal = subtotal + baseShippingFee - shippingDiscountAmount - discountAmount;
            if (finalTotal < 0) finalTotal = 0; // Đảm bảo tiền không bao giờ bị âm
            
            // -------------------------------------------------------------------
            // GỌI DATABASE ĐỂ TẠO ĐƠN HÀNG MỚI CHÍNH THỨC
            // -------------------------------------------------------------------
            // Lệnh createOrder() này sẽ làm 4 việc cùng lúc:
            // 1. Tạo Đơn hàng (Order)
            // 2. Trừ bớt Số lượng Voucher (UsageCount)
            // 3. Trừ bớt Tồn kho Sản phẩm (Stock)
            // 4. Xóa hàng khỏi Giỏ (Cart)
            int orderId = dbHelper.createOrder(
                    currentUserId,
                    name,
                    phone,
                    address,
                    finalNote.toString(), // Truyền dòng nhật ký ghi chú lên
                    finalTotal,
                    checkoutItems,
                    selectedFreeshipVoucher != null ? selectedFreeshipVoucher.getCode() : null,
                    selectedDiscountVoucher != null ? selectedDiscountVoucher.getCode() : null
            );

            // Xử lý sau khi tạo Đơn thành công / thất bại
            if (orderId != -1) {
                // Thành công: Chuyển sang màn hình Báo Thành Công (OrderSuccessActivity)
                Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                intent.putExtra("order_id", orderId);
                startActivity(intent);
                finish(); // Đóng trang thanh toán này
            } else {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Đặt hàng thất bại, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm vẽ lại thông tin giá tiền lên Giao Diện
    // Chú ý: Hàm này CHỈ HIỂN THỊ, không ghi gì xuống cơ sở dữ liệu cả.
    private void updateTotalUI() {
        int baseShippingFee = 30000; // Giả sử ship toàn quốc 30k
        int shippingDiscountAmount = 0;

        // Nếu có chọn mã Freeship -> Tính số tiền sẽ được trừ
        if (selectedFreeshipVoucher != null) {
            shippingDiscountAmount = Math.min(baseShippingFee, selectedFreeshipVoucher.getValue());
        }

        // Nếu có chọn mã Giảm giá tiền hàng
        if (selectedDiscountVoucher != null) {
            discountAmount = selectedDiscountVoucher.getValue();
        } else {
            discountAmount = 0;
        }

        // Tổng tiền cuối cùng = Hàng + Ship - Khuyến mãi
        int finalTotal = subtotal + baseShippingFee - shippingDiscountAmount - discountAmount;
        if (finalTotal < 0) finalTotal = 0; // Chống lỗi tiền âm

        tvShippingFee.setText(FormatUtils.formatPrice(baseShippingFee));
        
        // Ánh xạ view từ XML sang Java
        android.view.View shippingDiscountRow = findViewById(R.id.llShippingDiscount);
        // Ánh xạ view từ XML sang Java
        TextView tvShippingDiscount = findViewById(R.id.tvShippingDiscount);
        if (shippingDiscountAmount > 0) {
            tvShippingDiscount.setText("-" + FormatUtils.formatPrice(shippingDiscountAmount));
            shippingDiscountRow.setVisibility(android.view.View.VISIBLE);
        } else {
            shippingDiscountRow.setVisibility(android.view.View.GONE);
        }
        
        // Ánh xạ view từ XML sang Java
        android.view.View discountRow = findViewById(R.id.llDiscountAmount);
        if (discountAmount > 0) {
            tvDiscountAmount.setText("-" + FormatUtils.formatPrice(discountAmount));
            discountRow.setVisibility(android.view.View.VISIBLE);
        } else {
            discountRow.setVisibility(android.view.View.GONE);
        }
        
        tvOrderTotal.setText(FormatUtils.formatPrice(finalTotal));

        // Hiển thị danh sách voucher đang được gắn tạm vào đơn.
        StringBuilder selectedText = new StringBuilder();
        if (selectedFreeshipVoucher != null) {
            selectedText.append(selectedFreeshipVoucher.getCode());
        }
        if (selectedDiscountVoucher != null) {
            if (selectedText.length() > 0) selectedText.append(" & ");
            selectedText.append(selectedDiscountVoucher.getCode());
        }
        if (selectedText.length() == 0) {
            tvSelectedVoucher.setText("Chọn hoặc nhập mã giảm giá");
        } else {
            tvSelectedVoucher.setText("Đã áp dụng: " + selectedText.toString());
        }
    }

    // ==============================================================================
    // PHẦN CHỌN VOUCHER (HỘP THOẠI)
    // ==============================================================================
    // Hàm bật lên Hộp thoại danh sách Mã giảm giá.
    // Chú ý: Khi chọn Voucher ở đây, hệ thống chưa vội trừ lượt dùng (usageCount).
    // Hệ thống chỉ trừ khi khách hàng Bấm Nút ĐẶT HÀNG THÀNH CÔNG ở bước trên.
    private void showVoucherDialog() {
        java.util.List<com.example.quanlycuahangthoitrang.model.Voucher> vouchers = dbHelper.getAllVouchers();
        if (vouchers.isEmpty()) {
            // Hiện thông báo (Toast) cho người dùng
            Toast.makeText(this, "Hiện không có mã giảm giá nào khả dụng", Toast.LENGTH_SHORT).show();
            return;
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_vouchers, null);
        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        android.widget.LinearLayout llVoucherList = dialogView.findViewById(R.id.llVoucherList);

        for (com.example.quanlycuahangthoitrang.model.Voucher v : vouchers) {
            android.view.View itemView = getLayoutInflater().inflate(R.layout.item_voucher, llVoucherList, false);
            android.widget.TextView tvCode = itemView.findViewById(R.id.tvVoucherCode);
            android.widget.TextView tvDesc = itemView.findViewById(R.id.tvVoucherDesc);
            android.widget.TextView btnApply = itemView.findViewById(R.id.btnApplyVoucher);

            tvCode.setText(v.getCode());
            tvDesc.setText(v.getDisplayText());

            boolean isSelected = (selectedFreeshipVoucher != null && selectedFreeshipVoucher.getId() == v.getId()) ||
                                 (selectedDiscountVoucher != null && selectedDiscountVoucher.getId() == v.getId());

            if (isSelected) {
                btnApply.setText("Bỏ chọn");
                btnApply.setTextColor(getResources().getColor(R.color.error_red));
                btnApply.setBackgroundResource(R.drawable.bg_button_outline_orange); // Keep outline but use for red
                btnApply.setOnClickListener(btn -> {
                    if (v.getType().equals("freeship")) {
                        selectedFreeshipVoucher = null;
                    } else {
                        selectedDiscountVoucher = null;
                    }
                    updateTotalUI();
                    dialog.dismiss();
                });
            } else {
                if (subtotal < v.getMinOrder()) {
                    btnApply.setText("Chưa đạt");
                    btnApply.setTextColor(getResources().getColor(R.color.text_secondary));
                    btnApply.setBackgroundResource(0);
                    itemView.setAlpha(0.5f);
                } else {
                    btnApply.setOnClickListener(btn -> {
                        if (v.getType().equals("freeship")) {
                            selectedFreeshipVoucher = v;
                        } else {
                            selectedDiscountVoucher = v;
                        }
                        updateTotalUI();
                        // Hiện thông báo (Toast) cho người dùng
                        Toast.makeText(this, "Đã áp dụng mã " + v.getCode(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                }
            }
            llVoucherList.addView(itemView);
        }

        dialogView.findViewById(R.id.btnCancelVoucher).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Resolve user hiện tại để toàn bộ thao tác order luôn gắn đúng chủ sở hữu.
    private int getCurrentUserId() {
        User currentUser = dbHelper.getUserByEmail(sessionManager.getEmail());
        return currentUser != null ? currentUser.getId() : -1;
    }
}
