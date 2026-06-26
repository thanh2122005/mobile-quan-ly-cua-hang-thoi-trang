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

        llOrderItems.removeAllViews();
        for (OrderItem item : currentOrder.getItems()) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 0, 0, 32);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);

            LinearLayout textContainer = new LinearLayout(this);
            textContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            textParams.setMarginEnd(24);
            textContainer.setLayoutParams(textParams);

            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
            
            TextView tvItemName = new TextView(this);
            tvItemName.setText(item.getQuantity() + "x " + item.getProductName());
            tvItemName.setTextColor(getResources().getColor(R.color.text_primary));
            tvItemName.setTextSize(15);
            tvItemName.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            nameParams.setMarginEnd(16);
            tvItemName.setLayoutParams(nameParams);

            TextView tvItemPrice = new TextView(this);
            tvItemPrice.setText(FormatUtils.formatPrice(item.getSubtotal()));
            tvItemPrice.setTextColor(getResources().getColor(R.color.primary_purple));
            tvItemPrice.setTextSize(15);
            tvItemPrice.setTypeface(null, android.graphics.Typeface.BOLD);

            topRow.addView(tvItemName);
            topRow.addView(tvItemPrice);

            if (currentOrder.getStatus().equals("Chờ xác nhận")) {
                TextView btnEditItem = new TextView(this);
                btnEditItem.setText("Sửa");
                btnEditItem.setTextColor(getResources().getColor(R.color.white));
                btnEditItem.setBackgroundResource(R.drawable.bg_button_orange);
                btnEditItem.setPadding(24, 8, 24, 8);
                btnEditItem.setTextSize(12);
                btnEditItem.setTypeface(null, android.graphics.Typeface.BOLD);
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                btnParams.setMarginStart(24);
                btnEditItem.setLayoutParams(btnParams);
                btnEditItem.setOnClickListener(v -> showEditItemDialog(item));
                topRow.addView(btnEditItem);
            }

            textContainer.addView(topRow);

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
                    textContainer.addView(tvVariant);
                }
            }

            row.addView(textContainer);

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
