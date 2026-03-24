package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.OrderItemType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequest {

    @NotNull(message = "Item type is required")
    private OrderItemType itemType;

    private Long inventoryId; // required if itemType == VEHICLE

    private String description;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be > 0")
    private BigDecimal unitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be >= 1")
    private Integer quantity;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private BigDecimal discount;
}
