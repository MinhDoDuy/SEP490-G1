package com.ffood.g1.enum_pay;

public enum PaymentMethod {
    VNPAY(1),
    CASH(2);

    private  final int value ;

    PaymentMethod(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
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