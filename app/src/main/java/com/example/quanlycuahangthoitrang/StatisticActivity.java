package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Invoice;
import com.example.quanlycuahangthoitrang.model.InvoiceItem;
import com.example.quanlycuahangthoitrang.model.Product;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticActivity extends AppCompatActivity {

    private TextView btnFilterAll, btnFilterToday, btnFilterWeek, btnFilterMonth;
    private String currentFilter = "ALL";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterToday = findViewById(R.id.btnFilterToday);
        btnFilterWeek = findViewById(R.id.btnFilterWeek);
        btnFilterMonth = findViewById(R.id.btnFilterMonth);

        setupFilters();
        setupInteractions();
    }

    private void setupFilters() {
        View.OnClickListener filterListener = v -> {
            resetFilterStyles();
            TextView tv = (TextView) v;
            tv.setBackgroundResource(R.drawable.bg_button_orange);
            tv.setTextColor(Color.WHITE);

            int id = v.getId();
            if (id == R.id.btnFilterAll) {
                currentFilter = "ALL";
                Toast.makeText(this, "Đang hiển thị tất cả thống kê", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.btnFilterToday) {
                currentFilter = "TODAY";
                Toast.makeText(this, "Đang hiển thị thống kê hôm nay", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.btnFilterWeek) {
                currentFilter = "WEEK";
                Toast.makeText(this, "Đang hiển thị thống kê theo tuần", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.btnFilterMonth) {
                currentFilter = "MONTH";
                Toast.makeText(this, "Đang hiển thị thống kê theo tháng", Toast.LENGTH_SHORT).show();
            }
            loadStatistics();
        };

        btnFilterAll.setOnClickListener(filterListener);
        btnFilterToday.setOnClickListener(filterListener);
        btnFilterWeek.setOnClickListener(filterListener);
        btnFilterMonth.setOnClickListener(filterListener);
    }

    private void resetFilterStyles() {
        TextView[] filters = {btnFilterAll, btnFilterToday, btnFilterWeek, btnFilterMonth};
        for (TextView tv : filters) {
            tv.setBackgroundResource(R.drawable.bg_button_outline_orange);
            tv.setTextColor(getResources().getColor(R.color.accent_orange));
        }
    }

    private void setupInteractions() {
        findViewById(R.id.cardRevenue).setOnClickListener(v -> {
            Toast.makeText(this, "Mở lịch sử hóa đơn", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, InvoiceHistoryActivity.class));
        });
        findViewById(R.id.cardOrders).setOnClickListener(v -> {
            Toast.makeText(this, "Mở lịch sử hóa đơn", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, InvoiceHistoryActivity.class));
        });
        findViewById(R.id.cardSold).setOnClickListener(v -> {
            Toast.makeText(this, "Mở lịch sử hóa đơn", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, InvoiceHistoryActivity.class));
        });
        findViewById(R.id.cardLowStock).setOnClickListener(v -> {
            Toast.makeText(this, "Mở sản phẩm sắp hết hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LowStockActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics();
    }

    private void loadStatistics() {
        TextView tvRevenue = findViewById(R.id.tvRevenue);
        TextView tvTotalInvoices = findViewById(R.id.tvTotalInvoices);
        TextView tvTotalProductsSold = findViewById(R.id.tvTotalProductsSold);
        TextView tvLowStock = findViewById(R.id.tvLowStock);

        int revenue = 0;
        int totalInvoices = 0;
        int productsSold = 0;

        String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        Map<Integer, Integer> productSales = new HashMap<>();

        for (Invoice invoice : dbHelper.getAllInvoices()) {
            boolean include = true;
            if (currentFilter.equals("TODAY")) {
                include = invoice.getDate() != null && invoice.getDate().startsWith(todayDate);
            }

            if (include) {
                revenue += invoice.getTotal();
                totalInvoices++;
                for (InvoiceItem item : invoice.getItems()) {
                    productsSold += item.getQuantity();

                    int pid = item.getProduct().getId();
                    productSales.put(pid, productSales.getOrDefault(pid, 0) + item.getQuantity());
                }
            }
        }

        int lowStockCount = dbHelper.getLowStockProducts(3).size();

        tvRevenue.setText(FormatUtils.formatPrice(revenue));
        tvTotalInvoices.setText(String.valueOf(totalInvoices));
        tvTotalProductsSold.setText(String.valueOf(productsSold));
        tvLowStock.setText(String.valueOf(lowStockCount));

        loadTopProducts(productSales);
        loadChart();
    }

    private void loadTopProducts(Map<Integer, Integer> productSales) {
        LinearLayout llTopProducts = findViewById(R.id.llTopProducts);
        TextView tvEmptyTopProducts = findViewById(R.id.tvEmptyTopProducts);
        llTopProducts.removeAllViews();

        if (productSales.isEmpty()) {
            llTopProducts.setVisibility(View.GONE);
            tvEmptyTopProducts.setVisibility(View.VISIBLE);
            return;
        }

        llTopProducts.setVisibility(View.VISIBLE);
        tvEmptyTopProducts.setVisibility(View.GONE);

        List<Map.Entry<Integer, Integer>> sortedList = new ArrayList<>(productSales.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int rank = 1;
        for (Map.Entry<Integer, Integer> entry : sortedList) {
            if (rank > 3) break;

            Product p = dbHelper.getProductById(entry.getKey());
            if (p != null) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(0, 8, 0, 8);
                row.setGravity(Gravity.CENTER_VERTICAL);

                TextView tvRank = new TextView(this);
                tvRank.setText(rank + ".");
                tvRank.setTypeface(null, android.graphics.Typeface.BOLD);
                tvRank.setPadding(0, 0, 16, 0);

                TextView tvName = new TextView(this);
                tvName.setText(p.getName());
                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                tvName.setLayoutParams(nameParams);

                TextView tvSold = new TextView(this);
                tvSold.setText(entry.getValue() + " đã bán");
                tvSold.setTextSize(12);
                tvSold.setTextColor(getResources().getColor(R.color.text_secondary));

                row.addView(tvRank);
                row.addView(tvName);
                row.addView(tvSold);

                row.setOnClickListener(v -> Toast.makeText(this, p.getName(), Toast.LENGTH_SHORT).show());

                llTopProducts.addView(row);
                rank++;
            }
        }
    }

    private void loadChart() {
        LinearLayout llChartContainer = findViewById(R.id.llChartContainer);
        llChartContainer.removeAllViews();

        // Simulate 7 days revenue heights (relative to a max height of 150dp - 32dp padding approx = 100dp max)
        int[] heights = {40, 70, 30, 90, 60, 50, 80}; // dp

        float density = getResources().getDisplayMetrics().density;

        for (int i = 0; i < 7; i++) {
            View bar = new View(this);
            int heightPx = (int) (heights[i] * density);
            int widthPx = (int) (20 * density);
            int marginPx = (int) (4 * density);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthPx, heightPx);
            params.setMargins(marginPx, 0, marginPx, 0);
            bar.setLayoutParams(params);

            if (i == 6) { // today
                bar.setBackgroundColor(getResources().getColor(R.color.primary_purple));
            } else {
                bar.setBackgroundColor(getResources().getColor(R.color.primary_fixed));
            }

            llChartContainer.addView(bar);
        }
    }
}
