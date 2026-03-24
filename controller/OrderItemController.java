package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.request.OrderItemRequest;
import com.hyundai.DMS.dto.response.OrderItemResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> list(@PathVariable Long orderId,
                                                         @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(orderItemService.listItems(orderId, principal));
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> add(@PathVariable Long orderId,
                                                  @Valid @RequestBody OrderItemRequest request,
                                                  @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItemService.addItem(orderId, request, principal));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<OrderItemResponse> update(@PathVariable Long orderId,
                                                     @PathVariable Long itemId,
                                                     @Valid @RequestBody OrderItemRequest request,
                                                     @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(orderItemService.updateItem(orderId, itemId, request, principal));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> remove(@PathVariable Long orderId,
                                        @PathVariable Long itemId,
                                        @AuthenticationPrincipal DmsUserDetails principal) {
        orderItemService.removeItem(orderId, itemId, principal);
        return ResponseEntity.noContent().build();
    }
}
