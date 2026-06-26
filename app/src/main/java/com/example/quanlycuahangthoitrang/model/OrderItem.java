package com.example.quanlycuahangthoitrang.model;

public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private String productName;
    private int quantity;
    private int unitPrice;
    private int subtotal;
    private String selectedColor;
    private String selectedSize;

    public OrderItem(int id, int orderId, int productId, String productName, int quantity, int unitPrice, int subtotal, String selectedColor, String selectedSize) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
        this.selectedColor = selectedColor;
        this.selectedSize = selectedSize;
    }

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public int getUnitPrice() { return unitPrice; }
    public int getSubtotal() { return subtotal; }
    public String getSelectedColor() { return selectedColor; }
    public String getSelectedSize() { return selectedSize; }
}
