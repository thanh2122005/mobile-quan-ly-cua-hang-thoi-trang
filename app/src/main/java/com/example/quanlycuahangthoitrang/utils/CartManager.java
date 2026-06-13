package com.example.quanlycuahangthoitrang.utils;

import com.example.quanlycuahangthoitrang.model.CartItem;
import com.example.quanlycuahangthoitrang.model.Product;
import java.util.ArrayList;

public class CartManager {
    private static ArrayList<CartItem> cartItems = new ArrayList<>();

    public static void addToCart(Product product, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cartItems.add(new CartItem(product, quantity));
    }

    public static void removeFromCart(int productId) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getProduct().getId() == productId) {
                cartItems.remove(i);
                break;
            }
        }
    }

    public static void increaseQuantity(int productId) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(item.getQuantity() + 1);
                break;
            }
        }
    }

    public static void decreaseQuantity(int productId) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    removeFromCart(productId);
                }
                break;
            }
        }
    }

    public static ArrayList<CartItem> getCartItems() {
        return cartItems;
    }

    public static int getTotalPrice() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    public static int getTotalQuantity() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    public static void clearCart() {
        cartItems.clear();
    }
}
