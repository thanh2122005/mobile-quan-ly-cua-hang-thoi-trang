package com.example.quanlycuahangthoitrang.data;

import com.example.quanlycuahangthoitrang.model.Product;
import com.example.quanlycuahangthoitrang.utils.AdminDataManager;

import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static List<Product> getProducts() {
        return AdminDataManager.getAllProducts();
    }

    public static Product getProductById(int id) {
        return AdminDataManager.getProductById(id);
    }

    public static List<Product> getProductsByCategory(String category) {
        List<Product> result = new ArrayList<>();
        if (category == null || category.equals("Tất cả")) {
            return getProducts();
        }
        for (Product product : getProducts()) {
            if (product.getCategory().equals(category)) {
                result.add(product);
            }
        }
        return result;
    }
}
