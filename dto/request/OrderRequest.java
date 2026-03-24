package com.hyundai.DMS.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrderRequest {

    @NotNull(message = "Dealership ID is required")
    private Long dealershipId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Salesperson ID is required")
    private Long salespersonId;

    private LocalDate orderDate;

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", message = "Tax rate cannot be negative")
    private BigDecimal taxRate;

    @DecimalMin(value = "0.0", message = "Trade-in value cannot be negative")
    private BigDecimal tradeInValue;

    private String notes;
}
