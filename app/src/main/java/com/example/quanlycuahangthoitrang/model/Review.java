package com.example.quanlycuahangthoitrang.model;

public class Review {
    private int id;
    private int userId;
    private int productId;
    private int rating;
    private String comment;
    private String createdAt;

    public Review(int id, int userId, int productId, int rating, String comment, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getProductId() { return productId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
}
