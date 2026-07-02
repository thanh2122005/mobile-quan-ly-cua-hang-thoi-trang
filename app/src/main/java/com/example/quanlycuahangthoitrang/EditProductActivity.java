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

        Toast.makeText(this, "Mẹo: Nhấn giữ ô [Tồn kho] để quản lý kho chi tiết (Màu/Size)", Toast.LENGTH_LONG).show();
        edtStock.setOnLongClickListener(v -> {
            openVariantDialog();
            return true;
        });

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

    // Hàm được tự động gọi lại sau khi người dùng vừa chọn file ảnh từ Thư viện (Gallery) xong
    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // Kiểm tra đúng mã yêu cầu (100), kết quả chọn thành công (RESULT_OK) và có dữ liệu trả về
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            
            // Xử lý trường hợp CHỌN NHIỀU ẢNH (ClipData)
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount(); // Đếm xem chọn bao nhiêu ảnh
                for (int i = 0; i < count; i++) {
                    // Lấy ra địa chỉ (URI) tạm thời của từng ảnh
                    android.net.Uri uri = data.getClipData().getItemAt(i).getUri();
                    
                    // GỌI HÀM copyToInternal để sao chép ảnh từ bộ nhớ điện thoại vào thư mục nội bộ của App
                    String path = copyToInternal(uri);
                    
                    // Nếu copy thành công thì lưu đường dẫn mới vào mảng selectedImages
                    if (path != null) selectedImages.add(path);
                }
            } 
            // Xử lý trường hợp CHỌN 1 ẢNH DUY NHẤT (Data thường)
            else if (data.getData() != null) {
                String path = copyToInternal(data.getData());
                if (path != null) selectedImages.add(path);
            }
            
            // Sau khi chọn và copy xong, lấy ảnh ĐẦU TIÊN (get(0)) hiển thị lên màn hình
            if (!selectedImages.isEmpty()) {
                android.widget.ImageView ivProductImage = findViewById(R.id.ivProductImage);
                com.example.quanlycuahangthoitrang.utils.ImageLoader.load(ivProductImage, selectedImages.get(0));
                Toast.makeText(this, "Đã chọn " + selectedImages.size() + " ảnh mới", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Hàm cực kỳ quan trọng: COPY ảnh từ bộ nhớ công khai vào bộ nhớ an toàn (Internal Storage) của ứng dụng
    // Tránh việc người dùng xóa mất ảnh trong Bộ sưu tập (Gallery) làm App không hiển thị được nữa
    private String copyToInternal(android.net.Uri uri) {
        try {
            // Bước 1: Tạo một thư mục ẩn tên là "product_images" bên trong CSDL của ứng dụng
            java.io.File dir = new java.io.File(getFilesDir(), "product_images");
            if (!dir.exists()) dir.mkdirs(); // Nếu thư mục chưa có thì tự động tạo
            
            // Bước 2: Băm tên file để tránh bị trùng lặp (Ví dụ: IMG_16999999_123ab.jpg)
            String fileName = "IMG_" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0,5) + ".jpg";
            
            // Tạo file đích (Rỗng)
            java.io.File dest = new java.io.File(dir, fileName);
            
            // Bước 3: Dùng Luồng (Stream) để đọc dữ liệu từ file gốc và Ghi vào file đích
            java.io.InputStream is = getContentResolver().openInputStream(uri);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(dest);
            
            byte[] buffer = new byte[1024]; // Mỗi lần múc 1KB dữ liệu
            int length;
            // Vòng lặp: Đọc và đổ dữ liệu liên tục cho đến khi cạn file
            while ((length = is.read(buffer)) > 0) fos.write(buffer, 0, length);
            
            // Đóng luồng cho an toàn bộ nhớ
            is.close();
            fos.close();
            
            // Trả về địa chỉ tuyệt đối của file ảnh MỚI để lưu thẳng vào Database
            return dest.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Báo lỗi nếu copy thất bại
        }
    }

    private void openVariantDialog() {
        String colorsStr = ((android.widget.EditText) findViewById(R.id.edtColor)).getText().toString().trim();
        String sizesStr = ((android.widget.EditText) findViewById(R.id.edtSizes)).getText().toString().trim();
        
        if (colorsStr.isEmpty() || sizesStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Màu sắc và Kích cỡ trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] colors = colorsStr.split(",");
        String[] sizes = sizesStr.split(",");
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Quản lý tồn kho theo Phân loại");

        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 32, 50, 32);

        java.util.List<android.widget.EditText> inputs = new java.util.ArrayList<>();
        java.util.List<String[]> combos = new java.util.ArrayList<>();

        for (String c : colors) {
            String color = c.trim();
            if (color.isEmpty()) continue;
            for (String s : sizes) {
                String size = s.trim();
                if (size.isEmpty()) continue;

                android.widget.TextView tv = new android.widget.TextView(this);
                tv.setText("Màu: " + color + " - Size: " + size);
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
                tv.setPadding(0, 20, 0, 5);
                layout.addView(tv);

                android.widget.EditText edt = new android.widget.EditText(this);
                edt.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                int existingStock = dbHelper.getVariantStock(product.getId(), color, size);
                edt.setText(String.valueOf(existingStock == -1 ? 0 : existingStock));
                layout.addView(edt);
                
                inputs.add(edt);
                combos.add(new String[]{color, size});
            }
        }

        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            int totalVariantStock = 0;
            for (int i = 0; i < inputs.size(); i++) {
                String val = inputs.get(i).getText().toString();
                int stock = val.isEmpty() ? 0 : Integer.parseInt(val);
                totalVariantStock += stock;
                String[] combo = combos.get(i);
                dbHelper.updateVariantStock(product.getId(), combo[0], combo[1], stock);
            }
            
            // Cập nhật lại kho tổng hiển thị trên UI
            ((android.widget.EditText) findViewById(R.id.edtStock)).setText(String.valueOf(totalVariantStock));
            Toast.makeText(this, "Đã lưu tồn kho chi tiết!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}