package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CustomerStatus {
    LEAD("lead"),
    PROSPECT("prospect"),
    CUSTOMER("customer"),
    LOST("lost")
    ;

    private final String value;

    CustomerStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
