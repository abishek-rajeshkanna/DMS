package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.ServicePriority;
import com.hyundai.DMS.domain.enums.ServiceTicketStatus;
import com.hyundai.DMS.domain.enums.ServiceType;
import lombok.Data;

@Data
public class ServiceTicketFilter {
    private Long dealershipId;
    private ServiceTicketStatus status;
    private ServicePriority priority;
    private ServiceType serviceType;
    private Long assignedToUserId;
    private Long customerId;
}
