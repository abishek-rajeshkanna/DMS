package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderFilter {
    private Long dealershipId;
    private OrderStatus status;
    private Long customerId;
    private Long salespersonId;
    private LocalDate orderDateFrom;
    private LocalDate orderDateTo;
}
