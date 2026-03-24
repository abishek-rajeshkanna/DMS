package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceTicketStatus {
    OPEN("open"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    DELIVERED("delivered"),
    CANCELLED("cancelled")
    ;

    private final String value;

    ServiceTicketStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
