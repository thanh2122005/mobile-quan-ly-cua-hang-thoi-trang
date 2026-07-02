package com.example.quanlycuahangthoitrang;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Order;

import java.util.ArrayList;

public class AdminOrderActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rvAdminOrders;
    private AdminOrderAdapter adapter;
    private ArrayList<Order> allOrders = new ArrayList<>();
    private TextView tvEmptyState;
    private String currentFilter = "Tất cả";
    private String currentKeyword = "";

    private TextView chipAll, chipPending, chipConfirmed, chipShipping, chipCompleted, chipCanceled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_admin_order);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Ánh xạ view từ XML sang Java
        rvAdminOrders = findViewById(R.id.rvAdminOrders);
        // Ánh xạ view từ XML sang Java
        tvEmptyState = findViewById(R.id.tvEmptyState);
        // Ánh xạ view từ XML sang Java
        EditText edtSearch = findViewById(R.id.edtSearch);

        rvAdminOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrderAdapter(this, new ArrayList<>());
        rvAdminOrders.setAdapter(adapter);

        // Ánh xạ view từ XML sang Java
        chipAll = findViewById(R.id.chipAll);
        // Ánh xạ view từ XML sang Java
        chipPending = findViewById(R.id.chipPending);
        // Ánh xạ view từ XML sang Java
        chipConfirmed = findViewById(R.id.chipConfirmed);
        // Ánh xạ view từ XML sang Java
        chipShipping = findViewById(R.id.chipShipping);
        // Ánh xạ view từ XML sang Java
        chipCompleted = findViewById(R.id.chipCompleted);
        // Ánh xạ view từ XML sang Java
        chipCanceled = findViewById(R.id.chipCanceled);

        setupChips();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s.toString().trim();
                applyFilterAndSearch();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        allOrders = dbHelper.getAllOrders();
        applyFilterAndSearch();
    }

    private void setupChips() {
        View.OnClickListener chipClickListener = v -> {
            resetChips();
            v.setBackgroundResource(R.drawable.bg_chip_selected);
            ((TextView) v).setTextColor(getResources().getColor(R.color.white));
            currentFilter = ((TextView) v).getText().toString();
            applyFilterAndSearch();
        };

        chipAll.setOnClickListener(chipClickListener);
        chipPending.setOnClickListener(chipClickListener);
        chipConfirmed.setOnClickListener(chipClickListener);
        chipShipping.setOnClickListener(chipClickListener);
        chipCompleted.setOnClickListener(chipClickListener);
        chipCanceled.setOnClickListener(chipClickListener);
    }

    private void resetChips() {
        TextView[] chips = {chipAll, chipPending, chipConfirmed, chipShipping, chipCompleted, chipCanceled};
        for (TextView chip : chips) {
            chip.setBackgroundResource(R.drawable.bg_chip_unselected);
            chip.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    // Hàm này rất quan trọng: Kết hợp giữa TÌM KIẾM (theo chữ gõ) và LỌC (theo trạng thái đã chọn)
    private void applyFilterAndSearch() {
        // Tạo một mảng rỗng để chứa kết quả trung gian sau khi Tìm kiếm
        ArrayList<Order> searchResults = new ArrayList<>();
        
        // Bước 1: KIỂM TRA TÌM KIẾM
        // Nếu ô tìm kiếm đang bỏ trống -> Lấy toàn bộ danh sách gốc (allOrders) bỏ vào searchResults
        if (currentKeyword.isEmpty()) {
            searchResults.addAll(allOrders);
        } else {
            // Nếu có gõ chữ -> Gọi hàm tìm kiếm trong Database để quét các đơn hàng khớp với chữ đó
            searchResults = dbHelper.searchOrders(currentKeyword);
        }

        // Tạo thêm một mảng rỗng nữa để chứa kết quả cuối cùng sau khi Lọc
        ArrayList<Order> filteredList = new ArrayList<>();
        
        // Bước 2: KIỂM TRA LỌC TRẠNG THÁI
        // Duyệt qua từng Đơn hàng (Order) trong danh sách vừa Tìm kiếm được
        for (Order o : searchResults) {
            // Rào cản: Nếu đang chọn "Tất cả" HOẶC trạng thái của đơn hàng này khớp y chang cái Đang chọn
            if ("Tất cả".equals(currentFilter) || o.getStatus().equals(currentFilter)) {
                // Thì mới nhét đơn hàng đó vào danh sách Cuối cùng
                filteredList.add(o);
            }
        }

        // Bước 3: Đưa danh sách Cuối cùng vào Adapter để vẽ ra màn hình
        adapter.updateList(filteredList);

        // Bước 4: HIỂN THỊ THÔNG BÁO NẾU TRỐNG
        if (filteredList.isEmpty()) {
            // Nếu danh sách lọc ra không có đơn nào -> Bật dòng chữ "Không tìm thấy" lên
            tvEmptyState.setVisibility(View.VISIBLE);
            // Giấu hẳn cái Danh sách cuộn đi để không bị lỗi khoảng trắng
            rvAdminOrders.setVisibility(View.GONE);
        } else {
            // Nếu có đơn hàng -> Giấu dòng chữ trống đi
            tvEmptyState.setVisibility(View.GONE);
            // Hiện danh sách cuộn lên
            rvAdminOrders.setVisibility(View.VISIBLE);
        }
    }
}
