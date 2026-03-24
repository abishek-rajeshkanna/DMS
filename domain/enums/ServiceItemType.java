package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceItemType {
    PART("part"),
    LABOR("labor"),
    CONSUMABLE("consumable"),
    EXTERNAL("external")
    ;

    private final String value;

    ServiceItemType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
