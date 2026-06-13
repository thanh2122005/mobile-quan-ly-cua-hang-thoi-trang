package com.example.quanlycuahangthoitrang.utils;

import com.example.quanlycuahangthoitrang.R;
import com.example.quanlycuahangthoitrang.model.Category;
import com.example.quanlycuahangthoitrang.model.Product;

import java.util.ArrayList;
import java.util.List;

public class AdminDataManager {

    private static ArrayList<Product> products = new ArrayList<>();
    private static ArrayList<Category> categories = new ArrayList<>();
    private static int nextProductId = 10;
    private static int nextCategoryId = 5;

    static {
        // Initialize mock products
        products.add(new Product(1, "Áo len cổ tròn basic nam nữ unisex", "Áo", 299000, "Xám", "Áo len chất liệu mềm mại, giữ ấm tốt, phù hợp mọi giới tính.", R.drawable.ic_product_tshirt, 12));
        products.add(new Product(2, "Áo sơ mi nam", "Áo", 350000, "Trắng", "Áo sơ mi nam thanh lịch, chống nhăn.", R.drawable.ic_product_shirt, 8));
        products.add(new Product(3, "Áo thun cotton trơn thoáng mát", "Áo", 120000, "Đen", "Thoáng mát, thấm hút mồ hôi tốt.", R.drawable.ic_product_tshirt, 20));
        products.add(new Product(4, "Quần Jeans Denim dáng suông rộng", "Quần", 450000, "Xanh", "Quần jeans form rộng thoải mái, phong cách.", R.drawable.ic_product_pants, 10));
        products.add(new Product(5, "Quần Jeans cạp cao", "Quần", 499000, "Đen", "Tôn dáng, vải denim cao cấp.", R.drawable.ic_product_pants, 6));
        products.add(new Product(6, "Giày Sneaker thể thao năng động", "Giày", 890000, "Trắng", "Giày thể thao nhẹ, êm chân.", R.drawable.ic_product_shoes, 5));
        products.add(new Product(7, "Giày sneaker phối màu", "Giày", 809000, "Đỏ Trắng", "Thiết kế nổi bật, phù hợp đi chơi.", R.drawable.ic_product_shoes, 3));
        products.add(new Product(8, "Túi đơn giản và sang trọng", "Phụ kiện", 1299000, "Nâu", "Túi xách tay chất liệu da PU.", R.drawable.ic_product_bag, 4));
        products.add(new Product(9, "Kính râm gọng tròn", "Phụ kiện", 879000, "Đen", "Kính râm chống tia UV.", R.drawable.ic_product_glasses, 2));

        // Initialize mock categories
        categories.add(new Category(1, "Áo", "👕"));
        categories.add(new Category(2, "Quần", "👖"));
        categories.add(new Category(3, "Giày", "👟"));
        categories.add(new Category(4, "Phụ kiện", "🕶️"));
    }

    // --- Product Methods ---
    public static ArrayList<Product> getAllProducts() {
        return products;
    }

    public static Product getProductById(int id) {
        for (Product p : products) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public static void addProduct(Product product) {
        product.setId(nextProductId++);
        products.add(product);
    }

    public static void updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                return;
            }
        }
    }

    public static void deleteProduct(int productId) {
        products.removeIf(p -> p.getId() == productId);
    }

    public static ArrayList<Product> searchProducts(String keyword) {
        ArrayList<Product> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) return products;
        
        for (Product p : products) {
            if (FormatUtils.matchesSearch(keyword, p.getName(), p.getCategory(), p.getColor())) {
                result.add(p);
            }
        }
        return result;
    }

    public static ArrayList<Product> getLowStockProducts(int threshold) {
        ArrayList<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (p.getStock() <= threshold) {
                result.add(p);
            }
        }
        return result;
    }

    // --- Category Methods ---
    public static ArrayList<Category> getAllCategories() {
        return categories;
    }

    public static void addCategory(Category category) {
        category.setId(nextCategoryId++);
        categories.add(category);
    }

    public static void updateCategory(Category category) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == category.getId()) {
                categories.set(i, category);
                return;
            }
        }
    }

    public static void deleteCategory(int categoryId) {
        categories.removeIf(c -> c.getId() == categoryId);
    }

    public static int getProductCountByCategory(String categoryName) {
        int count = 0;
        for (Product p : products) {
            if (p.getCategory().equals(categoryName)) {
                count++;
            }
        }
        return count;
    }
}
