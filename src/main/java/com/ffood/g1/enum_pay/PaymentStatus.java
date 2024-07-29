package com.ffood.g1.enum_pay;


public enum PaymentStatus {
    PENDING_PAYMENT(1, "PENDING_PAYMENT","Đang chờ thanh toán"),
    PAYMENT_COMPLETE(2, "PAYMENT_COMPLETE","Thanh toán hoàn tất");

    private final int value;
    private final String code;
    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }


    PaymentStatus(int value, String code, String displayName) {
        this.value = value;
        this.code = code;
        this.displayName = displayName;
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
