package com.example.quanlycuahangthoitrang;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Voucher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class VoucherManagementActivity extends AppCompatActivity {

    private RecyclerView rvVouchers;
    private FloatingActionButton fabAddVoucher;
    private VoucherAdminAdapter adapter;
    private DatabaseHelper dbHelper;
    private ArrayList<Voucher> voucherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_voucher_management);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        // Ánh xạ view từ XML sang Java
        rvVouchers = findViewById(R.id.rvVouchers);
        // Ánh xạ view từ XML sang Java
        fabAddVoucher = findViewById(R.id.fabAddVoucher);

        rvVouchers.setLayoutManager(new LinearLayoutManager(this));

        loadVouchers();

        fabAddVoucher.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void loadVouchers() {
        voucherList = dbHelper.getAdminAllVouchers();
        if (adapter == null) {
            adapter = new VoucherAdminAdapter(this, voucherList, new VoucherAdminAdapter.OnVoucherInteractionListener() {
                @Override
                public void onEdit(Voucher voucher) {
                    showAddEditDialog(voucher);
                }

                @Override
                public void onDelete(Voucher voucher) {
                    new AlertDialog.Builder(VoucherManagementActivity.this)
                            .setTitle("Xóa mã giảm giá")
                            .setMessage("Bạn có chắc chắn muốn xóa mã " + voucher.getCode() + " không?")
                            .setPositiveButton("Xóa", (dialog, which) -> {
                                if (dbHelper.deleteVoucher(voucher.getId())) {
                                    // Hiện thông báo (Toast) cho người dùng
                                    Toast.makeText(VoucherManagementActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                                    loadVouchers();
                                }
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                }
            });
            rvVouchers.setAdapter(adapter);
        } else {
            adapter.updateData(voucherList);
        }
    }

    // Hàm hiển thị Hộp thoại (Dialog) để Thêm mới hoặc Sửa mã giảm giá (Voucher)
    // Tham số voucher: Nếu bằng null -> Thêm mới. Nếu có dữ liệu -> Sửa mã cũ.
    private void showAddEditDialog(Voucher voucher) {
        // Nạp giao diện của hộp thoại từ file XML (dialog_add_edit_voucher.xml)
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_voucher, null);
        
        // Tạo một đối tượng hộp thoại (AlertDialog) và gắn giao diện vừa nạp vào
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
                
        // Làm trong suốt nền của hộp thoại để lộ các góc bo tròn mềm mại
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Ánh xạ các thành phần (TextView, EditText, Spinner) từ XML sang biến Java
        TextView tvTitle = view.findViewById(R.id.tvDialogTitle); // Tiêu đề hộp thoại
        EditText edtCode = view.findViewById(R.id.edtCode); // Ô nhập mã (VD: FREESHIP50K)
        Spinner spinnerType = view.findViewById(R.id.spinnerType); // Ô chọn kiểu giảm giá (Miễn ship / Giảm tiền)
        EditText edtValue = view.findViewById(R.id.edtValue); // Mức giảm (VD: 30000)
        EditText edtMinOrder = view.findViewById(R.id.edtMinOrder); // Đơn tối thiểu để được dùng mã
        EditText edtLimit = view.findViewById(R.id.edtLimit); // Số lượng mã tối đa được phát hành
        TextView btnSave = view.findViewById(R.id.btnSave); // Nút Lưu
        TextView btnCancel = view.findViewById(R.id.btnCancel); // Nút Hủy

        // Tạo mảng danh sách các loại mã giảm giá để nhét vào hộp thả xuống (Spinner)
        String[] types = {"Miễn phí vận chuyển", "Giảm giá trực tiếp"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spinnerType.setAdapter(spinnerAdapter);

        // KIỂM TRA: Nếu tham số voucher KHÔNG rỗng -> Tức là đang ở chế độ SỬA
        if (voucher != null) {
            tvTitle.setText("Sửa mã giảm giá"); // Đổi tiêu đề
            edtCode.setText(voucher.getCode()); // Đổ mã cũ vào ô nhập
            
            // Nếu kiểu trong CSDL là "freeship" thì chọn dòng số 0 trong Spinner, ngược lại chọn dòng số 1
            spinnerType.setSelection(voucher.getType().equalsIgnoreCase("freeship") ? 0 : 1);
            
            // Đổ các thông số cũ lên giao diện
            edtValue.setText(String.valueOf(voucher.getValue()));
            edtMinOrder.setText(String.valueOf(voucher.getMinOrder()));
            edtLimit.setText(String.valueOf(voucher.getUsageLimit()));
        }

        // Bắt sự kiện bấm nút Hủy -> Đóng hộp thoại
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Bắt sự kiện bấm nút Lưu -> Kiểm tra và lưu vào CSDL
        btnSave.setOnClickListener(v -> {
            // Lấy dữ liệu người dùng vừa gõ và cắt khoảng trắng 2 đầu
            String code = edtCode.getText().toString().trim();
            // Lấy loại mã dựa vào vị trí khách hàng chọn trong Spinner (0 = freeship, 1 = discount)
            String type = spinnerType.getSelectedItemPosition() == 0 ? "freeship" : "discount";
            String valStr = edtValue.getText().toString().trim();
            String minOrderStr = edtMinOrder.getText().toString().trim();
            String limitStr = edtLimit.getText().toString().trim();

            // Rào cản 1: Kiểm tra xem có ô nào bị bỏ trống không
            if (code.isEmpty() || valStr.isEmpty() || minOrderStr.isEmpty() || limitStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return; // Bắt nhập lại
            }

            int value, minOrder, limit;
            try {
                // Ép kiểu dữ liệu từ Chữ (String) sang Số nguyên (int) để tính toán
                value = Integer.parseInt(valStr);
                minOrder = Integer.parseInt(minOrderStr);
                limit = Integer.parseInt(limitStr);
                
                // Rào cản 2: Không cho nhập số âm
                if (value < 0 || minOrder < 0 || limit < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                // Nếu ép kiểu lỗi (VD: Khách nhập chữ cái vào ô số) thì báo lỗi
                Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success;
            // KIỂM TRA: Nếu đang là THÊM MỚI
            if (voucher == null) {
                // Gọi DB để chèn dòng mới vào bảng vouchers
                success = dbHelper.addVoucher(code, type, value, minOrder, limit);
            } else {
                // KIỂM TRA: Nếu đang là SỬA
                // Gọi DB để cập nhật dòng cũ dựa theo ID
                success = dbHelper.updateVoucher(voucher.getId(), code, type, value, minOrder, limit);
            }

            // Phản hồi kết quả
            if (success) {
                Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                // Tải lại danh sách voucher lên giao diện để thấy dòng mới thêm
                loadVouchers();
                // Đóng hộp thoại
                dialog.dismiss();
            } else {
                // Báo lỗi nếu mã (Code) bị trùng lặp trong CSDL (Vì cột Code được cài là UNIQUE)
                Toast.makeText(this, "Lưu thất bại. Mã có thể bị trùng.", Toast.LENGTH_SHORT).show();
            }
        });

        // Hiển thị hộp thoại lên giữa màn hình
        dialog.show();
    }
}
