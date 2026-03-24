package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.CustomerStatus;
import lombok.Data;

@Data
public class CustomerFilter {
    private Long dealershipId;
    private CustomerStatus status;
    private String source;
    private Long assignedToUserId;
    private String search;
}
