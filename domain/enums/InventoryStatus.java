package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InventoryStatus {
    AVAILABLE("available"),
    RESERVED("reserved"),
    TEST_DRIVE("test_drive"),
    SOLD("sold"),
    INACTIVE("inactive")
    ;

    private final String value;

    InventoryStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
