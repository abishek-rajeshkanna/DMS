package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    DRAFT("draft"),
    CONFIRMED("confirmed"),
    PROCESSING("processing"),
    DELIVERED("delivered"),
    CANCELLED("cancelled")
    ;

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
