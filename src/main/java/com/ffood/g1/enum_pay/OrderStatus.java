package com.ffood.g1.enum_pay;


public enum OrderStatus {
    PENDING_PAYMENT(1, "PENDING_PAYMENT"),
    PAYMENT_COMPLETE(2, "PAYMENT_COMPLETE");

    private final int value;
    private final String code;

    OrderStatus(int value, String code) {
        this.value = value;
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public static OrderStatus fromValue(int value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    public static OrderStatus fromCode(String code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}