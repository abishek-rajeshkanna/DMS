package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.ServiceTicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceTicketStatusRequest {

    @NotNull(message = "Status is required")
    private ServiceTicketStatus status;

    private LocalDateTime completedAt;
    private LocalDateTime deliveredAt;
    private Integer odometerOut;
}
