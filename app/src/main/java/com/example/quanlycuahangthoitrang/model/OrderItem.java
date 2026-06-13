package com.example.quanlycuahangthoitrang.model;

public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private String productName;
    private int quantity;
    private int unitPrice;
    private int subtotal;

    public OrderItem(int id, int orderId, int productId, String productName, int quantity, int unitPrice, int subtotal) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public int getUnitPrice() { return unitPrice; }
    public int getSubtotal() { return subtotal; }
}
