package com.example.quanlycuahangthoitrang.model;

public class Voucher {
    private int id;
    private String code;
    private String type; // "discount" or "freeship"
    private int value;
    private int minOrder;
    private int usageLimit;
    private int usedCount;

    public Voucher(int id, String code, String type, int value, int minOrder, int usageLimit, int usedCount) {
        this.id = id;
        this.code = code;
        this.type = type;
        this.value = value;
        this.minOrder = minOrder;
        this.usageLimit = usageLimit;
        this.usedCount = usedCount;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getType() { return type; }
    public int getValue() { return value; }
    public int getMinOrder() { return minOrder; }
    public int getUsageLimit() { return usageLimit; }
    public int getUsedCount() { return usedCount; }

    public String getDisplayText() {
        if (type.equals("freeship")) {
            return "Miễn phí vận chuyển (Tối đa " + value/1000 + "k) - Đơn từ " + minOrder/1000 + "k";
        } else {
            return "Giảm " + value/1000 + "k - Đơn tối thiểu " + minOrder/1000 + "k";
        }
    }
}
