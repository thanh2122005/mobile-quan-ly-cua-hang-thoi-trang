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

    private void applyFilterAndSearch() {
        ArrayList<Order> searchResults = new ArrayList<>();
        if (currentKeyword.isEmpty()) {
            searchResults.addAll(allOrders);
        } else {
            searchResults = dbHelper.searchOrders(currentKeyword);
        }

        ArrayList<Order> filteredList = new ArrayList<>();
        for (Order o : searchResults) {
            if ("Tất cả".equals(currentFilter) || o.getStatus().equals(currentFilter)) {
                filteredList.add(o);
            }
        }

        adapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvAdminOrders.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvAdminOrders.setVisibility(View.VISIBLE);
        }
    }
}
