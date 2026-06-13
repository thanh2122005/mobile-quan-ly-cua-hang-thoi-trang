package com.example.quanlycuahangthoitrang.model;

import java.util.List;

public class Order {
    private int id;
    private String code;
    private int userId;
    private String receiverName;
    private String phone;
    private String address;
    private String note;
    private String createdAt;
    private int total;
    private String status;
    private List<OrderItem> items;

    public Order(int id, String code, int userId, String receiverName, String phone, String address, String note, String createdAt, int total, String status) {
        this.id = id;
        this.code = code;
        this.userId = userId;
        this.receiverName = receiverName;
        this.phone = phone;
        this.address = address;
        this.note = note;
        this.createdAt = createdAt;
        this.total = total;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public int getUserId() { return userId; }
    public String getReceiverName() { return receiverName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getNote() { return note; }
    public String getCreatedAt() { return createdAt; }
    public int getTotal() { return total; }
    public String getStatus() { return status; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
