package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InventoryRequest {

    @NotNull(message = "Dealership ID is required")
    private Long dealershipId;

    @NotBlank(message = "VIN is required")
    @Size(min = 11, max = 17, message = "VIN must be 11-17 characters")
    private String vin;

    @NotBlank(message = "Make is required")
    private String make;

    @NotBlank(message = "Model is required")
    private String model;

    private String variant;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be >= 1900")
    @Max(value = 2100, message = "Year must be <= 2100")
    private Integer year;

    @NotNull(message = "Fuel type is required")
    private FuelType fuelType;

    @NotNull(message = "Transmission is required")
    private Transmission transmission;

    @NotNull(message = "Body type is required")
    private BodyType bodyType;

    private String color;

    @Min(value = 0, message = "Mileage cannot be negative")
    private Integer mileage;

    @NotNull(message = "Condition is required")
    private InventoryCondition conditionType;

    @NotNull(message = "Status is required")
    private InventoryStatus status;

    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.01", message = "Cost price must be > 0")
    private BigDecimal costPrice;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.01", message = "Selling price must be > 0")
    private BigDecimal sellingPrice;

    private LocalDate intakeDate;

    private String notes;
}
