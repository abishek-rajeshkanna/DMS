package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    SUPER_ADMIN("super_admin"),
    ADMIN("admin"),
    MANAGER("manager"),
    SALESPERSON("salesperson"),
    TECHNICIAN("technician"),
    RECEPTIONIST("receptionist")
    ;

    private final String value;

    Role(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
