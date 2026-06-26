package com.example.quanlycuahangthoitrang.model;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private String category;
    private int price;
    private String color;
    private String sizes;
    private String description;
    private int imageResId; 
    private int stock;

    public Product(int id, String name, String category, int price, String color, String sizes, String description, int imageResId, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.color = color;
        this.sizes = sizes;
        this.description = description;
        this.imageResId = imageResId;
        this.stock = stock;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getSizes() { return sizes; }
    public void setSizes(String sizes) { this.sizes = sizes; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    private java.util.ArrayList<String> images = new java.util.ArrayList<>();
    public java.util.ArrayList<String> getImages() { return images; }
    public void setImages(java.util.ArrayList<String> images) { this.images = images; }
    public String getMainImage() { 
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return ""; 
    }
}
