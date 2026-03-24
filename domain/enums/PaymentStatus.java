package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatus {
    PENDING("pending"),
    COMPLETED("completed"),
    FAILED("failed"),
    REFUNDED("refunded")
    ;

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
