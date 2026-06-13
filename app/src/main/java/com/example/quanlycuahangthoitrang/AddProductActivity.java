package com.example.quanlycuahangthoitrang;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Product;

public class AddProductActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        EditText edtProductName = findViewById(R.id.edtProductName);
        Spinner spnCategory = findViewById(R.id.spnCategory);
        EditText edtPrice = findViewById(R.id.edtPrice);
        EditText edtStock = findViewById(R.id.edtStock);
        EditText edtColor = findViewById(R.id.edtColor);
        EditText edtDescription = findViewById(R.id.edtDescription);
        android.widget.ImageView ivProductImage = findViewById(R.id.ivProductImage);

        java.util.List<com.example.quanlycuahangthoitrang.model.Category> categoryList = dbHelper.getAllCategories();
        java.util.List<String> categoryNames = new java.util.ArrayList<>();
        for (com.example.quanlycuahangthoitrang.model.Category c : categoryList) {
            categoryNames.add(c.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryNames);
        spnCategory.setAdapter(adapter);

        final int[] selectedImageResId = {0};

        findViewById(R.id.btnChooseImage).setOnClickListener(v -> {
            String[] options = {"Áo thun", "Sơ mi", "Quần", "Giày", "Túi/Phụ kiện", "Kính", "Mặc định"};
            int[] resIds = {R.drawable.ic_product_tshirt, R.drawable.ic_product_shirt, R.drawable.ic_product_pants, R.drawable.ic_product_shoes, R.drawable.ic_product_bag, R.drawable.ic_product_glasses, R.drawable.ic_product_default};
            new android.app.AlertDialog.Builder(this)
                .setTitle("Chọn ảnh sản phẩm")
                .setItems(options, (dialog, which) -> {
                    selectedImageResId[0] = resIds[which];
                    if (ivProductImage != null) {
                        ivProductImage.setImageResource(selectedImageResId[0]);
                    }
                })
                .show();
        });

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = edtProductName.getText().toString().trim();
            String category = spnCategory.getSelectedItem() != null ? spnCategory.getSelectedItem().toString() : "";
            String priceStr = edtPrice.getText().toString().trim();
            String stockStr = edtStock.getText().toString().trim();
            String color = edtColor.getText().toString().trim();
            String desc = edtDescription.getText().toString().trim();

            if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (color.isEmpty()) color = "Chưa cập nhật";
            if (desc.isEmpty()) desc = "Chưa có mô tả";

            int price = 0;
            int stock = 0;
            try {
                price = Integer.parseInt(priceStr);
                stock = Integer.parseInt(stockStr);
                if (price <= 0 || stock < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá phải > 0 và số lượng phải >= 0", Toast.LENGTH_SHORT).show();
                return;
            }

            int finalImageResId = selectedImageResId[0];
            if (finalImageResId == 0) {
                String normalizedCategory = com.example.quanlycuahangthoitrang.utils.FormatUtils.normalizeSearchText(category);
                if (normalizedCategory.contains("ao")) {
                    finalImageResId = R.drawable.ic_product_tshirt;
                } else if (normalizedCategory.contains("quan")) {
                    finalImageResId = R.drawable.ic_product_pants;
                } else if (normalizedCategory.contains("giay")) {
                    finalImageResId = R.drawable.ic_product_shoes;
                } else if (normalizedCategory.contains("phu kien") || normalizedCategory.contains("tui")) {
                    finalImageResId = R.drawable.ic_product_bag;
                } else if (normalizedCategory.contains("kinh")) {
                    finalImageResId = R.drawable.ic_product_glasses;
                } else {
                    finalImageResId = R.drawable.ic_product_default;
                }
            }

            Product p = new Product(0, name, category, price, color, desc, finalImageResId, stock);
            if (dbHelper.insertProduct(p)) {
                Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi thêm sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
