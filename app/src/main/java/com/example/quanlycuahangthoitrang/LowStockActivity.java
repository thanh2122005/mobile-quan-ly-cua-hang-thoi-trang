package com.example.quanlycuahangthoitrang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Product;

import java.util.List;

public class LowStockActivity extends AppCompatActivity {

    private LowStockAdapter adapter;
    private RecyclerView rvLowStock;
    private TextView tvLowStockSummary;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_low_stock);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Ánh xạ view từ XML sang Java
        rvLowStock = findViewById(R.id.rvLowStock);
        tvLowStockSummary = findViewById(R.id.tvLowStockSummary);
        
        // Cài đặt danh sách cuộn theo chiều dọc
        rvLowStock.setLayoutManager(new LinearLayoutManager(this));

        // KHỞI TẠO BỘ CHUYỂN ĐỔI (ADAPTER) CHO CẢNH BÁO HẾT HÀNG
        // Gọi hàm getLowStockProducts(3) từ Database: Chỉ lấy những sản phẩm có tồn kho <= 3
        adapter = new LowStockAdapter(dbHelper.getLowStockProducts(3), new LowStockAdapter.OnProductClickListener() {
            // Xử lý sự kiện khi Admin bấm nút [Nhập thêm hàng]
            @Override
            public void onUpdateClick(Product product) {
                // Chuyển sang màn hình Nhập thêm hàng (UpdateStockActivity)
                Intent intent = new Intent(LowStockActivity.this, UpdateStockActivity.class);
                // Truyền theo ID của sản phẩm để màn hình kia biết đường cập nhật cho đúng
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }
        });
        // Gắn Adapter vào danh sách
        rvLowStock.setAdapter(adapter);

        // Hiển thị dòng chữ tóm tắt: "Có X sản phẩm dưới mức tồn kho"
        updateSummary();
    }

    // Hàm tự động chạy lại khi Admin vừa nhập xong hàng và quay lại màn hình này
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            // Tải lại danh sách (Nếu sản phẩm nào đã nhập hàng > 3 thì nó sẽ tự biến mất khỏi danh sách này)
            adapter.updateData(dbHelper.getLowStockProducts(3));
            // Cập nhật lại số đếm ở dòng chữ trên cùng
            updateSummary();
        }
    }

    // Hàm đếm số lượng sản phẩm sắp hết hàng và hiển thị ra câu thông báo
    private void updateSummary() {
        // Đếm kích thước (size) của danh sách lọc ra từ DB
        int count = dbHelper.getLowStockProducts(3).size();
        tvLowStockSummary.setText("Có " + count + " sản phẩm dưới mức tồn kho (<= 3).");
    }
}
