package com.example.quanlycuahangthoitrang;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlycuahangthoitrang.database.DatabaseHelper;
import com.example.quanlycuahangthoitrang.model.Product;

public class EditProductActivity extends AppCompatActivity {

    private Product product;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        int productId = getIntent().getIntExtra("product_id", -1);
        product = dbHelper.getProductById(productId);

        if (product == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        EditText edtProductName = findViewById(R.id.edtProductName);
        Spinner spnCategory = findViewById(R.id.spnCategory);
        EditText edtPrice = findViewById(R.id.edtPrice);
        EditText edtStock = findViewById(R.id.edtStock);
        EditText edtColor = findViewById(R.id.edtColor);
        EditText edtDescription = findViewById(R.id.edtDescription);
        android.widget.ImageView ivProductImage = findViewById(R.id.ivProductImage);

        java.util.List<com.example.quanlycuahangthoitrang.model.Category> categoryList = dbHelper.getAllCategories();
        java.util.List<String> categoryNames = new java.util.ArrayList<>();
        int selectedIndex = 0;
        for (int i = 0; i < categoryList.size(); i++) {
            String cName = categoryList.get(i).getName();
            categoryNames.add(cName);
            if (cName.equals(product.getCategory())) {
                selectedIndex = i;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryNames);
        spnCategory.setAdapter(adapter);
        spnCategory.setSelection(selectedIndex);

        edtProductName.setText(product.getName());
        edtPrice.setText(String.valueOf(product.getPrice()));
        edtStock.setText(String.valueOf(product.getStock()));
        edtColor.setText(product.getColor());
        edtDescription.setText(product.getDescription());
        
        final int[] selectedImageResId = {product.getImageResId()};
        ivProductImage.setImageResource(selectedImageResId[0]);

        findViewById(R.id.btnChooseImage).setOnClickListener(v -> {
            String[] options = {"Áo thun", "Sơ mi", "Quần", "Giày", "Túi/Phụ kiện", "Kính", "Mặc định"};
            int[] resIds = {R.drawable.ic_product_tshirt, R.drawable.ic_product_shirt, R.drawable.ic_product_pants, R.drawable.ic_product_shoes, R.drawable.ic_product_bag, R.drawable.ic_product_glasses, R.drawable.ic_product_default};
            new android.app.AlertDialog.Builder(this)
                .setTitle("Chọn ảnh sản phẩm")
                .setItems(options, (dialog, which) -> {
                    selectedImageResId[0] = resIds[which];
                    ivProductImage.setImageResource(selectedImageResId[0]);
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

            int price, stock;
            try {
                price = Integer.parseInt(priceStr);
                stock = Integer.parseInt(stockStr);
                if (price <= 0 || stock < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá phải > 0 và số lượng phải >= 0", Toast.LENGTH_SHORT).show();
                return;
            }

            product.setName(name);
            product.setCategory(category);
            product.setPrice(price);
            product.setStock(stock);
            product.setColor(color);
            product.setDescription(desc);
            product.setImageResId(selectedImageResId[0]);

            if (dbHelper.updateProduct(product)) {
                Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xóa sản phẩm")
                    .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        if (dbHelper.deleteProduct(product.getId())) {
                            Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Lỗi khi xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }
}
