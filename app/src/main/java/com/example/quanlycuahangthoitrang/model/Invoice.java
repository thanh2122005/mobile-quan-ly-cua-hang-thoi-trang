package com.example.quanlycuahangthoitrang.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Invoice implements Serializable {
    private int id;
    private String code;
    private String date;
    private List<InvoiceItem> items;
    private int total;
    private String status;

    public Invoice(int id, String code, String date, List<InvoiceItem> items, int total, String status) {
        this.id = id;
        this.code = code;
        this.date = date;
        this.items = items;
        this.total = total;
        this.status = status;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getDate() { return date; }
    public List<InvoiceItem> getItems() { return items; }
    public int getTotal() { return total; }
    public String getStatus() { return status; }
}
