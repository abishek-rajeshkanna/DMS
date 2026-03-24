package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryFilter {
    private Long dealershipId;
    private String make;
    private String model;
    private Integer year;
    private FuelType fuelType;
    private Transmission transmission;
    private BodyType bodyType;
    private InventoryCondition conditionType;
    private InventoryStatus status;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String search;
}
