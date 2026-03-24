package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.request.ServiceItemRequest;
import com.hyundai.DMS.dto.response.ServiceItemResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.ServiceItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service-tickets/{ticketId}/items")
@RequiredArgsConstructor
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    @GetMapping
    public ResponseEntity<List<ServiceItemResponse>> listItems(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(serviceItemService.listItems(ticketId, principal));
    }

    @PostMapping
    public ResponseEntity<ServiceItemResponse> addItem(
            @PathVariable Long ticketId,
            @Valid @RequestBody ServiceItemRequest request,
            @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceItemService.addItem(ticketId, request, principal));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ServiceItemResponse> updateItem(
            @PathVariable Long ticketId,
            @PathVariable Long itemId,
            @Valid @RequestBody ServiceItemRequest request,
            @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(serviceItemService.updateItem(ticketId, itemId, request, principal));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long ticketId,
            @PathVariable Long itemId,
            @AuthenticationPrincipal DmsUserDetails principal) {
        serviceItemService.removeItem(ticketId, itemId, principal);
        return ResponseEntity.noContent().build();
    }
}
