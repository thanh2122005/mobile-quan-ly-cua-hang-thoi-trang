package com.example.quanlycuahangthoitrang.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String role;

    public User(int id, String name, String email, String password, String phone, String address, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
}
