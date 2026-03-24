package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.PaymentMethod;
import com.hyundai.DMS.domain.enums.PaymentStatus;
import lombok.Data;

@Data
public class PaymentFilter {
    private Long orderId;
    private PaymentStatus status;
    private PaymentMethod method;
}
