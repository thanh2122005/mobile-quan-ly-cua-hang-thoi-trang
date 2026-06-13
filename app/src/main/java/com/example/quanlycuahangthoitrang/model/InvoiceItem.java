package com.example.quanlycuahangthoitrang.model;

import java.io.Serializable;

public class InvoiceItem implements Serializable {
    private Product product;
    private int quantity;
    private int unitPrice;

    public InvoiceItem(Product product, int quantity, int unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getUnitPrice() { return unitPrice; }
}
