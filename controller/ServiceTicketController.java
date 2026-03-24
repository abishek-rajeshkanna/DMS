package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.request.ServiceTicketFilter;
import com.hyundai.DMS.dto.request.ServiceTicketRequest;
import com.hyundai.DMS.dto.request.ServiceTicketStatusRequest;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.dto.response.ServiceTicketResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.ServiceTicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/service-tickets")
@RequiredArgsConstructor
public class ServiceTicketController {

    private final ServiceTicketService serviceTicketService;

    @GetMapping
    public ResponseEntity<PagedResponse<ServiceTicketResponse>> list(
            ServiceTicketFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal DmsUserDetails principal) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(serviceTicketService.listTickets(filter, PageRequest.of(page, size, sort), principal));
    }

    @PostMapping
    public ResponseEntity<ServiceTicketResponse> create(@Valid @RequestBody ServiceTicketRequest request,
                                                         @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceTicketService.createTicket(request, principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceTicketResponse> getById(@PathVariable Long id,
                                                          @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(serviceTicketService.getTicket(id, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceTicketResponse> update(@PathVariable Long id,
                                                         @Valid @RequestBody ServiceTicketRequest request,
                                                         @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(serviceTicketService.updateTicket(id, request, principal));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ServiceTicketResponse> transitionStatus(@PathVariable Long id,
                                                                   @Valid @RequestBody ServiceTicketStatusRequest request,
                                                                   @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(serviceTicketService.transitionStatus(id, request.getStatus(),
                request.getCompletedAt(), request.getDeliveredAt(), request.getOdometerOut(), principal));
    }
}
