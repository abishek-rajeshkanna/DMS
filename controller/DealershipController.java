package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.request.DealershipFilter;
import com.hyundai.DMS.dto.request.DealershipRequest;
import com.hyundai.DMS.dto.response.DealershipResponse;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.DealershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dealerships")
@RequiredArgsConstructor
public class DealershipController {

    private final DealershipService dealershipService;

    @GetMapping
    public ResponseEntity<PagedResponse<DealershipResponse>> list(
            DealershipFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(dealershipService.listDealerships(filter, PageRequest.of(page, size, sort)));
    }

    @PostMapping
    public ResponseEntity<DealershipResponse> create(@Valid @RequestBody DealershipRequest request,
                                                      @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dealershipService.createDealership(request, principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DealershipResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(dealershipService.getDealership(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DealershipResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody DealershipRequest request,
                                                      @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(dealershipService.updateDealership(id, request, principal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @AuthenticationPrincipal DmsUserDetails principal) {
        dealershipService.deleteDealership(id, principal);
        return ResponseEntity.noContent().build();
    }
}
