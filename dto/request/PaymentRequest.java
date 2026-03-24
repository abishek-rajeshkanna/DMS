package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.PaymentMethod;
import com.hyundai.DMS.domain.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be > 0")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    @NotNull(message = "Payment status is required")
    private PaymentStatus status;

    private String referenceNo;

    private String notes;

    private LocalDateTime paidAt;
}
