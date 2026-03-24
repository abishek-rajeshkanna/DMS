package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.request.PaymentFilter;
import com.hyundai.DMS.dto.request.PaymentRequest;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.dto.response.PaymentResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<PagedResponse<PaymentResponse>> list(
            PaymentFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal DmsUserDetails principal) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(paymentService.listPayments(filter, PageRequest.of(page, size, sort), principal));
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest request,
                                                   @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.recordPayment(request, principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable Long id,
                                                    @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(paymentService.getPayment(id, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody PaymentRequest request,
                                                   @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(paymentService.updatePayment(id, request, principal));
    }
}
