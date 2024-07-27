package com.ffood.g1.enum_pay;

public enum FeedbackStatus {
    PENDING("Chờ phê duyệt") ,
    COMPLETE("Phê Duyệt Thành Công"),
    REJECT("Từ Chối");


    private final String displayName;

    FeedbackStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    }
