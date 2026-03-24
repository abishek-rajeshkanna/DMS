package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InventoryCondition {
    NEW("new"),
    USED("used"),
    CERTIFIED_PRE_OWNED("certified_pre_owned")
    ;

    private final String value;

    InventoryCondition(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
