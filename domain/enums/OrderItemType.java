package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderItemType {
    VEHICLE("vehicle"),
    ACCESSORY("accessory"),
    INSURANCE("insurance"),
    WARRANTY("warranty"),
    FEE("fee"),
    OTHER("other")
    ;

    private final String value;

    OrderItemType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
