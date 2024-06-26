package com.ffood.g1.enum_pay;

public enum OrderStatus {
    PENDING_PAYMENT(1, "Chưa hoàn thành thanh toán"),
    PAYMENT_COMPLETE(2, "Kết thúc thanh toán");

    private final int value;
    private final String description;

    OrderStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static OrderStatus fromValue(int value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
