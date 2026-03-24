package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BodyType {
    SEDAN("sedan"),
    SUV("suv"),
    HATCHBACK("hatchback"),
    COUPE("coupe"),
    CONVERTIBLE("convertible"),
    VAN("van"),
    TRUCK("truck"),
    WAGON("wagon")
    ;

    private final String value;

    BodyType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
