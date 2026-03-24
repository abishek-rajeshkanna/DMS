package com.hyundai.DMS.dto.request;

import com.hyundai.DMS.domain.enums.ServicePriority;
import com.hyundai.DMS.domain.enums.ServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceTicketRequest {

    @NotNull(message = "Dealership ID is required")
    private Long dealershipId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private Long assignedToUserId;

    @NotBlank(message = "Vehicle make is required")
    private String vehicleMake;

    @NotBlank(message = "Vehicle model is required")
    private String vehicleModel;

    private Integer vehicleYear;
    private String vehicleVin;
    private String licensePlate;
    private Integer odometerIn;

    @NotNull(message = "Priority is required")
    private ServicePriority priority;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    private String diagnosis;
    private Boolean customerApproval;
    private LocalDateTime dropOffAt;
    private LocalDateTime promisedAt;
    private String notes;
}
