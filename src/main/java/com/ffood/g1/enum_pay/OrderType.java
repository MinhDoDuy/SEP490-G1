package com.ffood.g1.enum_pay;


public enum OrderType {
    AT_COUNTER("Tại quầy"),
    ONLINE_ORDER("Trực tuyến");

    private final String displayName;

    OrderType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}