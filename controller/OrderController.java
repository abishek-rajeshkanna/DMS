package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.request.OrderFilter;
import com.hyundai.DMS.dto.request.OrderRequest;
import com.hyundai.DMS.dto.request.OrderStatusRequest;
import com.hyundai.DMS.dto.response.OrderResponse;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<PagedResponse<OrderResponse>> list(
            OrderFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal DmsUserDetails principal) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(orderService.listOrders(filter, PageRequest.of(page, size, sort), principal));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request,
                                                 @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request, principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id,
                                                  @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(orderService.getOrder(id, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody OrderRequest request,
                                                 @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(orderService.updateOrder(id, request, principal));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> transitionStatus(@PathVariable Long id,
                                                           @Valid @RequestBody OrderStatusRequest request,
                                                           @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(orderService.transitionStatus(id, request.getStatus(),
                request.getCancellationReason(), principal));
    }
}
