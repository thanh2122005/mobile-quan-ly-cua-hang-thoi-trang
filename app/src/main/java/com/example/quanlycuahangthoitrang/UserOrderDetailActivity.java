package com.example.quanlycuahangthoitrang;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Order;
import com.example.quanlycuahangthoitrang.model.OrderItem;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

public class UserOrderDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_user_order_detail);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Khởi tạo bộ công cụ thao tác với CSDL
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int orderId = getIntent().getIntExtra("order_id", -1);

        Order order = dbHelper.getOrderById(orderId);
        if (order == null) {
            // Hiện thông báo (Toast) cho người dùng
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ view từ XML sang Java
        TextView tvOrderCode = findViewById(R.id.tvOrderCode);
        // Ánh xạ view từ XML sang Java
        TextView tvOrderStatus = findViewById(R.id.tvOrderStatus);
        // Ánh xạ view từ XML sang Java
        TextView tvOrderDate = findViewById(R.id.tvOrderDate);
        // Ánh xạ view từ XML sang Java
        TextView tvReceiverName = findViewById(R.id.tvReceiverName);
        // Ánh xạ view từ XML sang Java
        TextView tvReceiverPhone = findViewById(R.id.tvReceiverPhone);
        // Ánh xạ view từ XML sang Java
        TextView tvReceiverAddress = findViewById(R.id.tvReceiverAddress);
        // Ánh xạ view từ XML sang Java
        TextView tvOrderTotal = findViewById(R.id.tvOrderTotal);
        // Ánh xạ view từ XML sang Java
        LinearLayout llOrderItems = findViewById(R.id.llOrderItems);

        tvOrderCode.setText("Mã ĐH: " + order.getCode());
        tvOrderStatus.setText("Trạng thái: " + order.getStatus());
        tvOrderDate.setText("Ngày đặt: " + order.getCreatedAt());
        tvReceiverName.setText(order.getReceiverName());
        tvReceiverPhone.setText(order.getPhone());
        tvReceiverAddress.setText(order.getAddress());
        tvOrderTotal.setText(FormatUtils.formatPrice(order.getTotal()));

        // Vòng lặp: Vẽ danh sách các mặt hàng trong đơn ra màn hình bằng Java code thay vì XML
        // Mục đích: Mỗi đơn hàng có số lượng mặt hàng khác nhau nên phải vẽ động
        for (OrderItem item : order.getItems()) {
            // Tạo 1 dòng chữ nhật dọc để chứa thông tin 1 mặt hàng
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 0, 0, 32);

            // Dòng ngang để chứa [Tên + Số lượng] ở bên trái, [Giá tiền] ở bên phải
            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);

            // Chữ: Số lượng x Tên sản phẩm
            TextView tvItemName = new TextView(this);
            tvItemName.setText(item.getQuantity() + "x " + item.getProductName());
            tvItemName.setTextColor(getResources().getColor(R.color.text_primary));
            tvItemName.setTextSize(15);
            tvItemName.setTypeface(null, android.graphics.Typeface.BOLD);
            
            // Cài đặt cho Tên sản phẩm chiếm phần lớn diện tích (weight = 1)
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            nameParams.setMarginEnd(24);
            tvItemName.setLayoutParams(nameParams);

            // Chữ: Giá tiền
            TextView tvItemPrice = new TextView(this);
            tvItemPrice.setText(FormatUtils.formatPrice(item.getSubtotal()));
            tvItemPrice.setTextColor(getResources().getColor(R.color.primary_purple));
            tvItemPrice.setTextSize(15);
            tvItemPrice.setTypeface(null, android.graphics.Typeface.BOLD);

            // Nhét Tên và Giá vào Dòng ngang
            topRow.addView(tvItemName);
            topRow.addView(tvItemPrice);
            row.addView(topRow);

            if (item.getSelectedColor() != null || item.getSelectedSize() != null) {
                TextView tvVariant = new TextView(this);
                String variantText = "";
                if (item.getSelectedColor() != null && !item.getSelectedColor().isEmpty()) {
                    variantText += "Màu sắc: " + item.getSelectedColor();
                }
                if (item.getSelectedSize() != null && !item.getSelectedSize().isEmpty()) {
                    if (!variantText.isEmpty()) variantText += "   |   ";
                    variantText += "Kích cỡ: " + item.getSelectedSize();
                }
                if (!variantText.isEmpty()) {
                    tvVariant.setText(variantText);
                    tvVariant.setTextColor(getResources().getColor(R.color.text_secondary));
                    tvVariant.setTextSize(13);
                    tvVariant.setPadding(0, 8, 0, 0);
                    row.addView(tvVariant);
                }
            }

            llOrderItems.addView(row);
        }

        // Xử lý nút [HỦY ĐƠN HÀNG]
        // Chỉ cho phép hủy nếu trạng thái đơn đang là "Chờ xác nhận"
        // Ánh xạ view từ XML sang Java
        TextView btnCancelOrder = findViewById(R.id.btnCancelOrder);
        if (order.getStatus().equals("Chờ xác nhận")) {
            btnCancelOrder.setVisibility(android.view.View.VISIBLE); // Hiện nút Hủy
            
            // Cài đặt sự kiện khi bấm nút Hủy
            btnCancelOrder.setOnClickListener(v -> {
                // Hiển thị hộp thoại (Dialog) hỏi lại cho chắc chắn
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Xác nhận hủy")
                        .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này không?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            // Gọi DB để đổi trạng thái thành "Đã hủy"
                            if (dbHelper.updateOrderStatus(orderId, "Đã hủy")) {
                                // Hiện thông báo (Toast) cho người dùng
                                Toast.makeText(this, "Đã hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                                tvOrderStatus.setText("Trạng thái: Đã hủy");
                                btnCancelOrder.setVisibility(android.view.View.GONE); // Giấu nút đi
                            } else {
                                // Hiện thông báo (Toast) cho người dùng
                                Toast.makeText(this, "Hủy đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Đóng", null)
                        .show();
            });
        } else {
            // Giấu nút Hủy nếu đơn hàng đang giao hoặc đã hoàn thành
            btnCancelOrder.setVisibility(android.view.View.GONE);
        }

        // Ánh xạ view từ XML sang Java
        TextView btnRateOrder = findViewById(R.id.btnRateOrder);
        if (order.getStatus().equals("Hoàn thành")) {
            com.example.quanlycuahangthoitrang.utils.SessionManager sm = new com.example.quanlycuahangthoitrang.utils.SessionManager(this);
            com.example.quanlycuahangthoitrang.model.User currentUser = dbHelper.getUserByEmail(sm.getEmail());
            int userId = currentUser != null ? currentUser.getId() : -1;
            com.example.quanlycuahangthoitrang.model.Review existingReview = null;
            
            if (!order.getItems().isEmpty() && userId != -1) {
                existingReview = dbHelper.getReviewByUserAndProduct(userId, order.getItems().get(0).getProductId());
            }

            if (existingReview == null) {
                btnRateOrder.setVisibility(android.view.View.VISIBLE);
                btnRateOrder.setText("Đánh giá sản phẩm");
                btnRateOrder.setOnClickListener(v -> {
                    if (!order.getItems().isEmpty()) {
                        showReviewDialog(order.getItems().get(0).getProductId(), null);
                    }
                });
            } else {
                btnRateOrder.setVisibility(android.view.View.VISIBLE);
                btnRateOrder.setText("Sửa đánh giá");
                com.example.quanlycuahangthoitrang.model.Review finalExistingReview = existingReview;
                btnRateOrder.setOnClickListener(v -> {
                    if (!order.getItems().isEmpty()) {
                        showReviewDialog(order.getItems().get(0).getProductId(), finalExistingReview);
                    }
                });
            }
        } else {
            btnRateOrder.setVisibility(android.view.View.GONE);
        }
    }

    private void showReviewDialog(int productId, com.example.quanlycuahangthoitrang.model.Review existingReview) {
        android.app.Dialog dialog = new android.app.Dialog(this);
        // Nạp giao diện từ file XML
        dialog.setContentView(R.layout.dialog_add_review);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        android.widget.RatingBar rbReview = dialog.findViewById(R.id.ratingBar);
        android.widget.EditText edtComment = dialog.findViewById(R.id.edtReviewComment);
        TextView btnCancel = dialog.findViewById(R.id.btnCancelReview);
        TextView btnSubmit = dialog.findViewById(R.id.btnSubmitReview);

        if (existingReview != null) {
            rbReview.setRating(existingReview.getRating());
            edtComment.setText(existingReview.getComment());
            btnSubmit.setText("Cập nhật");
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSubmit.setOnClickListener(v -> {
            int rating = (int) rbReview.getRating();
            String comment = edtComment.getText().toString().trim();
            if (rating == 0) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }
            if (comment.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper db = new DatabaseHelper(this);
            int userId = -1;
            com.example.quanlycuahangthoitrang.utils.SessionManager sessionManager = new com.example.quanlycuahangthoitrang.utils.SessionManager(this);
            com.example.quanlycuahangthoitrang.model.User currentUser = db.getUserByEmail(sessionManager.getEmail());
            if (currentUser != null) {
                userId = currentUser.getId();
            }

            String createdAt = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());

            boolean success;
            if (existingReview != null) {
                success = db.updateReview(existingReview.getId(), rating, comment, createdAt);
            } else {
                success = db.addReview(userId, productId, rating, comment, createdAt);
            }

            if (success) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, existingReview != null ? "Cập nhật đánh giá thành công!" : "Đánh giá thành công!", Toast.LENGTH_SHORT).show();
                // Ánh xạ view từ XML sang Java
                TextView btnRateOrder = findViewById(R.id.btnRateOrder);
                if (btnRateOrder != null) {
                    btnRateOrder.setText("Sửa đánh giá");
                    // Update the click listener to pass the new review
                    com.example.quanlycuahangthoitrang.model.Review newRev = db.getReviewByUserAndProduct(userId, productId);
                    btnRateOrder.setOnClickListener(v2 -> showReviewDialog(productId, newRev));
                }
                dialog.dismiss();
            } else {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Lỗi khi đánh giá", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
