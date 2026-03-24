package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.request.CustomerFilter;
import com.hyundai.DMS.dto.request.CustomerRequest;
import com.hyundai.DMS.dto.response.CustomerResponse;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<PagedResponse<CustomerResponse>> list(
            CustomerFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal DmsUserDetails principal) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(customerService.listCustomers(filter, PageRequest.of(page, size, sort), principal));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request,
                                                    @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request, principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id,
                                                     @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(customerService.getCustomer(id, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody CustomerRequest request,
                                                    @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request, principal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @AuthenticationPrincipal DmsUserDetails principal) {
        customerService.deleteCustomer(id, principal);
        return ResponseEntity.noContent().build();
    }
}
