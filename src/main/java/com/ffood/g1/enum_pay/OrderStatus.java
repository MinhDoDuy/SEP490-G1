package com.ffood.g1.enum_pay;

public enum OrderStatus {
    PENDING("Chờ xử lý"),
    PROGRESS("Đang giao"),
    COMPLETE("Hoàn thành"),
    REFUND("Hoàn Tiền"),
    REJECT("Từ chối");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
