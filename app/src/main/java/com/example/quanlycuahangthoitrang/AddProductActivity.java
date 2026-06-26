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
        // Hàm khởi tạo chạy đầu tiên khi mở Activity
        super.onCreate(savedInstanceState);
        // Nạp giao diện từ file XML
        setContentView(R.layout.activity_add_product);

        // Khởi tạo bộ công cụ thao tác với CSDL
        dbHelper = new DatabaseHelper(this);

        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        // Bắt sự kiện bấm nút quay lại
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

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
        for (com.example.quanlycuahangthoitrang.model.Category c : categoryList) {
            categoryNames.add(c.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryNames);
        spnCategory.setAdapter(adapter);

        final int[] selectedImageResId = {0};

                findViewById(R.id.btnChooseImage).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.putExtra(android.content.Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, 100);
        });

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            // Lấy dữ liệu từ các ô nhập
            String name = edtProductName.getText().toString().trim();
            String category = spnCategory.getSelectedItem() != null ? spnCategory.getSelectedItem().toString() : "";
            String priceStr = edtPrice.getText().toString().trim();
            String stockStr = edtStock.getText().toString().trim();
            String color = edtColor.getText().toString().trim();
            String sizes = edtSizes.getText().toString().trim();
            String desc = edtDescription.getText().toString().trim();

            // Kiểm tra các trường bắt buộc (Tên, Danh mục, Giá, Số lượng)
            if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // GÁN GIÁ TRỊ MẶC ĐỊNH NẾU QUÊN NHẬP MÀU
            // Tùy theo loại hàng hóa mà gán màu mặc định khác nhau cho hợp lý
            if (color.isEmpty()) {
                String catStr = category.toLowerCase();
                if (catStr.contains("áo") || catStr.contains("quần")) color = "Đen,Trắng";
                else if (catStr.contains("giày")) color = "Trắng,Đen";
                else color = "Đen"; // Phụ kiện thường mặc định đen
            }
            
            // GÁN GIÁ TRỊ MẶC ĐỊNH NẾU QUÊN NHẬP KÍCH CỠ
            if (sizes.isEmpty()) {
                String catStr = category.toLowerCase();
                if (catStr.contains("áo")) sizes = "S,M,L,XL,XXL"; // Áo có size chữ
                else if (catStr.contains("quần")) sizes = "28,29,30,31,32,33"; // Quần có size số
                else if (catStr.contains("giày")) sizes = "38,39,40,41,42,43"; // Giày size lớn hơn
                else sizes = "Freesize"; // Phụ kiện thì Freesize
            }
            
            if (desc.isEmpty()) desc = "Chưa có mô tả";

            int price = 0;
            int stock = 0;
            // Chuyển đổi Giá và Số lượng từ dạng Chữ (String) sang dạng Số (int)
            // Phải dùng try-catch để phòng trường hợp người dùng nhập chữ cái vào ô số gây lỗi ứng dụng (Crash)
            try {
                price = Integer.parseInt(priceStr);
                stock = Integer.parseInt(stockStr);
                if (price <= 0 || stock < 0) throw new NumberFormatException(); // Bắt lỗi số âm
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá phải > 0 và số lượng phải >= 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tiến hành tạo đối tượng Product để lưu vào CSDL
            Product p = new Product(0, name, category, price, color, sizes, desc, 0, stock);
            if (selectedImages.isEmpty()) {
                String catStr = category.toLowerCase();
                if (catStr.contains("áo thun") || catStr.contains("ao thun")) selectedImages.add("tshirt_1");
                else if (catStr.contains("áo len") || catStr.contains("ao len")) selectedImages.add("sweater_1");
                else if (catStr.contains("áo") || catStr.contains("ao")) selectedImages.add("shirt_1");
                else if (catStr.contains("cạp cao") || catStr.contains("cap cao")) selectedImages.add("jeans2_1");
                else if (catStr.contains("quần") || catStr.contains("quan")) selectedImages.add("jeans_1");
                else if (catStr.contains("phối màu") || catStr.contains("phoi mau")) selectedImages.add("shoes2_1");
                else if (catStr.contains("giày") || catStr.contains("giay")) selectedImages.add("shoes_1");
                else if (catStr.contains("kính") || catStr.contains("kinh")) selectedImages.add("glasses_1");
                else if (catStr.contains("túi") || catStr.contains("tui") || catStr.contains("phu kien")) selectedImages.add("bag_1");
                else selectedImages.add("shirt_1");
            }
            p.setImages(selectedImages);

            if (dbHelper.insertProduct(p)) {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // Hiện thông báo (Toast) cho người dùng
                Toast.makeText(this, "Lỗi khi thêm sản phẩm", Toast.LENGTH_SHORT).show();
            }
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
                Toast.makeText(this, "Đã chọn " + selectedImages.size() + " ảnh", Toast.LENGTH_SHORT).show();
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