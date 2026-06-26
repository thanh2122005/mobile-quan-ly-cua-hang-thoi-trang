package com.example.quanlycuahangthoitrang.model;

public class CartItem {
    private Product product;
    private int quantity;
    private String selectedColor;
    private String selectedSize;
    private boolean isSelected = true;

    public CartItem(Product product, int quantity, String selectedColor, String selectedSize) {
        this.product = product;
        this.quantity = quantity;
        this.selectedColor = selectedColor;
        this.selectedSize = selectedSize;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getSelectedColor() { return selectedColor; }
    public String getSelectedSize() { return selectedSize; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
