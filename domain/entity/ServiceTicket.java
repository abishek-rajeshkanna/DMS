package com.hyundai.DMS.domain.entity;

import com.hyundai.DMS.domain.enums.ServicePriority;
import com.hyundai.DMS.domain.enums.ServiceTicketStatus;
import com.hyundai.DMS.domain.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "service_tickets")
@EntityListeners(AuditingEntityListener.class)
public class ServiceTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealership_id", nullable = false)
    private Dealership dealership;

    @Column(name = "ticket_number")
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to", nullable = true)
    private User assignedTo;

    @Column(name = "vehicle_make")
    private String vehicleMake;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Column(name = "vehicle_year")
    private Integer vehicleYear;

    @Column(name = "vehicle_vin")
    private String vehicleVin;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "odometer_in")
    private Integer odometerIn;

    @Column(name = "odometer_out")
    private Integer odometerOut;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ServiceTicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private ServicePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;

    private String diagnosis;

    @Column(name = "customer_approval")
    private Boolean customerApproval;

    @Column(name = "drop_off_at")
    private LocalDateTime dropOffAt;

    @Column(name = "promised_at")
    private LocalDateTime promisedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "total_parts", precision = 12, scale = 2)
    private BigDecimal totalParts;

    @Column(name = "total_labor", precision = 12, scale = 2)
    private BigDecimal totalLabor;

    @Column(name = "total_cost", precision = 12, scale = 2)
    private BigDecimal totalCost;

    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
