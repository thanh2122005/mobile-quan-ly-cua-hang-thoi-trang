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

    private void showAddEditDialog(Voucher voucher) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_voucher, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
        EditText edtCode = view.findViewById(R.id.edtCode);
        Spinner spinnerType = view.findViewById(R.id.spinnerType);
        EditText edtValue = view.findViewById(R.id.edtValue);
        EditText edtMinOrder = view.findViewById(R.id.edtMinOrder);
        EditText edtLimit = view.findViewById(R.id.edtLimit);
        TextView btnSave = view.findViewById(R.id.btnSave);
        TextView btnCancel = view.findViewById(R.id.btnCancel);

        String[] types = {"Miễn phí vận chuyển", "Giảm giá trực tiếp"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spinnerType.setAdapter(spinnerAdapter);

        if (voucher != null) {
            tvTitle.setText("Sửa mã giảm giá");
            edtCode.setText(voucher.getCode());
            spinnerType.setSelection(voucher.getType().equalsIgnoreCase("freeship") ? 0 : 1);
            edtValue.setText(String.valueOf(voucher.getValue()));
            edtMinOrder.setText(String.valueOf(voucher.getMinOrder()));
            edtLimit.setText(String.valueOf(voucher.getUsageLimit()));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String code = edtCode.getText().toString().trim();
            String type = spinnerType.getSelectedItemPosition() == 0 ? "freeship" : "discount";
            String valStr = edtValue.getText().toString().trim();
            String minOrderStr = edtMinOrder.getText().toString().trim();
            String limitStr = edtLimit.getText().toString().trim();

            if (code.isEmpty() || valStr.isEmpty() || minOrderStr.isEmpty() || limitStr.isEmpty()) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int value, minOrder, limit;
            try {
                value = Integer.parseInt(valStr);
                minOrder = Integer.parseInt(minOrderStr);
                limit = Integer.parseInt(limitStr);
                if (value < 0 || minOrder < 0 || limit < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success;
            if (voucher == null) {
                success = dbHelper.addVoucher(code, type, value, minOrder, limit);
            } else {
                success = dbHelper.updateVoucher(voucher.getId(), code, type, value, minOrder, limit);
            }

            if (success) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                loadVouchers();
                dialog.dismiss();
            } else {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Lưu thất bại. Mã có thể bị trùng.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
