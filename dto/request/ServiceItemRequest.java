package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.ServiceItemType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceItemRequest {

    @NotNull(message = "Item type is required")
    private ServiceItemType itemType;

    @NotBlank(message = "Description is required")
    private String description;

    private String partNumber;

    @NotNull(message = "Unit cost is required")
    @DecimalMin(value = "0.01", message = "Unit cost must be > 0")
    private BigDecimal unitCost;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be > 0")
    private BigDecimal quantity;
}
