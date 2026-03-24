package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.request.InventoryFilter;
import com.hyundai.DMS.dto.request.InventoryRequest;
import com.hyundai.DMS.dto.response.InventoryHistoryResponse;
import com.hyundai.DMS.dto.response.InventoryResponse;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.InventoryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<PagedResponse<InventoryResponse>> list(
            InventoryFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletResponse response) {

        response.setHeader("Cache-Control", "max-age=300, public");
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(inventoryService.listInventory(filter, PageRequest.of(page, size, sort)));
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody InventoryRequest request,
                                                     @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createInventory(request, principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getById(@PathVariable Long id,
                                                      @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(inventoryService.getInventory(id, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody InventoryRequest request,
                                                     @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, request, principal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @AuthenticationPrincipal DmsUserDetails principal) {
        inventoryService.deleteInventory(id, principal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<PagedResponse<InventoryHistoryResponse>> history(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal DmsUserDetails principal) {

        return ResponseEntity.ok(inventoryService.getInventoryHistory(id,
                PageRequest.of(page, size), principal));
    }
}
