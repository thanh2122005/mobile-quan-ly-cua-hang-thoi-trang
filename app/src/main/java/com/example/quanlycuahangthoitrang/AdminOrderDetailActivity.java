package com.example.quanlycuahangthoitrang;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Order;
import com.example.quanlycuahangthoitrang.model.OrderItem;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

public class AdminOrderDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int orderId;
    private Spinner spinnerStatus;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_admin_order_detail);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);
        orderId = getIntent().getIntExtra("order_id", -1);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Ánh xạ view từ XML sang Java
        spinnerStatus = findViewById(R.id.spinnerStatus);
        loadOrderDetails();

        // Nút Cập nhật Trạng thái đơn hàng (Dành riêng cho Admin)
        findViewById(R.id.btnUpdateStatus).setOnClickListener(v -> {
            // Lấy trạng thái mới mà Admin vừa chọn từ cái Menu thả xuống (Spinner)
            String newStatus = spinnerStatus.getSelectedItem().toString();
            
            // Nếu trạng thái mới y hệt trạng thái cũ thì không làm gì cả
            if (currentOrder != null && currentOrder.getStatus().equals(newStatus)) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Trạng thái chưa thay đổi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tiến hành cập nhật trạng thái mới xuống Database
            // Lưu ý: Nếu đổi sang "Đã hủy" thì phải cộng lại số lượng vào tồn kho
            if (dbHelper.updateOrderStatus(orderId, newStatus)) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                loadOrderDetails(); // Tải lại giao diện để xem cập nhật
            } else {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Cập nhật thất bại. Trạng thái không hợp lệ hoặc kho không đủ.", Toast.LENGTH_LONG).show();
                loadOrderDetails();
            }
        });
    }

    private void loadOrderDetails() {
        currentOrder = dbHelper.getOrderById(orderId);
        if (currentOrder == null) {
            // Hiện thông báo (Toast) cho người dùng
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ view từ XML sang Java
        TextView tvOrderCode = findViewById(R.id.tvOrderCode);
        // Ánh xạ view từ XML sang Java
        TextView tvOrderDate = findViewById(R.id.tvOrderDate);
        // Ánh xạ view từ XML sang Java
        TextView tvOrderStatus = findViewById(R.id.tvOrderStatus);
        // Ánh xạ view từ XML sang Java
        TextView tvReceiverName = findViewById(R.id.tvReceiverName);
        // Ánh xạ view từ XML sang Java
        TextView tvReceiverPhone = findViewById(R.id.tvReceiverPhone);
        // Ánh xạ view từ XML sang Java
        TextView tvReceiverAddress = findViewById(R.id.tvReceiverAddress);
        // Ánh xạ view từ XML sang Java
        TextView tvOrderNote = findViewById(R.id.tvOrderNote);
        // Ánh xạ view từ XML sang Java
        TextView tvOrderTotal = findViewById(R.id.tvOrderTotal);
        // Ánh xạ view từ XML sang Java
        LinearLayout llOrderItems = findViewById(R.id.llOrderItems);

        tvOrderCode.setText("Mã ĐH: " + currentOrder.getCode());
        tvOrderDate.setText("Ngày đặt: " + currentOrder.getCreatedAt());
        tvOrderStatus.setText("Trạng thái: " + currentOrder.getStatus());
        tvReceiverName.setText("Tên: " + currentOrder.getReceiverName());
        tvReceiverPhone.setText("SĐT: " + currentOrder.getPhone());
        tvReceiverAddress.setText("Địa chỉ: " + currentOrder.getAddress());
        String formattedNote = currentOrder.getNote() != null ? currentOrder.getNote() : "";
        formattedNote = formattedNote.replace(" [", "\n[");
        if (!formattedNote.toLowerCase().startsWith("ghi chú")) {
            formattedNote = "Ghi chú:\n" + formattedNote;
        }
        tvOrderNote.setText(formattedNote);
        tvOrderTotal.setText(FormatUtils.formatPrice(currentOrder.getTotal()));
        setupStatusOptions(currentOrder.getStatus());

        // Set spinner selection
        String status = currentOrder.getStatus();
        for (int i = 0; i < spinnerStatus.getCount(); i++) {
            if (spinnerStatus.getItemAtPosition(i).toString().equals(status)) {
                spinnerStatus.setSelection(i);
                break;
            }
        }

        // Làm sạch danh sách trước khi vẽ để tránh bị chồng chéo dữ liệu cũ
        llOrderItems.removeAllViews();
        
        // Vòng lặp: Duyệt qua từng sản phẩm (OrderItem) có trong đơn hàng này
        for (OrderItem item : currentOrder.getItems()) {
            // Tạo một khung chữ nhật nằm ngang (LinearLayout) để chứa 1 mặt hàng
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 0, 0, 32); // Thêm khoảng cách ở dưới đáy cho đỡ dính nhau
            row.setGravity(android.view.Gravity.CENTER_VERTICAL); // Căn giữa theo chiều dọc

            // Tạo một khung con để chứa toàn bộ chữ (Tên, Giá, Thuộc tính)
            LinearLayout textContainer = new LinearLayout(this);
            textContainer.setOrientation(LinearLayout.VERTICAL); // Xếp chữ từ trên xuống dưới
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            textParams.setMarginEnd(24);
            textContainer.setLayoutParams(textParams);

            // Tạo một hàng ngang nhỏ để chứa Tên sản phẩm, Giá tiền, và nút Sửa
            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
            
            // Cài đặt hiển thị Tên sản phẩm (VD: "2x Áo len cổ tròn")
            TextView tvItemName = new TextView(this);
            tvItemName.setText(item.getQuantity() + "x " + item.getProductName());
            tvItemName.setTextColor(getResources().getColor(R.color.text_primary)); // Tô màu đen
            tvItemName.setTextSize(15);
            tvItemName.setTypeface(null, android.graphics.Typeface.BOLD); // In đậm
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            nameParams.setMarginEnd(16);
            tvItemName.setLayoutParams(nameParams);

            // Cài đặt hiển thị Giá tiền (VD: "299.000đ")
            TextView tvItemPrice = new TextView(this);
            tvItemPrice.setText(FormatUtils.formatPrice(item.getSubtotal()));
            tvItemPrice.setTextColor(getResources().getColor(R.color.primary_purple)); // Tô màu tím đặc trưng
            tvItemPrice.setTextSize(15);
            tvItemPrice.setTypeface(null, android.graphics.Typeface.BOLD); // In đậm

            // Gắn Tên và Giá vào cái hàng ngang nhỏ
            topRow.addView(tvItemName);
            topRow.addView(tvItemPrice);

            // NGHIỆP VỤ RIÊNG CỦA ADMIN:
            // Chỉ khi đơn hàng đang ở trạng thái "Chờ xác nhận" thì Admin mới có quyền Sửa thông tin món hàng
            // (Vì nếu đã giao đi rồi thì không thể sửa được nữa)
            if (currentOrder.getStatus().equals("Chờ xác nhận")) {
                // Tạo một cái nút giả lập bằng TextView
                TextView btnEditItem = new TextView(this);
                btnEditItem.setText("Sửa");
                btnEditItem.setTextColor(getResources().getColor(R.color.white));
                btnEditItem.setBackgroundResource(R.drawable.bg_button_orange); // Nền màu cam
                btnEditItem.setPadding(24, 8, 24, 8);
                btnEditItem.setTextSize(12);
                btnEditItem.setTypeface(null, android.graphics.Typeface.BOLD);
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                btnParams.setMarginStart(24);
                btnEditItem.setLayoutParams(btnParams);
                
                // Bắt sự kiện: Bấm vào nút thì mở hộp thoại Sửa mặt hàng
                btnEditItem.setOnClickListener(v -> showEditItemDialog(item));
                
                // Gắn cái nút Sửa này vào hàng ngang nhỏ
                topRow.addView(btnEditItem);
            }

            // Gắn cái hàng ngang nhỏ (Gồm Tên + Giá + Sửa) vào khung con
            textContainer.addView(topRow);

            // KIỂM TRA PHÂN LOẠI (MÀU SẮC, KÍCH CỠ)
            // Nếu khách hàng có chọn Màu hoặc Size thì mới in ra
            if (item.getSelectedColor() != null || item.getSelectedSize() != null) {
                TextView tvVariant = new TextView(this);
                String variantText = "";
                // Nếu có Màu thì nối chuỗi "Màu sắc: Xanh"
                if (item.getSelectedColor() != null && !item.getSelectedColor().isEmpty()) {
                    variantText += "Màu sắc: " + item.getSelectedColor();
                }
                // Nếu có Size thì nối chuỗi "Kích cỡ: XL"
                if (item.getSelectedSize() != null && !item.getSelectedSize().isEmpty()) {
                    // Thêm dấu gạch đứng để ngăn cách nếu có cả Màu và Size
                    if (!variantText.isEmpty()) variantText += "   |   ";
                    variantText += "Kích cỡ: " + item.getSelectedSize();
                }
                
                // Nếu chuỗi phân loại không rỗng thì gắn vào giao diện
                if (!variantText.isEmpty()) {
                    tvVariant.setText(variantText);
                    tvVariant.setTextColor(getResources().getColor(R.color.text_secondary)); // Chữ màu xám nhạt
                    tvVariant.setTextSize(13);
                    tvVariant.setPadding(0, 8, 0, 0);
                    textContainer.addView(tvVariant);
                }
            }

            // Gắn khung con vào khung chữ nhật tổng của 1 món hàng
            row.addView(textContainer);

            // Cuối cùng: Gắn cái món hàng hoàn chỉnh này vào danh sách ngoài màn hình
            llOrderItems.addView(row);
        }
    }

    private void showEditItemDialog(OrderItem item) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa sản phẩm");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        android.widget.EditText edtQuantity = new android.widget.EditText(this);
        edtQuantity.setHint("Số lượng");
        edtQuantity.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        edtQuantity.setText(String.valueOf(item.getQuantity()));
        layout.addView(edtQuantity);

        android.widget.EditText edtColor = new android.widget.EditText(this);
        edtColor.setHint("Màu sắc (vd: Đen)");
        edtColor.setText(item.getSelectedColor());
        layout.addView(edtColor);

        android.widget.EditText edtSize = new android.widget.EditText(this);
        edtSize.setHint("Kích cỡ (vd: M)");
        edtSize.setText(item.getSelectedSize());
        layout.addView(edtSize);

        builder.setView(layout);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String qtyStr = edtQuantity.getText().toString().trim();
            String color = edtColor.getText().toString().trim();
            String size = edtSize.getText().toString().trim();

            if (qtyStr.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
                return;
            }
            int newQty;
            try {
                newQty = Integer.parseInt(qtyStr);
                if (newQty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.updateOrderItem(orderId, item.getId(), item.getProductId(), item.getQuantity(), newQty, color, size, item.getUnitPrice())) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                loadOrderDetails(); // Reload to reflect changes
            } else {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Lỗi cập nhật. Đơn hàng không còn cho phép sửa hoặc kho không đủ.", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void setupStatusOptions(String currentStatus) {
        String[] statuses;
        if ("Chờ xác nhận".equals(currentStatus)) {
            statuses = new String[]{"Chờ xác nhận", "Đã xác nhận", "Đã hủy"};
        } else if ("Đã xác nhận".equals(currentStatus)) {
            statuses = new String[]{"Đã xác nhận", "Đang giao", "Đã hủy"};
        } else if ("Đang giao".equals(currentStatus)) {
            statuses = new String[]{"Đang giao", "Hoàn thành"};
        } else {
            statuses = new String[]{currentStatus};
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        spinnerStatus.setAdapter(adapter);
    }
}
