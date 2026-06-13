package com.example.quanlycuahangthoitrang.model;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private String iconName;

    public Category(int id, String name, String iconName) {
        this.id = id;
        this.name = name;
        this.iconName = iconName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
}
