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
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_edit_product);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        int productId = getIntent().getIntExtra("product_id", -1);
        product = dbHelper.getProductById(productId);

        if (product == null) {
            // Hiện thông báo (Toast) cho người dùng
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ view từ XML sang Java
        EditText edtProductName = findViewById(R.id.edtProductName);
        // Ánh xạ view từ XML sang Java
        Spinner spnCategory = findViewById(R.id.spnCategory);
        // Ánh xạ view từ XML sang Java
        EditText edtPrice = findViewById(R.id.edtPrice);
        // Ánh xạ view từ XML sang Java
        EditText edtStock = findViewById(R.id.edtStock);
        // Ánh xạ view từ XML sang Java
        EditText edtColor = findViewById(R.id.edtColor);
        // Ánh xạ view từ XML sang Java
        EditText edtSizes = findViewById(R.id.edtSizes);
        // Ánh xạ view từ XML sang Java
        EditText edtDescription = findViewById(R.id.edtDescription);
        // Ánh xạ view từ XML sang Java
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
        edtSizes.setText(product.getSizes());
        edtDescription.setText(product.getDescription());
        
        final int[] selectedImageResId = {product.getImageResId()};
        ivProductImage.setImageResource(selectedImageResId[0]);

                findViewById(R.id.btnChooseImage).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.putExtra(android.content.Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, 100);
        });

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            // Lấy toàn bộ thông tin mới mà Admin vừa sửa
            String name = edtProductName.getText().toString().trim();
            String category = spnCategory.getSelectedItem() != null ? spnCategory.getSelectedItem().toString() : "";
            String priceStr = edtPrice.getText().toString().trim();
            String stockStr = edtStock.getText().toString().trim();
            String color = edtColor.getText().toString().trim();
            String sizes = edtSizes.getText().toString().trim();
            String desc = edtDescription.getText().toString().trim();

            // Kiểm tra các trường bắt buộc không được để trống
            if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int price, stock;
            // Ép kiểu Giá và Số lượng từ Chuỗi sang Số nguyên
            // Dùng try-catch để nếu Admin lỡ tay nhập chữ "abc" vào ô Giá thì phần mềm không bị văng (Crash)
            try {
                price = Integer.parseInt(priceStr);
                stock = Integer.parseInt(stockStr);
                if (price <= 0 || stock < 0) throw new NumberFormatException(); // Bắt lỗi nhập số âm
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá phải > 0 và số lượng phải >= 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gán dữ liệu mới đè lên dữ liệu cũ của sản phẩm
            product.setName(name);
            product.setCategory(category);
            product.setPrice(price);
            product.setStock(stock);
            product.setColor(color);
            product.setSizes(sizes);
            product.setDescription(desc);
            product.setImageResId(selectedImageResId[0]);
            
            if (!selectedImages.isEmpty()) {
                product.setImages(selectedImages);
            }

            // Gọi Database để lưu cập nhật
            if (dbHelper.updateProduct(product)) {
                Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                finish(); // Đóng màn hình để quay lại danh sách
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });

        // ----------------------------------------------------
        // XỬ LÝ SỰ KIỆN NÚT [XÓA SẢN PHẨM]
        // ----------------------------------------------------
        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            // Hiển thị hộp thoại Dialog hỏi lại 1 lần nữa cho chắc chắn
            new AlertDialog.Builder(this)
                    .setTitle("Xóa sản phẩm")
                    .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        // Gọi DB xóa theo ID
                        if (dbHelper.deleteProduct(product.getId())) {
                            Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Lỗi khi xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null) // Bấm hủy thì đóng hộp thoại không làm gì cả
                    .show();
        });
    }

    private java.util.ArrayList<String> selectedImages = new java.util.ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    android.net.Uri uri = data.getClipData().getItemAt(i).getUri();
                    String path = copyToInternal(uri);
                    if (path != null) selectedImages.add(path);
                }
            } else if (data.getData() != null) {
                String path = copyToInternal(data.getData());
                if (path != null) selectedImages.add(path);
            }
            
            if (!selectedImages.isEmpty()) {
                // Ánh xạ view từ XML sang Java
                android.widget.ImageView ivProductImage = findViewById(R.id.ivProductImage);
                com.example.quanlycuahangthoitrang.utils.ImageLoader.load(ivProductImage, selectedImages.get(0));
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Đã chọn " + selectedImages.size() + " ảnh mới", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String copyToInternal(android.net.Uri uri) {
        try {
            java.io.File dir = new java.io.File(getFilesDir(), "product_images");
            if (!dir.exists()) dir.mkdirs();
            String fileName = "IMG_" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0,5) + ".jpg";
            java.io.File dest = new java.io.File(dir, fileName);
            
            java.io.InputStream is = getContentResolver().openInputStream(uri);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) fos.write(buffer, 0, length);
            is.close();
            fos.close();
            return dest.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}