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
import com.example.quanlycuahangthoitrang.model.Invoice;
import com.example.quanlycuahangthoitrang.model.InvoiceItem;
import com.example.quanlycuahangthoitrang.model.Order;
import com.example.quanlycuahangthoitrang.model.OrderItem;
import com.example.quanlycuahangthoitrang.model.Product;
import com.example.quanlycuahangthoitrang.model.User;
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
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
                "description TEXT, " +
                "imageResId INTEGER)");

        // 4. Table cart_items
        db.execSQL("CREATE TABLE cart_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productId INTEGER, " +
                "quantity INTEGER)");

        // 5. Table invoices
        db.execSQL("CREATE TABLE invoices (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT, " +
                "createdAt TEXT, " +
                "createdBy TEXT, " +
                "total INTEGER, " +
                "status TEXT)");

        // 6. Table invoice_details
        db.execSQL("CREATE TABLE invoice_details (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "invoiceId INTEGER, " +
                "productId INTEGER, " +
                "productName TEXT, " +
                "quantity INTEGER, " +
                "unitPrice INTEGER, " +
                "subtotal INTEGER)");

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
                "status TEXT)");

        // 8. Table order_details
        db.execSQL("CREATE TABLE order_details (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "orderId INTEGER, " +
                "productId INTEGER, " +
                "productName TEXT, " +
                "quantity INTEGER, " +
                "unitPrice INTEGER, " +
                "subtotal INTEGER)");

        insertInitialData(db);
        Log.d("DatabaseHelper", "Khởi tạo database thành công!");
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Users
        db.execSQL("INSERT INTO users (name, email, password, phone, address, role) VALUES ('Admin', 'admin@gmail.com', '123456', '0123456789', 'Hà Nội', 'admin')");
        db.execSQL("INSERT INTO users (name, email, password, phone, address, role) VALUES ('Thành', 'user@gmail.com', '123456', '0987654321', 'Hải Dương', 'user')");

        // Categories
        db.execSQL("INSERT INTO categories (name, imageResId) VALUES ('Áo', 0)");
        db.execSQL("INSERT INTO categories (name, imageResId) VALUES ('Quần', 0)");
        db.execSQL("INSERT INTO categories (name, imageResId) VALUES ('Giày', 0)");
        db.execSQL("INSERT INTO categories (name, imageResId) VALUES ('Phụ kiện', 0)");

        // Products
        db.execSQL("INSERT INTO products (name, categoryId, categoryName, price, stock, color, description, imageResId) VALUES " +
                "('Áo len cổ tròn basic nam nữ unisex', 1, 'Áo', 299000, 12, 'Xám', 'Mô tả', " + R.drawable.ic_product_tshirt + ")");
        db.execSQL("INSERT INTO products (name, categoryId, categoryName, price, stock, color, description, imageResId) VALUES " +
                "('Áo sơ mi nam', 1, 'Áo', 350000, 8, 'Trắng', 'Mô tả', " + R.drawable.ic_product_shirt + ")");
        db.execSQL("INSERT INTO products (name, categoryId, categoryName, price, stock, color, description, imageResId) VALUES " +
                "('Áo thun cotton trơn thoáng mát', 1, 'Áo', 120000, 20, 'Đen', 'Mô tả', " + R.drawable.ic_product_tshirt + ")");
        db.execSQL("INSERT INTO products (name, categoryId, categoryName, price, stock, color, description, imageResId) VALUES " +
                "('Quần Jeans Denim dáng suông rộng', 2, 'Quần', 450000, 10, 'Xanh', 'Mô tả', " + R.drawable.ic_product_pants + ")");
        db.execSQL("INSERT INTO products (name, categoryId, categoryName, price, stock, color, description, imageResId) VALUES " +
                "('Quần Jeans cạp cao', 2, 'Quần', 499000, 6, 'Đen', 'Mô tả', " + R.drawable.ic_product_pants + ")");
        db.execSQL("INSERT INTO products (name, categoryId, categoryName, price, stock, color, description, imageResId) VALUES " +
                "('Giày Sneaker thể thao năng động', 3, 'Giày', 890000, 5, 'Trắng', 'Mô tả', " + R.drawable.ic_product_shoes + ")");
        db.execSQL("INSERT INTO products (name, categoryId, categoryName, price, stock, color, description, imageResId) VALUES " +
                "('Giày sneaker phối màu', 3, 'Giày', 809000, 3, 'Đỏ Trắng', 'Mô tả', " + R.drawable.ic_product_shoes + ")");
        db.execSQL("INSERT INTO products (name, categoryId, categoryName, price, stock, color, description, imageResId) VALUES " +
                "('Túi đơn giản và sang trọng', 4, 'Phụ kiện', 1299000, 4, 'Nâu', 'Mô tả', " + R.drawable.ic_product_bag + ")");
        db.execSQL("INSERT INTO products (name, categoryId, categoryName, price, stock, color, description, imageResId) VALUES " +
                "('Kính râm gọng tròn', 4, 'Phụ kiện', 879000, 2, 'Đen', 'Mô tả', " + R.drawable.ic_product_glasses + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS products");
        db.execSQL("DROP TABLE IF EXISTS cart_items");
        db.execSQL("DROP TABLE IF EXISTS invoices");
        db.execSQL("DROP TABLE IF EXISTS invoice_details");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS order_details");
        onCreate(db);
    }

    // ==========================================
    // PHẦN 3: METHOD CHO USER
    // ==========================================

    public boolean checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT id FROM users WHERE email=? AND password=?", new String[]{email, password});
            return cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT role FROM users WHERE email=?", new String[]{email});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return "";
        } finally {
            if (cursor != null) cursor.close();
        }
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
                    list.add(new Category(cursor.getInt(0), cursor.getString(1), ""));
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
                return new Category(cursor.getInt(0), cursor.getString(1), "");
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
        int result = db.update("categories", cv, "id=?", new String[]{String.valueOf(category.getId())});
        return result > 0;
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
    // ==========================================

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM products", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(new Product(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(3),
                            cursor.getInt(4),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getInt(8),
                            cursor.getInt(5)
                    ));
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
                return new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getInt(8),
                        cursor.getInt(5)
                );
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
                    list.add(new Product(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(3),
                            cursor.getInt(4),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getInt(8),
                            cursor.getInt(5)
                    ));
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
        cv.put("description", product.getDescription());
        cv.put("imageResId", product.getImageResId());
        long result = db.insert("products", null, cv);
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
        cv.put("description", product.getDescription());
        cv.put("imageResId", product.getImageResId());
        int result = db.update("products", cv, "id=?", new String[]{String.valueOf(product.getId())});
        return result > 0;
    }

    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
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
                    list.add(new Product(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(3),
                            cursor.getInt(4),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getInt(8),
                            cursor.getInt(5)
                    ));
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

    public ArrayList<CartItem> getCartItems() {
        ArrayList<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM cart_items", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int productId = cursor.getInt(1);
                    int quantity = cursor.getInt(2);
                    Product product = getProductById(productId);
                    if (product != null) {
                        list.add(new CartItem(product, quantity));
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public boolean addToCart(int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT quantity FROM cart_items WHERE productId=?", new String[]{String.valueOf(productId)});
            if (cursor != null && cursor.moveToFirst()) {
                int existingQty = cursor.getInt(0);
                int newQty = existingQty + quantity;
                if (newQty <= 0) {
                    return removeCartItem(productId);
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put("quantity", newQty);
                    return db.update("cart_items", cv, "productId=?", new String[]{String.valueOf(productId)}) > 0;
                }
            } else {
                if (quantity > 0) {
                    ContentValues cv = new ContentValues();
                    cv.put("productId", productId);
                    cv.put("quantity", quantity);
                    return db.insert("cart_items", null, cv) != -1;
                }
                return false;
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean updateCartQuantity(int productId, int quantity) {
        if (quantity <= 0) {
            return removeCartItem(productId);
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("quantity", quantity);
        return db.update("cart_items", cv, "productId=?", new String[]{String.valueOf(productId)}) > 0;
    }

    public boolean removeCartItem(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("cart_items", "productId=?", new String[]{String.valueOf(productId)}) > 0;
    }

    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart_items", null, null);
    }

    public int getCartTotal() {
        int total = 0;
        for (CartItem item : getCartItems()) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    // ==========================================
    // PHẦN 7: METHOD CHO INVOICE ADMIN
    // ==========================================

    public int createInvoice(ArrayList<InvoiceItem> items, String createdBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            int total = 0;
            for (InvoiceItem item : items) {
                total += item.getQuantity() * item.getUnitPrice();
            }

            String createdAt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            ContentValues cv = new ContentValues();
            cv.put("code", ""); // Tạm trống
            cv.put("createdAt", createdAt);
            cv.put("createdBy", createdBy);
            cv.put("total", total);
            cv.put("status", "Hoàn thành");

            long invoiceId = db.insert("invoices", null, cv);
            if (invoiceId == -1) return -1;

            String code = "HD" + String.format(Locale.getDefault(), "%03d", invoiceId);
            ContentValues cvCode = new ContentValues();
            cvCode.put("code", code);
            db.update("invoices", cvCode, "id=?", new String[]{String.valueOf(invoiceId)});

            for (InvoiceItem item : items) {
                ContentValues cvDetail = new ContentValues();
                cvDetail.put("invoiceId", invoiceId);
                cvDetail.put("productId", item.getProduct().getId());
                cvDetail.put("productName", item.getProduct().getName());
                cvDetail.put("quantity", item.getQuantity());
                cvDetail.put("unitPrice", item.getUnitPrice());
                cvDetail.put("subtotal", item.getQuantity() * item.getUnitPrice());
                db.insert("invoice_details", null, cvDetail);

                Product p = getProductById(item.getProduct().getId());
                if (p != null) {
                    updateProductStock(p.getId(), p.getStock() - item.getQuantity());
                }
            }

            db.setTransactionSuccessful();
            return (int) invoiceId;
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Invoice> getAllInvoices() {
        ArrayList<Invoice> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM invoices ORDER BY id DESC", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    Invoice inv = new Invoice(
                            id,
                            cursor.getString(1),
                            cursor.getString(2),
                            getInvoiceDetails(id),
                            cursor.getInt(4),
                            cursor.getString(5)
                    );
                    list.add(inv);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public Invoice getInvoiceById(int invoiceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM invoices WHERE id=?", new String[]{String.valueOf(invoiceId)});
            if (cursor != null && cursor.moveToFirst()) {
                return new Invoice(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        getInvoiceDetails(invoiceId),
                        cursor.getInt(4),
                        cursor.getString(5)
                );
            }
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public ArrayList<InvoiceItem> getInvoiceDetails(int invoiceId) {
        ArrayList<InvoiceItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM invoice_details WHERE invoiceId=?", new String[]{String.valueOf(invoiceId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int productId = cursor.getInt(2);
                    Product p = getProductById(productId);
                    if (p == null) {
                        p = new Product(productId, cursor.getString(3), "", cursor.getInt(5), "", "", 0, 0);
                    }
                    list.add(new InvoiceItem(p, cursor.getInt(4), cursor.getInt(5)));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public int getTotalRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(total) FROM invoices WHERE status='Hoàn thành'", null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public int getInvoiceCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM invoices", null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public int getSoldQuantity() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(quantity) FROM invoice_details", null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public ArrayList<Product> getBestSellingProducts() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT productId, SUM(quantity) as total_qty FROM invoice_details GROUP BY productId ORDER BY total_qty DESC LIMIT 3", null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Product p = getProductById(cursor.getInt(0));
                    if (p != null) list.add(p);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    // ==========================================
    // PHẦN 8: METHOD CHO ORDER USER
    // ==========================================

    public int createOrder(int userId, String receiverName, String phone, String address, String note) {
        ArrayList<CartItem> cartItems = getCartItems();
        if (cartItems.isEmpty()) return -1;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            int total = getCartTotal();
            String createdAt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            ContentValues cv = new ContentValues();
            cv.put("code", "");
            cv.put("userId", userId);
            cv.put("receiverName", receiverName);
            cv.put("phone", phone);
            cv.put("address", address);
            cv.put("note", note);
            cv.put("createdAt", createdAt);
            cv.put("total", total);
            cv.put("status", "Chờ xác nhận");

            long orderId = db.insert("orders", null, cv);
            if (orderId == -1) return -1;

            String code = "DH" + String.format(Locale.getDefault(), "%03d", orderId);
            ContentValues cvCode = new ContentValues();
            cvCode.put("code", code);
            db.update("orders", cvCode, "id=?", new String[]{String.valueOf(orderId)});

            for (CartItem item : cartItems) {
                ContentValues cvDetail = new ContentValues();
                cvDetail.put("orderId", orderId);
                cvDetail.put("productId", item.getProduct().getId());
                cvDetail.put("productName", item.getProduct().getName());
                cvDetail.put("quantity", item.getQuantity());
                cvDetail.put("unitPrice", item.getProduct().getPrice());
                cvDetail.put("subtotal", item.getQuantity() * item.getProduct().getPrice());
                db.insert("order_details", null, cvDetail);

                Product p = item.getProduct();
                updateProductStock(p.getId(), p.getStock() - item.getQuantity());
            }

            clearCart();
            db.setTransactionSuccessful();
            return (int) orderId;
        } finally {
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
                            cursor.getInt(6)
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
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
}
