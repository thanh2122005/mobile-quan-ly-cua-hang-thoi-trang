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

    // Hàm này chạy đầu tiên khi màn hình Thống kê được mở lên
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file activity_statistic.xml
        setContentView(R.layout.activity_statistic);

        // Khởi tạo công cụ kết nối cơ sở dữ liệu
        dbHelper = new DatabaseHelper(this);

        // Nút quay lại màn hình trước
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Ánh xạ các nút bấm chọn bộ lọc thời gian (Tất cả, Hôm nay, Tuần, Tháng)
        btnFilterAll = findViewById(R.id.btnFilterAll);
        // Ánh xạ view từ XML sang Java
        btnFilterToday = findViewById(R.id.btnFilterToday);
        // Ánh xạ view từ XML sang Java
        btnFilterWeek = findViewById(R.id.btnFilterWeek);
        // Ánh xạ view từ XML sang Java
        btnFilterMonth = findViewById(R.id.btnFilterMonth);

        // Gọi hàm cài đặt sự kiện khi bấm vào các nút lọc
        setupFilters();
        
        // Gọi hàm cài đặt sự kiện khi bấm vào các thẻ thống kê
        setupInteractions();
    }

    // Hàm cài đặt sự kiện click cho các nút lọc thời gian
    private void setupFilters() {
        // Tạo một bộ lắng nghe sự kiện dùng chung cho cả 4 nút
        View.OnClickListener filterListener = v -> {
            // Bước 1: Xóa màu nền màu cam của nút đang được chọn trước đó
            resetFilterStyles();
            
            // Bước 2: Tô màu cam cho nút vừa được bấm
            TextView tv = (TextView) v;
            tv.setBackgroundResource(R.drawable.bg_button_orange);
            tv.setTextColor(Color.WHITE);

            // Bước 3: Kiểm tra xem người dùng vừa bấm vào nút nào để gán biến currentFilter
            int id = v.getId();
            if (id == R.id.btnFilterAll) {
                currentFilter = "ALL";
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Đang hiển thị tất cả thống kê", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.btnFilterToday) {
                currentFilter = "TODAY";
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Đang hiển thị thống kê hôm nay", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.btnFilterWeek) {
                currentFilter = "WEEK";
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Đang hiển thị thống kê theo tuần", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.btnFilterMonth) {
                currentFilter = "MONTH";
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Đang hiển thị thống kê theo tháng", Toast.LENGTH_SHORT).show();
            }
            
            // Bước 4: Sau khi chọn xong bộ lọc thì tính toán lại toàn bộ con số thống kê
            loadStatistics();
        };

        // Gắn bộ lắng nghe sự kiện vừa tạo vào 4 nút
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

    // Hàm cài đặt sự kiện khi bấm vào các khối vuông thống kê
    private void setupInteractions() {
        // Bấm vào ô Doanh thu -> Chuyển sang màn hình Quản lý đơn hàng
        findViewById(R.id.cardRevenue).setOnClickListener(v -> {
            // Hiện thông báo (Toast) cho người dùng
            Toast.makeText(this, "Mở quản lý đơn hàng", Toast.LENGTH_SHORT).show();
            // Chuyển sang màn hình tương ứng
            startActivity(new Intent(this, AdminOrderActivity.class));
        });
        
        // Bấm vào ô Đơn hàng -> Chuyển sang màn hình Quản lý đơn hàng
        findViewById(R.id.cardOrders).setOnClickListener(v -> {
            // Hiện thông báo (Toast) cho người dùng
            Toast.makeText(this, "Mở quản lý đơn hàng", Toast.LENGTH_SHORT).show();
            // Chuyển sang màn hình tương ứng
            startActivity(new Intent(this, AdminOrderActivity.class));
        });
        
        // Bấm vào ô Sản phẩm đã bán -> Chuyển sang màn hình Quản lý đơn hàng
        findViewById(R.id.cardSold).setOnClickListener(v -> {
            // Hiện thông báo (Toast) cho người dùng
            Toast.makeText(this, "Mở quản lý đơn hàng", Toast.LENGTH_SHORT).show();
            // Chuyển sang màn hình tương ứng
            startActivity(new Intent(this, AdminOrderActivity.class));
        });
        
        // Bấm vào ô Sắp hết hàng -> Chuyển sang màn hình Cảnh báo hết hàng
        findViewById(R.id.cardLowStock).setOnClickListener(v -> {
            // Hiện thông báo (Toast) cho người dùng
            Toast.makeText(this, "Mở sản phẩm sắp hết hàng", Toast.LENGTH_SHORT).show();
            // Chuyển sang màn hình tương ứng
            startActivity(new Intent(this, LowStockActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics();
    }

    // Hàm hỗ trợ kiểm tra xem ngày tạo đơn hàng có nằm trong khoảng thời gian lọc hay không
    private boolean isDateIncluded(String dateStr, long filterStartTime) {
        if (filterStartTime == 0) return true; // Nếu chọn "Tất cả" thì luôn trả về đúng
        if (dateStr == null) return false;
        
        try {
            // Chuyển đổi chuỗi ngày tháng thành số miliseconds để so sánh
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date d = sdf.parse(dateStr);
            if (d != null) {
                return d.getTime() >= filterStartTime; // Đúng nếu ngày tạo lớn hơn ngày bắt đầu lọc
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hàm cực kỳ quan trọng: Tính toán lại toàn bộ con số trên màn hình
    private void loadStatistics() {
        // Ánh xạ các ô chứa số lượng để cập nhật dữ liệu
        TextView tvRevenue = findViewById(R.id.tvRevenue);
        // Ánh xạ view từ XML sang Java
        TextView tvTotalInvoices = findViewById(R.id.tvTotalInvoices);
        // Ánh xạ view từ XML sang Java
        TextView tvTotalProductsSold = findViewById(R.id.tvTotalProductsSold);
        // Ánh xạ view từ XML sang Java
        TextView tvLowStock = findViewById(R.id.tvLowStock);

        // Khởi tạo các biến chứa kết quả đếm (lúc đầu bằng 0)
        int revenue = 0;
        int totalInvoices = 0;
        int productsSold = 0;

        // --- BẮT ĐẦU PHẦN TÍNH TOÁN NGÀY GIỜ ĐỂ LỌC ---
        java.util.Calendar cal = java.util.Calendar.getInstance();
        long filterStartTime = 0; // Thời điểm bắt đầu tính
        
        // Lấy ngày hôm nay dưới dạng chữ (VD: "15/06/2026")
        String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.getTime());

        if (currentFilter.equals("TODAY")) {
            // Nếu lọc Hôm nay: Đặt thời gian về 0h:00m:00s của ngày hôm nay
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            filterStartTime = cal.getTimeInMillis();
            
        } else if (currentFilter.equals("WEEK")) {
            // Nếu lọc Tuần: Lùi lịch lại 7 ngày trước đó
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            cal.add(java.util.Calendar.DAY_OF_YEAR, -7);
            filterStartTime = cal.getTimeInMillis();
            
        } else if (currentFilter.equals("MONTH")) {
            // Nếu lọc Tháng: Lùi lịch lại 30 ngày trước đó
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            cal.add(java.util.Calendar.DAY_OF_YEAR, -30);
            filterStartTime = cal.getTimeInMillis();
        }
        // --- KẾT THÚC PHẦN TÍNH TOÁN NGÀY GIỜ ---

        // Lưu tổng số lượng bán ra của từng sản phẩm (Key: ProductId, Value: Quantity)
        Map<Integer, Integer> productSales = new HashMap<>();

        // Quét qua toàn bộ ĐƠN HÀNG ONLINE (mua qua app)

        // Quét qua toàn bộ ĐƠN HÀNG ONLINE (mua qua app)
        for (com.example.quanlycuahangthoitrang.model.Order order : dbHelper.getAllOrders()) {
            // Chỉ tính tiền cho những đơn Online đã "Hoàn thành"
            if (!"Hoàn thành".equals(order.getStatus())) continue;

            boolean include = true;
            // Kiểm tra khớp bộ lọc thời gian
            if (currentFilter.equals("TODAY")) {
                include = order.getCreatedAt() != null && order.getCreatedAt().startsWith(todayDate);
            } else if (!currentFilter.equals("ALL")) {
                include = isDateIncluded(order.getCreatedAt(), filterStartTime);
            }

            // Nếu khớp, cộng dồn dữ liệu tương tự như Hóa đơn trực tiếp
            if (include) {
                revenue += order.getTotal();
                totalInvoices++;
                for (com.example.quanlycuahangthoitrang.model.OrderItem item : order.getItems()) {
                    productsSold += item.getQuantity();
                    int pid = item.getProductId();
                    productSales.put(pid, productSales.getOrDefault(pid, 0) + item.getQuantity());
                }
            }
        }

        // Tính xem có bao nhiêu sản phẩm tồn kho sắp hết (dưới 3 cái)
        int lowStockCount = dbHelper.getLowStockProducts(3).size();

        // HIỂN THỊ KẾT QUẢ LÊN MÀN HÌNH
        tvRevenue.setText(FormatUtils.formatPrice(revenue));
        tvTotalInvoices.setText(String.valueOf(totalInvoices));
        tvTotalProductsSold.setText(String.valueOf(productsSold));
        tvLowStock.setText(String.valueOf(lowStockCount));

        loadTopProducts(productSales);
        loadChart(filterStartTime);
    }

    // Hàm hiển thị danh sách 3 sản phẩm bán chạy nhất
    private void loadTopProducts(Map<Integer, Integer> productSales) {
        // Ánh xạ vùng hiển thị danh sách từ file XML
        LinearLayout llTopProducts = findViewById(R.id.llTopProducts);
        // Ánh xạ view từ XML sang Java
        TextView tvEmptyTopProducts = findViewById(R.id.tvEmptyTopProducts);
        
        // Xóa các sản phẩm cũ trước khi nạp dữ liệu mới
        llTopProducts.removeAllViews();

        // Nếu không có sản phẩm nào bán được thì hiện dòng chữ "Chưa có dữ liệu"
        if (productSales.isEmpty()) {
            llTopProducts.setVisibility(View.GONE);
            tvEmptyTopProducts.setVisibility(View.VISIBLE);
            return;
        }

        // Ẩn chữ "Chưa có dữ liệu" đi nếu có danh sách
        llTopProducts.setVisibility(View.VISIBLE);
        tvEmptyTopProducts.setVisibility(View.GONE);

        // Chuyển Map thành List và sắp xếp giảm dần theo số lượng bán được
        // Để sản phẩm bán nhiều nhất lên đầu
        List<Map.Entry<Integer, Integer>> sortedList = new ArrayList<>(productSales.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int rank = 1; // Biến đánh số thứ hạng (1, 2, 3)
        // Vòng lặp lấy thông tin và vẽ ra màn hình
        for (Map.Entry<Integer, Integer> entry : sortedList) {
            if (rank > 3) break; // Chỉ lấy top 3 sản phẩm đầu tiên

            // Truy vấn database để lấy Tên sản phẩm từ ProductId
            Product p = dbHelper.getProductById(entry.getKey());
            
            if (p != null) {
                // TẠO GIAO DIỆN BẰNG CODE JAVA THAY VÌ XML
                // Tạo một hàng nằm ngang (LinearLayout)
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(0, 8, 0, 8);
                row.setGravity(Gravity.CENTER_VERTICAL);

                // Cột 1: Hiển thị thứ hạng (1. 2. 3.)
                TextView tvRank = new TextView(this);
                tvRank.setText(rank + ".");
                tvRank.setTypeface(null, android.graphics.Typeface.BOLD);
                tvRank.setPadding(0, 0, 16, 0);

                // Cột 2: Hiển thị tên sản phẩm
                TextView tvName = new TextView(this);
                tvName.setText(p.getName());
                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                tvName.setLayoutParams(nameParams);

                // Cột 3: Hiển thị số lượng đã bán ("x đã bán")
                TextView tvSold = new TextView(this);
                tvSold.setText(entry.getValue() + " đã bán");
                tvSold.setTextSize(12);
                tvSold.setTextColor(getResources().getColor(R.color.text_secondary));

                // Bỏ 3 cột này vào hàng ngang vừa tạo
                row.addView(tvRank);
                row.addView(tvName);
                row.addView(tvSold);

                // Khi bấm vào sản phẩm thì hiện bong bóng nhỏ (Toast) báo tên sản phẩm
                row.setOnClickListener(v -> Toast.makeText(this, p.getName(), Toast.LENGTH_SHORT).show());

                // Gắn nguyên cái hàng ngang này vào giao diện chính
                llTopProducts.addView(row);
                rank++;
            }
        }
    }

    // Hàm hiển thị Biểu đồ cột Doanh thu
    private void loadChart(long filterStartTime) {
        // Ánh xạ biểu đồ từ giao diện XML sang Java
        // Chú ý: Ở đây ta dùng lớp CustomBarChartView do tự tay ta code, thay vì View mặc định của Android
        CustomBarChartView barChartView = findViewById(R.id.barChartView);

        // Lấy danh sách doanh thu được gom nhóm theo từng ngày từ trong CSDL
        // Kết quả ví dụ: { "15/06": 3000000, "16/06": 5000000 }
        java.util.Map<String, Integer> dailyRevenue = dbHelper.getDailyRevenue(filterStartTime);

        // Gửi mảng dữ liệu này vào trong biểu đồ
        // Biểu đồ sẽ đọc các con số này, tự động chia tỷ lệ cao thấp và dùng thuật toán vẽ (onDraw)
        // để phác họa ra các hình chữ nhật (Cột biểu đồ) tương ứng.
        barChartView.setData(dailyRevenue);
    }
}
