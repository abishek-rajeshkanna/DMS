package com.hyundai.DMS.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    CASH("cash"),
    CARD("card"),
    BANK_TRANSFER("bank_transfer"),
    FINANCE("finance"),
    CHEQUE("cheque")
    ;

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
