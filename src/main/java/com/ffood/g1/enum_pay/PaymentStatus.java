package com.ffood.g1.enum_pay;


public enum PaymentStatus {
    PENDING_PAYMENT(1, "PENDING_PAYMENT"),
    PAYMENT_COMPLETE(2, "PAYMENT_COMPLETE");

    private final int value;
    private final String code;

    PaymentStatus(int value, String code) {
        this.value = value;
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public static PaymentStatus fromValue(int value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    public static PaymentStatus fromCode(String code) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getCode().equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
