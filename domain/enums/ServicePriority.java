package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ServicePriority {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    URGENT("urgent")
    ;

    private final String value;

    ServicePriority(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
