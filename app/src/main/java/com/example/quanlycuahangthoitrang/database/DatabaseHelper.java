package com.example.quanlycuahangthoitrang.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.quanlycuahangthoitrang.R;
import com.example.quanlycuahangthoitrang.model.CartItem;
import com.example.quanlycuahangthoitrang.model.Category;
import com.example.quanlycuahangthoitrang.model.Order;
import com.example.quanlycuahangthoitrang.model.OrderItem;
import com.example.quanlycuahangthoitrang.model.Product;
import com.example.quanlycuahangthoitrang.model.User;
import com.example.quanlycuahangthoitrang.model.Voucher;
import com.example.quanlycuahangthoitrang.utils.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FashionStore.db";
    private static final int DATABASE_VERSION = 13;
    private static final String ORDER_STATUS_PENDING = "Chờ xác nhận";
    private static final String ORDER_STATUS_CONFIRMED = "Đã xác nhận";
    private static final String ORDER_STATUS_SHIPPING = "Đang giao";
    private static final String ORDER_STATUS_COMPLETED = "Hoàn thành";
    private static final String ORDER_STATUS_CANCELLED = "Đã hủy";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Tự động tạo bảng product_variants để quản lý tồn kho theo màu/size (Không cần tăng DB_VERSION)
        db.execSQL("CREATE TABLE IF NOT EXISTS product_variants (id INTEGER PRIMARY KEY AUTOINCREMENT, product_id INTEGER, color TEXT, size TEXT, stock INTEGER)");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Bắt đầu tạo database...");

        // 1. Table users
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "phone TEXT, " +
                "address TEXT, " +
                "role TEXT)");

        // 2. Table categories
        db.execSQL("CREATE TABLE categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT UNIQUE, " +
                "imageResId INTEGER)");

        // 3. Table products
        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "categoryId INTEGER, " +
                "categoryName TEXT, " +
                "price INTEGER, " +
                "stock INTEGER, " +
                "color TEXT, " +
                "sizes TEXT, " +
                "description TEXT, " +
                "imageResId INTEGER, " +
                "FOREIGN KEY (categoryId) REFERENCES categories(id) ON DELETE SET NULL)");

        // 3a. Table product_images
        db.execSQL("CREATE TABLE product_images (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productId INTEGER, " +
                "imagePath TEXT, " +
                "sortOrder INTEGER, " +
                "FOREIGN KEY (productId) REFERENCES products(id) ON DELETE CASCADE)");

        // 4. Table cart_items
        db.execSQL("CREATE TABLE cart_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER, " +
                "productId INTEGER, " +
                "quantity INTEGER, " +
                "selectedColor TEXT, " +
                "selectedSize TEXT, " +
                "FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (productId) REFERENCES products(id) ON DELETE CASCADE)");



        // 7. Table orders
        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT, " +
                "userId INTEGER, " +
                "receiverName TEXT, " +
                "phone TEXT, " +
                "address TEXT, " +
                "note TEXT, " +
                "createdAt TEXT, " +
                "total INTEGER, " +
                "status TEXT, " +
                "freeshipVoucherCode TEXT, " +
                "discountVoucherCode TEXT, " +
                "FOREIGN KEY (userId) REFERENCES users(id) ON DELETE SET NULL)");

        // 8. Table order_details
        db.execSQL("CREATE TABLE order_details (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "orderId INTEGER, " +
                "productId INTEGER, " +
                "productName TEXT, " +
                "quantity INTEGER, " +
                "unitPrice INTEGER, " +
                "subtotal INTEGER, " +
                "selectedColor TEXT, " +
                "selectedSize TEXT, " +
                "FOREIGN KEY (orderId) REFERENCES orders(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (productId) REFERENCES products(id) ON DELETE SET NULL)");

        // 9. Table vouchers
        db.execSQL("CREATE TABLE vouchers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT UNIQUE, " +
                "type TEXT, " +
                "value INTEGER, " +
                "minOrder INTEGER, " +
                "usageLimit INTEGER, " +
                "usedCount INTEGER)");

        // 10. Table reviews
        db.execSQL("CREATE TABLE reviews (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER, " +
                "productId INTEGER, " +
                "rating INTEGER, " +
                "comment TEXT, " +
                "createdAt TEXT, " +
                "FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (productId) REFERENCES products(id) ON DELETE CASCADE)");

        insertInitialData(db);
        Log.d("DatabaseHelper", "Khởi tạo database thành công!");
    }

    private void insertInitialData(SQLiteDatabase db) {
        db.execSQL("INSERT OR IGNORE INTO users (name, email, password, phone, address, role) VALUES ('Admin', 'admin@gmail.com', '123456', '0123456789', 'Hà Nội', 'admin')");
        db.execSQL("INSERT OR IGNORE INTO users (name, email, password, phone, address, role) VALUES ('Thành', 'user@gmail.com', '123456', '0987654321', 'Hải Dương', 'user')");

        db.execSQL("INSERT OR IGNORE INTO categories (name, imageResId) VALUES ('Áo', 0)");
        db.execSQL("INSERT OR IGNORE INTO categories (name, imageResId) VALUES ('Quần', 0)");
        db.execSQL("INSERT OR IGNORE INTO categories (name, imageResId) VALUES ('Giày', 0)");
        db.execSQL("INSERT OR IGNORE INTO categories (name, imageResId) VALUES ('Phụ kiện', 0)");

        db.execSQL("INSERT OR IGNORE INTO vouchers (code, type, value, minOrder, usageLimit, usedCount) VALUES ('FREESHIP', 'freeship', 30000, 0, 100, 0)");
        db.execSQL("INSERT OR IGNORE INTO vouchers (code, type, value, minOrder, usageLimit, usedCount) VALUES ('GIAM50K', 'discount', 50000, 200000, 50, 0)");

        insertOldProducts(db);
        insertNewProducts(db);
        insertSampleOrders(db);
    }

    private void insertSampleOrders(SQLiteDatabase db) {
        // Đơn 1: Hoàn thành (Tháng 5/2026 để có dữ liệu so sánh)
        db.execSQL("INSERT OR IGNORE INTO orders (id, code, userId, receiverName, phone, address, note, createdAt, total, status, freeshipVoucherCode, discountVoucherCode) VALUES (1, 'DH001', 2, 'Thành', '0987654321', 'Hải Dương', 'Giao giờ hành chính', '15/05/2026 10:30', 569000, 'Hoàn thành', null, null)");
        db.execSQL("INSERT OR IGNORE INTO order_details (orderId, productId, productName, quantity, unitPrice, subtotal, selectedColor, selectedSize) VALUES (1, 1, 'Áo len cổ tròn basic nam nữ unisex', 1, 299000, 299000, 'Xám', 'XL')");
        db.execSQL("INSERT OR IGNORE INTO order_details (orderId, productId, productName, quantity, unitPrice, subtotal, selectedColor, selectedSize) VALUES (1, 3, 'Áo thun cotton trơn thoáng mát', 2, 120000, 240000, 'Trắng', 'XL')");

        // Đơn 2: Hoàn thành (Tháng 6/2026)
        db.execSQL("INSERT OR IGNORE INTO orders (id, code, userId, receiverName, phone, address, note, createdAt, total, status, freeshipVoucherCode, discountVoucherCode) VALUES (2, 'DH002', 2, 'Thành', '0987654321', 'Hải Dương', '', '10/06/2026 08:15', 890000, 'Hoàn thành', 'FREESHIP', null)");
        db.execSQL("INSERT OR IGNORE INTO order_details (orderId, productId, productName, quantity, unitPrice, subtotal, selectedColor, selectedSize) VALUES (2, 6, 'Giày Sneaker thể thao năng động', 1, 890000, 890000, 'Đen', '41')");

        // Đơn 3: Đang giao
        db.execSQL("INSERT OR IGNORE INTO orders (id, code, userId, receiverName, phone, address, note, createdAt, total, status, freeshipVoucherCode, discountVoucherCode) VALUES (3, 'DH003', 2, 'Thành', '0987654321', 'Ký túc xá Đại học', 'Gọi trước khi giao', '24/06/2026 14:20', 1279000, 'Đang giao', null, 'GIAM50K')");
        db.execSQL("INSERT OR IGNORE INTO order_details (orderId, productId, productName, quantity, unitPrice, subtotal, selectedColor, selectedSize) VALUES (3, 8, 'Túi đơn giản và sang trọng', 1, 1299000, 1299000, 'Nâu', 'Freesize')");

        // Đơn 4: Đã xác nhận
        db.execSQL("INSERT OR IGNORE INTO orders (id, code, userId, receiverName, phone, address, note, createdAt, total, status, freeshipVoucherCode, discountVoucherCode) VALUES (4, 'DH004', 2, 'Nguyễn Văn A', '0912345678', 'Hà Nội', '', '25/06/2026 16:45', 529000, 'Đã xác nhận', null, null)");
        db.execSQL("INSERT OR IGNORE INTO order_details (orderId, productId, productName, quantity, unitPrice, subtotal, selectedColor, selectedSize) VALUES (4, 5, 'Quần Jeans cạp cao', 1, 499000, 499000, 'Xanh nhạt', '30')");

        // Đơn 5: Chờ xác nhận
        db.execSQL("INSERT OR IGNORE INTO orders (id, code, userId, receiverName, phone, address, note, createdAt, total, status, freeshipVoucherCode, discountVoucherCode) VALUES (5, 'DH005', 2, 'Thành', '0987654321', 'Hải Dương', 'Đóng gói cẩn thận', '26/06/2026 09:00', 380000, 'Chờ xác nhận', null, null)");
        db.execSQL("INSERT OR IGNORE INTO order_details (orderId, productId, productName, quantity, unitPrice, subtotal, selectedColor, selectedSize) VALUES (5, 2, 'Áo sơ mi nam', 1, 350000, 350000, 'Trắng', 'L')");

        // Đơn 6: Đã hủy
        db.execSQL("INSERT OR IGNORE INTO orders (id, code, userId, receiverName, phone, address, note, createdAt, total, status, freeshipVoucherCode, discountVoucherCode) VALUES (6, 'DH006', 2, 'Thành', '0987654321', 'Hải Dương', 'Đổi ý không mua nữa', '20/06/2026 11:00', 480000, 'Đã hủy', null, null)");
        db.execSQL("INSERT OR IGNORE INTO order_details (orderId, productId, productName, quantity, unitPrice, subtotal, selectedColor, selectedSize) VALUES (6, 4, 'Quần Jeans Denim dáng suông rộng', 1, 450000, 450000, 'Xanh denim', '31')");
    }

    private void insertOldProducts(SQLiteDatabase db) {
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Áo len cổ tròn basic nam nữ unisex', 1, 'Áo', 299000, 12, 'Xám,Đen', 'S,M,L,XL,XXL', 'Áo len cổ tròn chất liệu mềm mại, giữ ấm tốt.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Áo sơ mi nam', 1, 'Áo', 350000, 8, 'Trắng,Xanh nhạt', 'S,M,L,XL,XXL', 'Áo sơ mi nam form gọn, chất vải thoáng.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Áo thun cotton trơn thoáng mát', 1, 'Áo', 120000, 20, 'Đen,Trắng,Xám', 'S,M,L,XL,XXL', 'Áo thun cotton trơn, thấm hút tốt.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Quần Jeans Denim dáng suông rộng', 2, 'Quần', 450000, 10, 'Xanh denim,Xanh đậm', '28,29,30,31,32,33', 'Quần jeans denim dáng suông rộng, chất vải bền.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Quần Jeans cạp cao', 2, 'Quần', 499000, 6, 'Xanh nhạt,Xanh đậm', '28,29,30,31,32,33', 'Quần jeans cạp cao giúp tôn dáng.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Giày Sneaker thể thao năng động', 3, 'Giày', 890000, 5, 'Trắng,Đen', '38,39,40,41,42,43', 'Giày sneaker thể thao êm chân.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Giày sneaker phối màu', 3, 'Giày', 809000, 3, 'Trắng phối xanh,Trắng phối đỏ', '38,39,40,41,42,43', 'Giày sneaker phối màu trẻ trung.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Túi đơn giản và sang trọng', 4, 'Phụ kiện', 1299000, 4, 'Nâu,Đen', 'Freesize', 'Túi xách thiết kế đơn giản, sang trọng.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Kính râm gọng tròn', 4, 'Phụ kiện', 879000, 2, 'Đen,Nâu', 'Freesize', 'Kính râm gọng tròn phong cách thời trang.', 0)");

        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (1, 'ao_len_co_tron_1', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (1, 'ao_len_co_tron_2', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (1, 'ao_len_co_tron_3', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (2, 'shirt_1', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (2, 'shirt_2', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (2, 'shirt_3', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (3, 'tshirt_1', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (3, 'tshirt_2', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (3, 'tshirt_3', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (4, 'quan_jeans_dang_suong_1', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (4, 'quan_jeans_dang_suong_2', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (4, 'quan_jeans_dang_suong_3', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (5, 'quan_jeans_cap_cao_1', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (5, 'quan_jeans_cap_cao_2', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (5, 'quan_jeans_cap_cao_3', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (6, 'shoes_1', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (6, 'shoes_2', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (6, 'shoes_3', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (7, 'shoes2_1', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (7, 'shoes2_2', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (7, 'shoes2_3', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (8, 'bag_1', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (8, 'bag_2', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (8, 'bag_3', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (9, 'kinh_ram_gong_tron_1', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (9, 'kinh_ram_gong_tron_2', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (9, 'kinh_ram_gong_tron_3', 3)");
    }

    private void insertNewProducts(SQLiteDatabase db) {
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Áo len (Mới)', 1, 'Áo', 350000, 20, 'Đen,Trắng', 'S,M,L,XL', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Áo sơ mi (Mới)', 1, 'Áo', 250000, 20, 'Đen,Trắng', 'S,M,L,XL', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Áo thun (Mới)', 1, 'Áo', 150000, 20, 'Đen,Trắng', 'S,M,L,XL', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Áo polo (Mới)', 1, 'Áo', 200000, 20, 'Đen,Trắng', 'S,M,L,XL', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Hoodie (Mới)', 1, 'Áo', 450000, 20, 'Đen,Trắng', 'S,M,L,XL', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Quần jeans cạp cao (Mới)', 2, 'Quần', 350000, 20, 'Đen,Trắng', '28,29,30,31,32', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Quần jeans ống rộng (Mới)', 2, 'Quần', 400000, 20, 'Đen,Trắng', '28,29,30,31,32', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Quần jogger (Mới)', 2, 'Quần', 250000, 20, 'Đen,Trắng', '28,29,30,31,32', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Quần kaki (Mới)', 2, 'Quần', 300000, 20, 'Đen,Trắng', '28,29,30,31,32', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Giày sneaker (Mới)', 3, 'Giày', 800000, 20, 'Đen,Trắng', '39,40,41,42', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Giày running (Mới)', 3, 'Giày', 950000, 20, 'Đen,Trắng', '39,40,41,42', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Giày canvas (Mới)', 3, 'Giày', 500000, 20, 'Đen,Trắng', '39,40,41,42', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Kính râm (Mới)', 4, 'Phụ kiện', 250000, 20, 'Đen,Trắng', 'Freesize', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Túi xách (Mới)', 4, 'Phụ kiện', 550000, 20, 'Đen,Trắng', 'Freesize', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");
        db.execSQL("INSERT OR IGNORE INTO products (name, categoryId, categoryName, price, stock, color, sizes, description, imageResId) VALUES ('Balo (Mới)', 4, 'Phụ kiện', 650000, 20, 'Đen,Trắng', 'Freesize', 'Sản phẩm chất lượng cao, hình ảnh chụp thực tế.', 0)");

        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (10, 'assets/products/ao_len/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (10, 'assets/products/ao_len/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (10, 'assets/products/ao_len/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (11, 'assets/products/ao_so_mi/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (11, 'assets/products/ao_so_mi/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (11, 'assets/products/ao_so_mi/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (12, 'assets/products/ao_thun/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (12, 'assets/products/ao_thun/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (12, 'assets/products/ao_thun/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (13, 'assets/products/ao_polo/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (13, 'assets/products/ao_polo/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (13, 'assets/products/ao_polo/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (14, 'assets/products/hoodie/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (14, 'assets/products/hoodie/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (14, 'assets/products/hoodie/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (15, 'assets/products/jeans_cap_cao/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (15, 'assets/products/jeans_cap_cao/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (15, 'assets/products/jeans_cap_cao/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (16, 'assets/products/jeans_ong_rong/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (16, 'assets/products/jeans_ong_rong/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (16, 'assets/products/jeans_ong_rong/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (17, 'assets/products/jogger/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (17, 'assets/products/jogger/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (17, 'assets/products/jogger/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (18, 'assets/products/kaki/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (18, 'assets/products/kaki/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (18, 'assets/products/kaki/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (19, 'assets/products/sneaker/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (19, 'assets/products/sneaker/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (19, 'assets/products/sneaker/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (20, 'assets/products/running/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (20, 'assets/products/running/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (20, 'assets/products/running/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (21, 'assets/products/canvas/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (21, 'assets/products/canvas/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (21, 'assets/products/canvas/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (22, 'assets/products/kinh/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (22, 'assets/products/kinh/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (22, 'assets/products/kinh/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (23, 'assets/products/tui_xach/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (23, 'assets/products/tui_xach/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (23, 'assets/products/tui_xach/3.jpg', 3)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (24, 'assets/products/balo/1.jpg', 1)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (24, 'assets/products/balo/2.jpg', 2)");
        db.execSQL("INSERT INTO product_images (productId, imagePath, sortOrder) VALUES (24, 'assets/products/balo/3.jpg', 3)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS reviews");
        db.execSQL("DROP TABLE IF EXISTS order_details");
        db.execSQL("DROP TABLE IF EXISTS orders");

        db.execSQL("DROP TABLE IF EXISTS cart_items");
        db.execSQL("DROP TABLE IF EXISTS product_images");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS vouchers");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    private String normalizeOption(String value) {
        return value == null ? "" : value.trim();
    }

    private int getProductStock(SQLiteDatabase db, int productId) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT stock FROM products WHERE id=?", new String[]{String.valueOf(productId)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return -1;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // Cập nhật trực tiếp số lượng tồn kho của sản phẩm
    private boolean updateProductStock(SQLiteDatabase db, int productId, int newStock) {
        // Không cho phép tồn kho nhỏ hơn 0
        if (newStock < 0) return false;

        // Tạo đối tượng chứa dữ liệu cần cập nhật
        ContentValues cv = new ContentValues();

        // Gán giá trị mới cho cột "stock"
        cv.put("stock", newStock);

        // Thực hiện UPDATE products SET stock = newStock WHERE id = productId
        // Trả về true nếu có ít nhất 1 dòng được cập nhật
        return db.update(
                "products",
                cv,
                "id=?",
                new String[]{String.valueOf(productId)}
        ) > 0;
    }

    // Điều chỉnh số lượng tồn kho dựa trên mức thay đổi (delta)
    // delta > 0: nhập thêm hàng
    // delta < 0: bán hoặc xuất kho
    private boolean adjustProductStock(SQLiteDatabase db, int productId, int delta) {
        // Lấy số lượng tồn kho hiện tại của sản phẩm
        int currentStock = getProductStock(db, productId);

        // Nếu không tìm thấy sản phẩm hoặc xảy ra lỗi thì dừng
        if (currentStock < 0) return false;

        // Tính số lượng tồn kho mới rồi cập nhật vào cơ sở dữ liệu
        return updateProductStock(db, productId, currentStock + delta);
    }

    private Map<Integer, Integer> aggregateCartQuantities(List<CartItem> items) {
        Map<Integer, Integer> quantities = new HashMap<>();
        if (items == null) return quantities;

        for (CartItem item : items) {
            if (item == null || item.getProduct() == null) continue;
            int productId = item.getProduct().getId();
            quantities.put(productId, quantities.getOrDefault(productId, 0) + item.getQuantity());
        }
        return quantities;
    }


    // Lấy tồn kho theo màu/size cụ thể (nếu không có bản ghi thì lấy tồn kho tổng)
    public int getVariantStock(int productId, String color, String size) {
        SQLiteDatabase db = this.getReadableDatabase();
        int stock = -1;
        String query = "SELECT stock FROM product_variants WHERE product_id = ? AND color = ? AND size = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId), normalizeOption(color), normalizeOption(size)});
        if (cursor != null && cursor.moveToFirst()) {
            stock = cursor.getInt(0);
        }
        if (cursor != null) cursor.close();
        
        if (stock == -1) {
            return getProductStock(db, productId); // Rơi về tồn kho tổng nếu chưa cấu hình biến thể
        }
        return stock;
    }

    // Cập nhật tồn kho theo màu/size
    public void updateVariantStock(int productId, String color, String size, int newStock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("product_id", productId);
        cv.put("color", normalizeOption(color));
        cv.put("size", normalizeOption(size));
        cv.put("stock", newStock);
        
        int rows = db.update("product_variants", cv, "product_id = ? AND color = ? AND size = ?", new String[]{String.valueOf(productId), normalizeOption(color), normalizeOption(size)});
        if (rows == 0) {
            db.insert("product_variants", null, cv);
        }
    }

    // Đã thay đổi logic hasSufficientStock thành check theo biến thể
    private boolean hasSufficientStock(SQLiteDatabase db, java.util.List<CartItem> items) {
        // Cộng dồn yêu cầu theo Key: (productId_color_size)
        java.util.Map<String, Integer> requiredMap = new java.util.HashMap<>();
        for (CartItem item : items) {
            String key = item.getProduct().getId() + "_" + normalizeOption(item.getSelectedColor()) + "_" + normalizeOption(item.getSelectedSize());
            requiredMap.put(key, requiredMap.getOrDefault(key, 0) + item.getQuantity());
        }

        for (CartItem item : items) {
            String key = item.getProduct().getId() + "_" + normalizeOption(item.getSelectedColor()) + "_" + normalizeOption(item.getSelectedSize());
            int required = requiredMap.get(key);
            int available = getVariantStock(item.getProduct().getId(), item.getSelectedColor(), item.getSelectedSize());
            if (available < required) {
                return false;
            }
        }
        return true;
    }

    private int getCartQuantityForProduct(SQLiteDatabase db, int userId, int productId, String excludeColor, String excludeSize) {
        if (userId <= 0) return 0;

        ArrayList<String> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE userId=? AND productId=?");
        args.add(String.valueOf(userId));
        args.add(String.valueOf(productId));

        if (excludeColor != null && excludeSize != null) {
            sql.append(" AND NOT (selectedColor=? AND selectedSize=?)");
            args.add(normalizeOption(excludeColor));
            args.add(normalizeOption(excludeSize));
        }

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql.toString(), args.toArray(new String[0]));
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private boolean removeCartItem(SQLiteDatabase db, int userId, int productId, String selectedColor, String selectedSize) {
        return db.delete(
                "cart_items",
                "userId=? AND productId=? AND selectedColor=? AND selectedSize=?",
                new String[]{
                        String.valueOf(userId),
                        String.valueOf(productId),
                        normalizeOption(selectedColor),
                        normalizeOption(selectedSize)
                }
        ) > 0;
    }

    private Voucher getVoucherByCode(SQLiteDatabase db, String code) {
        if (code == null || code.trim().isEmpty()) return null;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM vouchers WHERE code=?", new String[]{code.trim()});
            if (cursor != null && cursor.moveToFirst()) {
                return new Voucher(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getInt(5),
                        cursor.getInt(6)
                );
            }
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private boolean changeVoucherUsage(SQLiteDatabase db, String code, int delta) {
        if (code == null || code.trim().isEmpty()) return true;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT usedCount, usageLimit FROM vouchers WHERE code=?", new String[]{code.trim()});
            if (cursor == null || !cursor.moveToFirst()) {
                return false;
            }

            int usedCount = cursor.getInt(0);
            int usageLimit = cursor.getInt(1);
            int nextCount = usedCount + delta;
            if (nextCount < 0 || nextCount > usageLimit) {
                return false;
            }

            ContentValues cv = new ContentValues();
            cv.put("usedCount", nextCount);
            return db.update("vouchers", cv, "code=?", new String[]{code.trim()}) > 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private boolean claimVoucher(SQLiteDatabase db, String code, String expectedType, int subtotal) {
        if (code == null || code.trim().isEmpty()) return true;

        Voucher voucher = getVoucherByCode(db, code);
        if (voucher == null) return false;
        if (!expectedType.equals(voucher.getType())) return false;
        if (subtotal < voucher.getMinOrder()) return false;

        return changeVoucherUsage(db, code, 1);
    }

    private void releaseOrderVouchers(SQLiteDatabase db, int orderId) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT freeshipVoucherCode, discountVoucherCode FROM orders WHERE id=?", new String[]{String.valueOf(orderId)});
            if (cursor != null && cursor.moveToFirst()) {
                changeVoucherUsage(db, cursor.getString(0), -1);
                changeVoucherUsage(db, cursor.getString(1), -1);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private boolean isValidOrderStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null || currentStatus.equals(newStatus)) {
            return false;
        }

        switch (currentStatus) {
            case ORDER_STATUS_PENDING:
                return ORDER_STATUS_CONFIRMED.equals(newStatus) || ORDER_STATUS_CANCELLED.equals(newStatus);
            case ORDER_STATUS_CONFIRMED:
                return ORDER_STATUS_SHIPPING.equals(newStatus) || ORDER_STATUS_CANCELLED.equals(newStatus);
            case ORDER_STATUS_SHIPPING:
                return ORDER_STATUS_COMPLETED.equals(newStatus);
            default:
                return false;
        }
    }

    private int getOrderItemsSubtotal(SQLiteDatabase db, int orderId) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COALESCE(SUM(subtotal), 0) FROM order_details WHERE orderId=?", new String[]{String.valueOf(orderId)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void updateOrderTotal(SQLiteDatabase db, int orderId, int preservedAdjustment) {
        int newTotal = getOrderItemsSubtotal(db, orderId) + preservedAdjustment;
        if (newTotal < 0) {
            newTotal = 0;
        }

        ContentValues cvOrder = new ContentValues();
        cvOrder.put("total", newTotal);
        db.update("orders", cvOrder, "id=?", new String[]{String.valueOf(orderId)});
    }

    // PHẦN 3: METHOD CHO USER

    // Hàm kiểm tra xem thông tin Đăng nhập (Email và Mật khẩu) có khớp trong Cơ sở dữ liệu hay không
    public boolean checkLogin(String email, String password) {
        // Mở kết nối đến cơ sở dữ liệu ở chế độ "Chỉ Đọc" (Vì mình chỉ cần kiểm tra, không cần sửa đổi)
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Khởi tạo một con trỏ (Cursor) dùng để duyệt qua các kết quả trả về từ CSDL
        Cursor cursor = null;
        try {
            // Thực thi câu lệnh SQL để tìm xem có dòng nào trong bảng "users" khớp đúng cả Email và Password không
            cursor = db.rawQuery("SELECT id FROM users WHERE email=? AND password=?", new String[]{email, password});
            
            // Nếu con trỏ không rỗng VÀ có thể trỏ tới được dòng kết quả đầu tiên (nghĩa là có tài khoản tồn tại)
            // thì trả về True (Đăng nhập đúng), ngược lại trả về False (Sai email hoặc mật khẩu)
            return cursor != null && cursor.moveToFirst();
        } finally {
            // Khối lệnh này luôn luôn chạy dù code phía trên có bị lỗi hay không
            // Dùng để Đóng con trỏ lại, giải phóng bộ nhớ cho điện thoại để không bị giật lag
            if (cursor != null) cursor.close();
        }
    }

    // Hàm lấy ra Quyền (Role) của người dùng dựa vào Email
    // Giúp ứng dụng biết được người này là "admin" hay chỉ là "user" bình thường
    public String getUserRole(String email) {
        // Mở kết nối đến cơ sở dữ liệu ở chế độ "Chỉ Đọc"
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Khởi tạo một con trỏ (Cursor)
        Cursor cursor = null;
        try {
            // Dùng câu lệnh SQL lấy đúng cột "role" từ bảng "users" với điều kiện Email trùng khớp
            cursor = db.rawQuery("SELECT role FROM users WHERE email=?", new String[]{email});
            
            // Nếu có kết quả trả về từ cơ sở dữ liệu
            if (cursor != null && cursor.moveToFirst()) {
                // Lấy dữ liệu ở cột số 0 (tức là cột "role" vừa lấy ra) và chuyển thành dạng Chữ (String)
                return cursor.getString(0);
            }
            
            // Nếu không tìm thấy thì trả về chuỗi rỗng
            return "";
        } finally {
            // Luôn luôn nhớ đóng con trỏ sau khi truy vấn xong để tránh tràn bộ nhớ (Memory Leak)
            if (cursor != null) cursor.close();
        }
    }

    public boolean checkUserEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT id FROM users WHERE email=?", new String[]{email});
            return cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean resetPassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);
        return db.update("users", cv, "email=?", new String[]{email}) > 0;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users WHERE email=?", new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                return new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)
                );
            }
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", user.getName());
        cv.put("email", user.getEmail());
        cv.put("password", user.getPassword());
        cv.put("phone", user.getPhone());
        cv.put("address", user.getAddress());
        cv.put("role", user.getRole());
        long result = db.insert("users", null, cv);
        return result != -1;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", user.getName());
        cv.put("phone", user.getPhone());
        cv.put("address", user.getAddress());
        int result = db.update("users", cv, "email=?", new String[]{user.getEmail()});
        return result > 0;
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) {
        if (!checkLogin(email, oldPassword)) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", newPassword);
        int result = db.update("users", cv, "email=?", new String[]{email});
        return result > 0;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM users", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(new User(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6)
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public boolean deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Xóa các món hàng tạm trong Giỏ của user này (Tránh rác DB)
            db.delete("cart_items", "userId=?", new String[]{String.valueOf(id)});
            // Xóa user
            int result = db.delete("users", "id=?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return result > 0;
        } finally {
            db.endTransaction();
        }
    }

    public boolean updateUserRole(int id, String newRole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("role", newRole);
        int result = db.update("users", cv, "id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // ==========================================
    // PHẦN 4: METHOD CHO CATEGORY
    // ==========================================

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM categories", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(1);
                    String icon = "🏷️";
                    if (name != null) {
                        String lowerName = name.toLowerCase();
                        if (lowerName.contains("áo")) icon = "👕";
                        else if (lowerName.contains("quần")) icon = "👖";
                        else if (lowerName.contains("giày")) icon = "👟";
                        else if (lowerName.contains("phụ kiện")) icon = "🎒";
                    }
                    list.add(new Category(cursor.getInt(0), name, icon));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public Category getCategoryById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM categories WHERE id=?", new String[]{String.valueOf(id)});
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(1);
                String icon = "🏷️";
                if (name != null) {
                    String lowerName = name.toLowerCase();
                    if (lowerName.contains("áo")) icon = "👕";
                    else if (lowerName.contains("quần")) icon = "👖";
                    else if (lowerName.contains("giày")) icon = "👟";
                    else if (lowerName.contains("phụ kiện")) icon = "🎒";
                }
                return new Category(cursor.getInt(0), name, icon);
            }
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean insertCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", category.getName());
        cv.put("imageResId", 0);
        long result = db.insert("categories", null, cv);
        return result != -1;
    }

    public boolean updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", category.getName());
        
        db.beginTransaction();
        try {
            int result = db.update("categories", cv, "id=?", new String[]{String.valueOf(category.getId())});
            if (result > 0) {
                // Cập nhật tên danh mục mới cho tất cả sản phẩm thuộc danh mục này
                ContentValues cvProduct = new ContentValues();
                cvProduct.put("categoryName", category.getName());
                db.update("products", cvProduct, "categoryId=?", new String[]{String.valueOf(category.getId())});
                db.setTransactionSuccessful();
                return true;
            }
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public boolean deleteCategory(int categoryId) {
        Category cat = getCategoryById(categoryId);
        if (cat == null) return false;
        if (getProductCountByCategory(cat.getName()) > 0) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("categories", "id=?", new String[]{String.valueOf(categoryId)});
        return result > 0;
    }

    public int getProductCountByCategory(String categoryName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM products WHERE categoryName=?", new String[]{categoryName});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public ArrayList<Category> searchCategories(String keyword) {
        ArrayList<Category> list = getAllCategories();
        ArrayList<Category> result = new ArrayList<>();
        for (Category c : list) {
            if (FormatUtils.matchesSearch(keyword, c.getName())) {
                result.add(c);
            }
        }
        return result;
    }

    // ==========================================
    // PHẦN 5: METHOD CHO PRODUCT

    public java.util.ArrayList<String> getProductImages(int productId) {
        java.util.ArrayList<String> images = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT imagePath FROM product_images WHERE productId=? ORDER BY sortOrder ASC", new String[]{String.valueOf(productId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    images.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return images;
    }

    private void insertProductImages(SQLiteDatabase db, int productId, java.util.ArrayList<String> images) {
        db.delete("product_images", "productId=?", new String[]{String.valueOf(productId)});
        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put("productId", productId);
                cv.put("imagePath", images.get(i));
                cv.put("sortOrder", i + 1);
                db.insert("product_images", null, cv);
            }
        }
    }

    // ==========================================

    private Product extractProductFromCursor(Cursor cursor) {
        int idIdx = cursor.getColumnIndex("id");
        int nameIdx = cursor.getColumnIndex("name");
        int catIdx = cursor.getColumnIndex("categoryName");
        int priceIdx = cursor.getColumnIndex("price");
        int stockIdx = cursor.getColumnIndex("stock");
        int colorIdx = cursor.getColumnIndex("color");
        int sizesIdx = cursor.getColumnIndex("sizes");
        int descIdx = cursor.getColumnIndex("description");
        int imgResIdx = cursor.getColumnIndex("imageResId");

        return new Product(
                idIdx != -1 ? cursor.getInt(idIdx) : 0,
                nameIdx != -1 ? cursor.getString(nameIdx) : "",
                catIdx != -1 ? cursor.getString(catIdx) : "",
                priceIdx != -1 ? cursor.getInt(priceIdx) : 0,
                colorIdx != -1 ? cursor.getString(colorIdx) : "",
                sizesIdx != -1 ? cursor.getString(sizesIdx) : "Freesize",
                descIdx != -1 ? cursor.getString(descIdx) : "",
                imgResIdx != -1 ? cursor.getInt(imgResIdx) : 0,
                stockIdx != -1 ? cursor.getInt(stockIdx) : 0
        );
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM products", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Product p = extractProductFromCursor(cursor);
                    p.setImages(getProductImages(p.getId()));
                    list.add(p);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM products WHERE id=?", new String[]{String.valueOf(id)});
            if (cursor != null && cursor.moveToFirst()) {
                Product p = extractProductFromCursor(cursor);
                p.setImages(getProductImages(p.getId()));
                return p;
            }
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public ArrayList<Product> getProductsByCategory(String categoryName) {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM products WHERE categoryName=?", new String[]{categoryName});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Product p = extractProductFromCursor(cursor);
                    p.setImages(getProductImages(p.getId()));
                    list.add(p);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public ArrayList<Product> searchProducts(String keyword) {
        ArrayList<Product> all = getAllProducts();
        ArrayList<Product> result = new ArrayList<>();
        for (Product p : all) {
            if (FormatUtils.matchesSearch(keyword, p.getName(), p.getCategory(), p.getColor())) {
                result.add(p);
            }
        }
        return result;
    }

    public boolean insertProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", product.getName());
        cv.put("categoryName", product.getCategory());
        cv.put("price", product.getPrice());
        cv.put("stock", product.getStock());
        cv.put("color", product.getColor());
        cv.put("sizes", product.getSizes());
        cv.put("description", product.getDescription());
        cv.put("imageResId", product.getImageResId());
        long result = db.insert("products", null, cv);
        if (result != -1) {
            insertProductImages(db, (int)result, product.getImages());
        }
        return result != -1;
    }

    public boolean updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", product.getName());
        cv.put("categoryName", product.getCategory());
        cv.put("price", product.getPrice());
        cv.put("stock", product.getStock());
        cv.put("color", product.getColor());
        cv.put("sizes", product.getSizes());
        cv.put("description", product.getDescription());
        cv.put("imageResId", product.getImageResId());
        int result = db.update("products", cv, "id=?", new String[]{String.valueOf(product.getId())});
        if (result > 0) {
            insertProductImages(db, product.getId(), product.getImages());
        }
        return result > 0;
    }

    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Xóa sạch các ảnh liên kết để tránh mồ côi (Orphan data)
        db.delete("product_images", "productId=?", new String[]{String.valueOf(productId)});
        // Xóa sản phẩm khỏi giỏ hàng của tất cả mọi người
        db.delete("cart_items", "productId=?", new String[]{String.valueOf(productId)});
        // Xóa sản phẩm chính
        int result = db.delete("products", "id=?", new String[]{String.valueOf(productId)});
        return result > 0;
    }

    public boolean updateProductStock(int productId, int newStock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stock", newStock);
        int result = db.update("products", cv, "id=?", new String[]{String.valueOf(productId)});
        return result > 0;
    }

    public ArrayList<Product> getLowStockProducts(int threshold) {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM products WHERE stock <= ?", new String[]{String.valueOf(threshold)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Product p = extractProductFromCursor(cursor);
                    p.setImages(getProductImages(p.getId()));
                    list.add(p);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public int getTotalProductCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM products", null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // ==========================================
    // PHẦN 6: METHOD CHO CART
    // ==========================================


    // Hàm lấy danh sách các mặt hàng đang có trong Giỏ hàng của một Khách hàng cụ thể (dựa vào userId)
    public ArrayList<CartItem> getCartItems(int userId) {
        // Tạo một danh sách rỗng để chuẩn bị chứa kết quả
        ArrayList<CartItem> list = new ArrayList<>();
        
        // Nếu ID khách hàng không hợp lệ (nhỏ hơn hoặc bằng 0) thì trả về danh sách rỗng luôn
        if (userId <= 0) return list;

        // Mở kết nối đến cơ sở dữ liệu ở chế độ "Chỉ Đọc"
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Khởi tạo con trỏ (Cursor) để hứng dữ liệu
        Cursor cursor = null;
        try {
            // Chạy câu lệnh SQL: "Lấy ra ID sản phẩm, Số lượng, Màu sắc, Kích cỡ từ bảng cart_items của ông khách có ID = ?"
            cursor = db.rawQuery(
                    "SELECT productId, quantity, selectedColor, selectedSize FROM cart_items WHERE userId=?",
                    new String[]{String.valueOf(userId)}
            );
            
            // Nếu có kết quả trả về từ cơ sở dữ liệu
            if (cursor != null && cursor.moveToFirst()) {
                // Vòng lặp do-while: Quét từng dòng dữ liệu trong bảng kết quả
                do {
                    // Lấy ra các cột theo thứ tự 0, 1, 2, 3 tương ứng với câu lệnh SELECT ở trên
                    int productId = cursor.getInt(0); // Cột productId
                    int quantity = cursor.getInt(1); // Cột quantity
                    String color = cursor.getString(2); // Cột selectedColor
                    String size = cursor.getString(3); // Cột selectedSize
                    
                    // Từ ID của sản phẩm, gọi hàm getProductById để lấy ra TOÀN BỘ thông tin (tên, giá, hình ảnh...) của nó
                    Product product = getProductById(productId);
                    
                    // Đề phòng trường hợp admin đã xóa mất sản phẩm đó khỏi kho nhưng trong giỏ của khách vẫn còn lưu ID cũ
                    if (product != null) {
                        // Đóng gói sản phẩm + số lượng + màu + size thành 1 đối tượng CartItem và nhét vào danh sách
                        list.add(new CartItem(product, quantity, color, size));
                    }
                } while (cursor.moveToNext()); // Tiếp tục nhảy sang dòng dữ liệu tiếp theo nếu có
            }
        } finally {
            // Luôn luôn đóng Cursor lại sau khi dùng xong để chống rò rỉ bộ nhớ (Tràn RAM)
            if (cursor != null) cursor.close();
        }
        
        // Trả về danh sách cuối cùng để giao diện (Activity) hiển thị lên màn hình
        return list;
    }

    // =========================================================================
    // HÀM THÊM SẢN PHẨM VÀO GIỎ HÀNG (addToCart)
    // Mục đích: Kiểm tra tồn kho trước khi thêm, tự động cộng dồn số lượng 
    // nếu khách hàng đã có sẵn món đó trong giỏ hàng (cùng màu, cùng size).
    // =========================================================================
    public boolean addToCart(int userId, int productId, int quantity, String selectedColor, String selectedSize) {
        // Kiểm tra nhanh, nếu user chưa đăng nhập hoặc lỗi số lượng thì chặn luôn
        if (userId <= 0 || quantity <= 0) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            // Bước 1: Kiểm tra xem sản phẩm đó trong kho còn hàng không (Theo biến thể)
            int currentStock = getVariantStock(productId, selectedColor, selectedSize);
            if (currentStock < 0) return false;
            
            // Bước 2: Kiểm tra xem số lượng trong Giỏ + Số lượng thêm mới có vượt Tồn kho không
            int currentCartQty = 0;
            Cursor cartCursor = db.rawQuery("SELECT quantity FROM cart_items WHERE userId=? AND productId=? AND selectedColor=? AND selectedSize=?", new String[]{String.valueOf(userId), String.valueOf(productId), normalizeOption(selectedColor), normalizeOption(selectedSize)});
            if (cartCursor != null && cartCursor.moveToFirst()) {
                currentCartQty = cartCursor.getInt(0);
            }
            if (cartCursor != null) cartCursor.close();

            if (currentCartQty + quantity > currentStock) {
                return false; // Vượt quá số lượng trong kho -> Thất bại
            }

            // Chuẩn hóa định dạng chuỗi màu và size để tránh lỗi (dư khoảng trắng, null)
            String normalizedColor = normalizeOption(selectedColor);
            String normalizedSize = normalizeOption(selectedSize);
            
            // Bước 3: Truy vấn xem người dùng này đã có món hàng (cùng ID, cùng Màu, cùng Size) trong giỏ chưa
            cursor = db.rawQuery(
                    "SELECT quantity FROM cart_items WHERE userId=? AND productId=? AND selectedColor=? AND selectedSize=?",
                    new String[]{String.valueOf(userId), String.valueOf(productId), normalizedColor, normalizedSize}
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                // KỊCH BẢN A: ĐÃ CÓ MÓN NÀY TRONG GIỎ
                // -> Không tạo dòng mới, mà chỉ CỘNG DỒN số lượng (UPDATE)
                int existingQty = cursor.getInt(0);
                ContentValues cv = new ContentValues();
                cv.put("quantity", existingQty + quantity); // Cũ + Mới
                return db.update(
                        "cart_items",
                        cv,
                        "userId=? AND productId=? AND selectedColor=? AND selectedSize=?",
                        new String[]{String.valueOf(userId), String.valueOf(productId), normalizedColor, normalizedSize}
                ) > 0;
            }

            // KỊCH BẢN B: CHƯA CÓ TRONG GIỎ
            // -> Tạo một dòng mới tinh trong bảng cart_items (INSERT)
            ContentValues cv = new ContentValues();
            cv.put("userId", userId);
            cv.put("productId", productId);
            cv.put("quantity", quantity);
            cv.put("selectedColor", normalizedColor);
            cv.put("selectedSize", normalizedSize);
            return db.insert("cart_items", null, cv) != -1;
        } finally {
            // Luôn luôn phải đóng kết nối DB để tránh rò rỉ bộ nhớ (Memory Leak)
            if (cursor != null) cursor.close();
        }
    }

    public boolean updateCartItemVariant(int userId, int productId, String oldColor, String oldSize, String newColor, String newSize) {
        if (userId <= 0) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        Cursor oldCursor = null;
        try {
            String normalizedOldColor = normalizeOption(oldColor);
            String normalizedOldSize = normalizeOption(oldSize);
            String normalizedNewColor = normalizeOption(newColor);
            String normalizedNewSize = normalizeOption(newSize);

            if (normalizedOldColor.equals(normalizedNewColor) && normalizedOldSize.equals(normalizedNewSize)) {
                return true;
            }

            cursor = db.rawQuery(
                    "SELECT quantity FROM cart_items WHERE userId=? AND productId=? AND selectedColor=? AND selectedSize=?",
                    new String[]{String.valueOf(userId), String.valueOf(productId), normalizedNewColor, normalizedNewSize}
            );
            if (cursor != null && cursor.moveToFirst()) {
                int existingQty = cursor.getInt(0);
                oldCursor = db.rawQuery(
                        "SELECT quantity FROM cart_items WHERE userId=? AND productId=? AND selectedColor=? AND selectedSize=?",
                        new String[]{String.valueOf(userId), String.valueOf(productId), normalizedOldColor, normalizedOldSize}
                );
                int oldQty = 0;
                if (oldCursor != null && oldCursor.moveToFirst()) {
                    oldQty = oldCursor.getInt(0);
                }

                ContentValues cv = new ContentValues();
                cv.put("quantity", existingQty + oldQty);
                db.update(
                        "cart_items",
                        cv,
                        "userId=? AND productId=? AND selectedColor=? AND selectedSize=?",
                        new String[]{String.valueOf(userId), String.valueOf(productId), normalizedNewColor, normalizedNewSize}
                );
                return removeCartItem(db, userId, productId, normalizedOldColor, normalizedOldSize);
            }

            ContentValues cv = new ContentValues();
            cv.put("selectedColor", normalizedNewColor);
            cv.put("selectedSize", normalizedNewSize);
            return db.update(
                    "cart_items",
                    cv,
                    "userId=? AND productId=? AND selectedColor=? AND selectedSize=?",
                    new String[]{String.valueOf(userId), String.valueOf(productId), normalizedOldColor, normalizedOldSize}
            ) > 0;
        } finally {
            if (oldCursor != null) oldCursor.close();
            if (cursor != null) cursor.close();
        }
    }

    public boolean updateCartQuantity(int userId, int productId, int quantity, String selectedColor, String selectedSize) {
        if (userId <= 0) return false;
        if (quantity <= 0) {
            return removeCartItem(userId, productId, selectedColor, selectedSize);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        int currentVariantStock = getVariantStock(productId, selectedColor, selectedSize);
        if (currentVariantStock < 0) return false;
        
        // Không gọi getCartQuantityForProduct(db, userId, productId, selectedColor, selectedSize) cộng thêm quantity 
        // vì quantity TRUYỀN VÀO LÀ SỐ LƯỢNG MỚI (không phải cộng dồn).
        // Phải đảm bảo số lượng mới không vượt quá currentVariantStock
        if (quantity > currentVariantStock) {
            return false;
        }

        ContentValues cv = new ContentValues();
        cv.put("quantity", quantity);
        return db.update(
                "cart_items",
                cv,
                "userId=? AND productId=? AND selectedColor=? AND selectedSize=?",
                new String[]{
                        String.valueOf(userId),
                        String.valueOf(productId),
                        normalizeOption(selectedColor),
                        normalizeOption(selectedSize)
                }
        ) > 0;
    }

    public boolean removeCartItem(int userId, int productId, String selectedColor, String selectedSize) {
        if (userId <= 0) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        return removeCartItem(db, userId, productId, selectedColor, selectedSize);
    }

    public void clearCart(int userId) {
        if (userId <= 0) return;
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart_items", "userId=?", new String[]{String.valueOf(userId)});
    }

    public int getCartTotal(int userId) {
        int total = 0;
        for (CartItem item : getCartItems(userId)) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }









    // ==========================================
    // PHẦN 8: METHOD CHO ORDER USER
    // ==========================================

    // =========================================================================
    // HÀM TẠO ĐƠN HÀNG (createOrder) - CỰC KỲ QUAN TRỌNG
    // Áp dụng cơ chế TRANSACTION: Nếu một trong các bước (Lưu đơn, Cập nhật kho, Dùng voucher) 
    // bị lỗi thì sẽ ROLLBACK (hủy bỏ tất cả) để bảo toàn dữ liệu.
    // =========================================================================
    public int createOrder(int userId, String receiverName, String phone, String address, String note, int finalTotal, java.util.List<CartItem> selectedItems, String freeshipVoucherCode, String discountVoucherCode) {
        if (userId <= 0 || selectedItems == null || selectedItems.isEmpty()) return -1;

        SQLiteDatabase db = this.getWritableDatabase();
        
        // Bắt đầu một Transaction:
        // Tất cả các lệnh INSERT, UPDATE, DELETE bên trong khối này sẽ được gom thành 1 gói duy nhất.
        db.beginTransaction();
        try {
            // Bước 1: Kiểm tra tồn kho theo từng biến thể (Màu/Size)
            if (!hasSufficientStock(db, selectedItems)) {
                return -1; // Kho không đủ hàng -> Trả về lỗi, Rollback toàn bộ
            }

            // Bước 2: Kiểm tra và trừ số lượt dùng của Voucher
            int subtotal = 0;
            for (CartItem item : selectedItems) {
                subtotal += item.getQuantity() * item.getProduct().getPrice(); // Tính tổng tiền gốc
            }
            
            // Gọi hàm claimVoucher để kiểm tra tính hợp lệ và trừ lượt dùng (usedCount + 1)
            if (!claimVoucher(db, freeshipVoucherCode, "freeship", subtotal)) {
                return -1; // Lỗi voucher Freeship (hết lượt hoặc sai mã) -> Rollback
            }
            if (!claimVoucher(db, discountVoucherCode, "discount", subtotal)) {
                return -1; // Lỗi voucher Giảm giá (hết lượt hoặc sai mã) -> Rollback
            }

            // Bước 3: Tạo bản ghi (dòng dữ liệu) cho Đơn Hàng mới vào bảng `orders`
            String createdAt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            ContentValues cv = new ContentValues();
            cv.put("code", ""); // Tạm để rỗng mã đơn (DH...), sẽ cập nhật sau khi lấy được ID tự tăng
            cv.put("userId", userId);
            cv.put("receiverName", receiverName);
            cv.put("phone", phone);
            cv.put("address", address);
            cv.put("note", note);
            cv.put("createdAt", createdAt);
            cv.put("total", finalTotal);
            cv.put("status", ORDER_STATUS_PENDING); // Đơn mới luôn ở trạng thái "Chờ xác nhận"
            cv.put("freeshipVoucherCode", normalizeOption(freeshipVoucherCode));
            cv.put("discountVoucherCode", normalizeOption(discountVoucherCode));

            // Thực thi INSERT vào bảng orders
            long orderId = db.insert("orders", null, cv);
            if (orderId == -1) return -1; // Lỗi tạo đơn -> Rollback

            // Bước 4: Tạo mã đơn hàng đẹp (VD: ID=5 -> Mã=DH005) và Cập nhật lại vào DB
            String code = "DH" + String.format(Locale.getDefault(), "%03d", orderId);
            ContentValues cvCode = new ContentValues();
            cvCode.put("code", code);
            db.update("orders", cvCode, "id=?", new String[]{String.valueOf(orderId)});

            // Bước 5: Thêm từng món hàng vào bảng `order_details` (Chi tiết đơn)
            for (CartItem item : selectedItems) {
                ContentValues cvDetail = new ContentValues();
                cvDetail.put("orderId", orderId);
                cvDetail.put("productId", item.getProduct().getId());
                cvDetail.put("productName", item.getProduct().getName()); // Lưu cứng tên đề phòng Admin xóa/sửa sau này
                cvDetail.put("quantity", item.getQuantity());
                cvDetail.put("unitPrice", item.getProduct().getPrice()); // Lưu cứng giá bán tại thời điểm mua
                cvDetail.put("subtotal", item.getQuantity() * item.getProduct().getPrice());
                cvDetail.put("selectedColor", normalizeOption(item.getSelectedColor()));
                cvDetail.put("selectedSize", normalizeOption(item.getSelectedSize()));
                
                // Thực thi INSERT vào bảng order_details
                db.insert("order_details", null, cvDetail);
            }

            // Bước 6: TRỪ SỐ LƯỢNG TỒN KHO THỰC TẾ TRONG BẢNG PRODUCTS VÀ PRODUCT_VARIANTS
            for (CartItem item : selectedItems) {
                // Trừ kho tổng (Luôn trừ kho tổng để backward compatible)
                if (!adjustProductStock(db, item.getProduct().getId(), -item.getQuantity())) {
                    return -1; // Lỗi kho lúc trừ -> Rollback toàn bộ các lệnh từ nãy giờ
                }
                
                // Kiểm tra và trừ kho biến thể (Nếu có tồn kho chi tiết)
                int variantStock = getVariantStock(item.getProduct().getId(), item.getSelectedColor(), item.getSelectedSize());
                if (variantStock >= item.getQuantity()) {
                    updateVariantStock(item.getProduct().getId(), item.getSelectedColor(), item.getSelectedSize(), variantStock - item.getQuantity());
                }
            }

            // Bước 7: XÓA CÁC MÓN HÀNG NÀY KHỎI GIỎ HÀNG (BẢNG CART_ITEMS) CỦA USER
            for (CartItem item : selectedItems) {
                removeCartItem(db, userId, item.getProduct().getId(), item.getSelectedColor(), item.getSelectedSize());
            }

            // Nếu luồng code chạy đến được đây, nghĩa là TẤT CẢ 7 BƯỚC đều không có lỗi!
            // Xác nhận Giao dịch thành công (Lưu thẳng vào ổ cứng)
            db.setTransactionSuccessful(); 
            return (int) orderId;
        } finally {
            // Đóng Transaction
            // ĐẶC BIỆT LƯU Ý: Nếu trước đó KHÔNG GỌI db.setTransactionSuccessful(),
            // Hàm endTransaction() này sẽ TỰ ĐỘNG HỦY BỎ (ROLLBACK) mọi thao tác INSERT, UPDATE, DELETE vừa làm!
            db.endTransaction();
        }
    }

    public ArrayList<Order> getOrdersByUser(int userId) {
        ArrayList<Order> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM orders WHERE userId=? ORDER BY id DESC", new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Order order = new Order(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getInt(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getInt(8),
                            cursor.getString(9)
                    );
                    order.setItems(getOrderDetails(order.getId()));
                    list.add(order);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public ArrayList<OrderItem> getOrderDetails(int orderId) {
        ArrayList<OrderItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM order_details WHERE orderId=?", new String[]{String.valueOf(orderId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(new OrderItem(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getString(3),
                            cursor.getInt(4),
                            cursor.getInt(5),
                            cursor.getInt(6),
                            cursor.getString(7),
                            cursor.getString(8)
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }


    public boolean updateOrderItem(int orderId, int itemId, int productId, int oldQuantity, int newQuantity, String newColor, String newSize, int unitPrice) {
        if (newQuantity <= 0) return false;

        Order order = getOrderById(orderId);
        if (order == null || !ORDER_STATUS_PENDING.equals(order.getStatus())) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            int preservedAdjustment = order.getTotal() - getOrderItemsSubtotal(db, orderId);
            int quantityDiff = newQuantity - oldQuantity;
            if (quantityDiff > 0 && !adjustProductStock(db, productId, -quantityDiff)) {
                return false;
            }
            if (quantityDiff < 0 && !adjustProductStock(db, productId, -quantityDiff)) {
                return false;
            }

            ContentValues cv = new ContentValues();
            cv.put("quantity", newQuantity);
            cv.put("selectedColor", normalizeOption(newColor));
            cv.put("selectedSize", normalizeOption(newSize));
            cv.put("subtotal", newQuantity * unitPrice);
            db.update("order_details", cv, "id=?", new String[]{String.valueOf(itemId)});

            updateOrderTotal(db, orderId, preservedAdjustment);

            db.setTransactionSuccessful();
            return true;
        } finally {
            if (db.inTransaction()) db.endTransaction();
        }
    }

    public Order getOrderById(int orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM orders WHERE id=?", new String[]{String.valueOf(orderId)});
            if (cursor != null && cursor.moveToFirst()) {
                Order order = new Order(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getInt(8),
                        cursor.getString(9)
                );
                order.setItems(getOrderDetails(orderId));
                return order;
            }
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public ArrayList<Order> getAllOrders() {
        ArrayList<Order> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM orders ORDER BY id DESC", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Order order = new Order(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getInt(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getInt(8),
                            cursor.getString(9)
                    );
                    order.setItems(getOrderDetails(order.getId()));
                    list.add(order);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public ArrayList<Order> searchOrders(String keyword) {
        ArrayList<Order> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String likeKeyword = "%" + keyword + "%";
            cursor = db.rawQuery("SELECT * FROM orders WHERE code LIKE ? OR receiverName LIKE ? OR phone LIKE ? OR status LIKE ? ORDER BY id DESC", 
                    new String[]{likeKeyword, likeKeyword, likeKeyword, likeKeyword});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Order order = new Order(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getInt(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getInt(8),
                            cursor.getString(9)
                    );
                    order.setItems(getOrderDetails(order.getId()));
                    list.add(order);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }


    public boolean updateOrderStatus(int orderId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        Order order = getOrderById(orderId);
        if (order == null) return false;
        
        // NGHIỆP VỤ: Kiểm tra luồng trạng thái hợp lệ. Không được phép Hủy khi đã Giao hoặc Hoàn thành.
        if (!isValidOrderStatusTransition(order.getStatus(), newStatus)) return false;

        // BẮT ĐẦU TRANSACTION: Đảm bảo mọi thay đổi về số lượng được đồng bộ, tránh rủi ro đứt mạng giữa chừng
        db.beginTransaction();
        try {
            // KỊCH BẢN ĐẶC BIỆT: HỦY ĐƠN HÀNG
            if (ORDER_STATUS_CANCELLED.equals(newStatus)) {
                
                // 1. Hoàn trả lại số lượng vào Tồn kho (Stock) của từng sản phẩm VÀ biến thể
                for (OrderItem item : order.getItems()) {
                    // Hoàn lại kho tổng
                    if (!adjustProductStock(db, item.getProductId(), item.getQuantity())) {
                        return false; // Nếu lỗi DB, Transaction sẽ lập tức tự động Rollback
                    }
                    
                    // Hoàn lại kho biến thể (nếu có cấu hình kho chi tiết)
                    int variantStock = getVariantStock(item.getProductId(), item.getSelectedColor(), item.getSelectedSize());
                    if (variantStock != -1) {
                        updateVariantStock(item.getProductId(), item.getSelectedColor(), item.getSelectedSize(), variantStock + item.getQuantity());
                    }
                }
                
                // 3. Hoàn trả lại số lượt sử dụng Voucher cho khách hàng
                releaseOrderVouchers(db, orderId);
            }

            // Cập nhật trạng thái chữ mới vào CSDL
            ContentValues cv = new ContentValues();
            cv.put("status", newStatus);
            int rows = db.update("orders", cv, "id=?", new String[]{String.valueOf(orderId)});
            
            // Chốt giao dịch (Xác nhận lưu mọi thay đổi xuống ổ cứng)
            db.setTransactionSuccessful();
            return rows > 0;
        } finally {
            // Nếu có bất kỳ lỗi nào xảy ra làm gián đoạn trước khi gọi setTransactionSuccessful()
            // Hàm endTransaction sẽ tự động Rollback toàn bộ (Thu hồi mọi thao tác cập nhật kho ở trên)
            if (db.inTransaction()) {
                db.endTransaction();
            }
        }
    }

    public int getUserOrderCount() {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM orders", null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return count;
    }

    // ==========================================
    // VOUCHERS
    // ==========================================

    public java.util.ArrayList<com.example.quanlycuahangthoitrang.model.Voucher> getAllVouchers() {
        java.util.ArrayList<com.example.quanlycuahangthoitrang.model.Voucher> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM vouchers WHERE usedCount < usageLimit", null);
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(new com.example.quanlycuahangthoitrang.model.Voucher(
                            c.getInt(0), c.getString(1), c.getString(2),
                            c.getInt(3), c.getInt(4), c.getInt(5), c.getInt(6)
                    ));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    public java.util.ArrayList<com.example.quanlycuahangthoitrang.model.Voucher> getAdminAllVouchers() {
        java.util.ArrayList<com.example.quanlycuahangthoitrang.model.Voucher> list = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM vouchers ORDER BY id DESC", null);
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(new com.example.quanlycuahangthoitrang.model.Voucher(
                            c.getInt(0), c.getString(1), c.getString(2),
                            c.getInt(3), c.getInt(4), c.getInt(5), c.getInt(6)
                    ));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    public boolean addVoucher(String code, String type, int value, int minOrder, int usageLimit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("code", code.toUpperCase());
        cv.put("type", type);
        cv.put("value", value);
        cv.put("minOrder", minOrder);
        cv.put("usageLimit", usageLimit);
        cv.put("usedCount", 0);
        return db.insert("vouchers", null, cv) != -1;
    }

    public boolean updateVoucher(int id, String code, String type, int value, int minOrder, int usageLimit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("code", code.toUpperCase());
        cv.put("type", type);
        cv.put("value", value);
        cv.put("minOrder", minOrder);
        cv.put("usageLimit", usageLimit);
        return db.update("vouchers", cv, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteVoucher(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("vouchers", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean useVoucher(String code) {
        return changeVoucherUsage(this.getWritableDatabase(), code, 1);
    }


    public int getUserOrderRevenue() {
        int revenue = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(total) FROM orders WHERE status=?", new String[]{ORDER_STATUS_COMPLETED});
            if (cursor != null && cursor.moveToFirst()) {
                revenue = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return revenue;
    }

    public int getUserSoldQuantity() {
        int quantity = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(d.quantity) FROM order_details d JOIN orders o ON d.orderId = o.id WHERE o.status=?", new String[]{ORDER_STATUS_COMPLETED});
            if (cursor != null && cursor.moveToFirst()) {
                quantity = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return quantity;
    }

    // --- REVIEWS ---
    public boolean addReview(int userId, int productId, int rating, String comment, String createdAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("productId", productId);
        values.put("rating", rating);
        values.put("comment", comment);
        values.put("createdAt", createdAt);
        long result = db.insert("reviews", null, values);
        return result != -1;
    }

    public List<com.example.quanlycuahangthoitrang.model.Review> getProductReviews(int productId) {
        List<com.example.quanlycuahangthoitrang.model.Review> reviewList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM reviews WHERE productId = ? ORDER BY id DESC", new String[]{String.valueOf(productId)});
        if (cursor.moveToFirst()) {
            do {
                reviewList.add(new com.example.quanlycuahangthoitrang.model.Review(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reviewList;
    }

    public boolean hasUserReviewedProduct(int userId, int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM reviews WHERE userId = ? AND productId = ?", new String[]{String.valueOf(userId), String.valueOf(productId)});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public com.example.quanlycuahangthoitrang.model.Review getReviewByUserAndProduct(int userId, int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, userId, productId, rating, comment, createdAt FROM reviews WHERE userId = ? AND productId = ?", new String[]{String.valueOf(userId), String.valueOf(productId)});
        com.example.quanlycuahangthoitrang.model.Review review = null;
        if (cursor.moveToFirst()) {
            review = new com.example.quanlycuahangthoitrang.model.Review(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getString(4),
                cursor.getString(5)
            );
        }
        cursor.close();
        return review;
    }

    public boolean updateReview(int reviewId, int rating, String comment, String createdAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("rating", rating);
        values.put("comment", comment);
        values.put("createdAt", createdAt);
        int rows = db.update("reviews", values, "id = ?", new String[]{String.valueOf(reviewId)});
        return rows > 0;
    }

    // Hàm tính tổng doanh thu theo từng ngày
    // Trả về Map với Key là Ngày (vd: 15/06/2026) và Value là Tổng tiền
    public java.util.Map<String, Integer> getDailyRevenue(long filterStartTime) {
        
        // Sử dụng LinkedHashMap để duy trì thứ tự ngày tháng (từ cũ đến mới)
        java.util.Map<String, Integer> revenueMap = new java.util.LinkedHashMap<>();
        
        // Sử dụng TreeMap<Date, Integer> để ép các ngày phải sắp xếp theo đúng thứ tự thời gian (Chronological order)
        java.util.TreeMap<java.util.Date, Integer> chronologicalMap = new java.util.TreeMap<>();

        // Mở kết nối đọc dữ liệu
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Fetch all and filter in Java to avoid SQLite date format issues since it is stored as "dd/MM/yyyy"
        String query = "SELECT createdAt, total FROM orders WHERE status = 'Hoàn thành'";
                       
        // Thực thi truy vấn
        Cursor cursor = db.rawQuery(query, null);
        
        // Duyệt qua kết quả trả về
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        java.text.SimpleDateFormat daySdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());

        if (cursor.moveToFirst()) {
            do {
                String createdAt = cursor.getString(0);
                int total = cursor.getInt(1);
                
                if (createdAt != null) {
                    try {
                        java.util.Date dateObj = sdf.parse(createdAt);
                        if (dateObj != null) {
                            if (filterStartTime > 0 && dateObj.getTime() < filterStartTime) {
                                continue; // Bỏ qua đơn hàng cũ hơn mốc thời gian lọc
                            }
                            
                            // Đưa Date về đúng 0h00m của ngày đó để gộp chung doanh thu trong cùng 1 ngày
                            java.util.Date dayOnly = daySdf.parse(daySdf.format(dateObj));
                            chronologicalMap.put(dayOnly, chronologicalMap.getOrDefault(dayOnly, 0) + total);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (cursor.moveToNext());
        }
        
        // Đóng cursor để giải phóng bộ nhớ
        cursor.close();
        
        // Chuyển dữ liệu đã được sắp xếp chuẩn xác từ TreeMap sang LinkedHashMap (Yêu cầu của Chart)
        for (java.util.Map.Entry<java.util.Date, Integer> entry : chronologicalMap.entrySet()) {
            revenueMap.put(daySdf.format(entry.getKey()), entry.getValue());
        }
        
        return revenueMap;
    }
}
// Syncing IDE
