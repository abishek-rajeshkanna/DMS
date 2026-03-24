package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;

    private String cancellationReason;
}
