package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.TestDriveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestDriveRequest {

    @NotNull(message = "Dealership ID is required")
    private Long dealershipId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Inventory ID is required")
    private Long inventoryId;

    @NotNull(message = "Staff ID is required")
    private Long staffId;

    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledAt;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private TestDriveStatus status;

    private Integer odometerBefore;
    private Integer odometerAfter;

    private String notes;
}
