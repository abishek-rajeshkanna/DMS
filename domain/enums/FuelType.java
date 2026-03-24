package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FuelType {
    PETROL("petrol"),
    DIESEL("diesel"),
    ELECTRIC("electric"),
    HYBRID("hybrid"),
    CNG("cng")
    ;

    private final String value;

    FuelType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
