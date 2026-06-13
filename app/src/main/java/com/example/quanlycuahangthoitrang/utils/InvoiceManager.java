package com.example.quanlycuahangthoitrang.utils;

import com.example.quanlycuahangthoitrang.model.Invoice;

import java.util.ArrayList;
import java.util.List;

public class InvoiceManager {
    private static List<Invoice> invoices = new ArrayList<>();
    private static int nextId = 1;

    static {
        // Mock data HD001
        List<com.example.quanlycuahangthoitrang.model.InvoiceItem> mockItems = new ArrayList<>();
        mockItems.add(new com.example.quanlycuahangthoitrang.model.InvoiceItem(
            new com.example.quanlycuahangthoitrang.model.Product(1, "Áo len cổ tròn basic nam nữ unisex", "Áo", 299000, "Xám", "Mô tả", com.example.quanlycuahangthoitrang.R.drawable.ic_product_tshirt, 12),
            1, 299000
        ));
        mockItems.add(new com.example.quanlycuahangthoitrang.model.InvoiceItem(
            new com.example.quanlycuahangthoitrang.model.Product(2, "Áo sơ mi nam", "Áo", 350000, "Trắng", "Mô tả", com.example.quanlycuahangthoitrang.R.drawable.ic_product_shirt, 8),
            2, 350000
        ));
        Invoice mockInvoice = new Invoice(
            nextId++,
            "HD001",
            "12/06/2026 15:30",
            mockItems,
            299000 + 700000,
            "Hoàn thành"
        );
        invoices.add(mockInvoice);
    }

    public static List<Invoice> getInvoices() {
        return invoices;
    }

    public static void addInvoice(Invoice invoice) {
        invoices.add(0, invoice); // Add to top
    }

    public static Invoice getInvoiceById(int id) {
        for (Invoice invoice : invoices) {
            if (invoice.getId() == id) {
                return invoice;
            }
        }
        return null;
    }

    public static int getNextId() {
        return nextId++;
    }
}
