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

        com.example.quanlycuahangthoitrang.utils.SessionManager sm = new com.example.quanlycuahangthoitrang.utils.SessionManager(this);
        com.example.quanlycuahangthoitrang.model.User currentUser = dbHelper.getUserByEmail(sm.getEmail());
        int userId = currentUser != null ? currentUser.getId() : -1;

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

            if (order.getStatus().equals("Hoàn thành") && userId != -1) {
                com.example.quanlycuahangthoitrang.model.Review existingReview = dbHelper.getReviewByUserAndProduct(userId, item.getProductId());
                TextView btnRateItem = new TextView(this);
                btnRateItem.setText(existingReview == null ? "⭐ Đánh giá sản phẩm" : "✏️ Sửa đánh giá");
                btnRateItem.setTextColor(getResources().getColor(R.color.accent_orange));
                btnRateItem.setTextSize(14);
                btnRateItem.setPadding(0, 16, 0, 0);
                btnRateItem.setTypeface(null, android.graphics.Typeface.BOLD);
                btnRateItem.setOnClickListener(v -> showReviewDialog(item.getProductId(), existingReview));
                row.addView(btnRateItem);
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
    }

    // Hàm hiển thị Hộp thoại (Dialog) cho phép Khách hàng Đánh giá (Review) sản phẩm
    // Tham số productId: ID của sản phẩm đang được đánh giá
    // Tham số existingReview: Dữ liệu đánh giá cũ (nếu khách hàng chọn Sửa đánh giá)
    private void showReviewDialog(int productId, com.example.quanlycuahangthoitrang.model.Review existingReview) {
        // Khởi tạo một Dialog (Hộp thoại nổi lơ lửng trên màn hình)
        android.app.Dialog dialog = new android.app.Dialog(this);
        
        // Gắn giao diện XML (dialog_add_review.xml) vào hộp thoại này
        dialog.setContentView(R.layout.dialog_add_review);
        
        // Làm trong suốt nền của hộp thoại (để lộ các viền cong bo góc đẹp hơn)
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Đặt kích thước chiều ngang tràn viền, chiều dọc tự động bóp lại vừa với nội dung
        dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        // Tìm thanh kéo thả số Sao (RatingBar) trong giao diện của Dialog
        android.widget.RatingBar rbReview = dialog.findViewById(R.id.ratingBar);
        // Tìm ô nhập chữ (EditText) để viết Bình luận
        android.widget.EditText edtComment = dialog.findViewById(R.id.edtReviewComment);
        // Tìm 2 nút Bấm: Hủy và Gửi
        TextView btnCancel = dialog.findViewById(R.id.btnCancelReview);
        TextView btnSubmit = dialog.findViewById(R.id.btnSubmitReview);

        // Kiểm tra xem khách hàng đang Viết mới hay Sửa bài cũ
        if (existingReview != null) {
            // Đổ số sao cũ ra thanh kéo
            rbReview.setRating(existingReview.getRating());
            // Đổ lời bình luận cũ ra ô nhập chữ
            edtComment.setText(existingReview.getComment());
            // Đổi tên nút thành "Cập nhật" thay vì "Gửi"
            btnSubmit.setText("Cập nhật");
        }

        // Bắt sự kiện bấm nút Hủy
        btnCancel.setOnClickListener(v -> dialog.dismiss()); // Tắt hộp thoại, không làm gì cả
        
        // Bắt sự kiện bấm nút Gửi/Cập nhật
        btnSubmit.setOnClickListener(v -> {
            // Lấy số sao khách vừa chọn (Từ 1 tới 5)
            int rating = (int) rbReview.getRating();
            // Lấy nội dung chữ khách vừa gõ và xóa khoảng trắng thừa ở 2 đầu
            String comment = edtComment.getText().toString().trim();
            
            // Bắt lỗi: Nếu khách chưa kéo sao nào thì báo lỗi
            if (rating == 0) {
                Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }
            // Bắt lỗi: Nếu khách để trống ô chữ thì báo lỗi
            if (comment.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi DatabaseHelper để chuẩn bị lưu vào CSDL
            DatabaseHelper db = new DatabaseHelper(this);
            int userId = -1; // Biến tạm lưu ID khách hàng
            
            // Mở SessionManager để xem ai đang đăng nhập
            com.example.quanlycuahangthoitrang.utils.SessionManager sessionManager = new com.example.quanlycuahangthoitrang.utils.SessionManager(this);
            com.example.quanlycuahangthoitrang.model.User currentUser = db.getUserByEmail(sessionManager.getEmail());
            if (currentUser != null) {
                // Lấy ID thật
                userId = currentUser.getId();
            }

            // Tạo chuỗi thời gian hiện tại để ghi nhớ lúc Đánh giá (VD: 30/06/2026 15:30)
            String createdAt = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());

            // Biến cờ kiểm tra xem quá trình lưu có thành công hay không
            boolean success;
            if (existingReview != null) {
                // Nếu là Sửa -> Gọi hàm UPDATE trong bảng reviews
                success = db.updateReview(existingReview.getId(), rating, comment, createdAt);
            } else {
                // Nếu là Thêm mới -> Gọi hàm INSERT vào bảng reviews
                success = db.addReview(userId, productId, rating, comment, createdAt);
            }

            // Kiểm tra kết quả
            if (success) {
                Toast.makeText(this, existingReview != null ? "Cập nhật đánh giá thành công!" : "Đánh giá thành công!", Toast.LENGTH_SHORT).show();
                
                // Tải lại toàn bộ màn hình để cập nhật nút đánh giá thành "Sửa đánh giá"
                recreate();
                
                // Tắt hộp thoại sau khi thành công
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Lỗi khi đánh giá", Toast.LENGTH_SHORT).show();
            }
        });

        // Lệnh cuối cùng: Hiện hộp thoại lên giữa màn hình
        dialog.show();
    }
}
