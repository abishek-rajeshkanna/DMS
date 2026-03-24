package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.TestDriveStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestDriveFilter {
    private Long dealershipId;
    private TestDriveStatus status;
    private Long customerId;
    private Long inventoryId;
    private Long staffId;
    private LocalDateTime scheduledFrom;
    private LocalDateTime scheduledTo;
}
