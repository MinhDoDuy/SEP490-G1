package com.ffood.g1.enum_pay;

public enum PaymentMethod {
    VNPAY(1, "VNPay"),
    CASH(2, "Tiền mặt");

    private final int value;
    private final String displayName;

    PaymentMethod(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentMethod fromValue(int value) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.getValue() == value) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
