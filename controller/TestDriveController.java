package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.request.TestDriveFilter;
import com.hyundai.DMS.dto.request.TestDriveRequest;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.dto.response.TestDriveResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.TestDriveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test-drives")
@RequiredArgsConstructor
public class TestDriveController {

    private final TestDriveService testDriveService;

    @GetMapping
    public ResponseEntity<PagedResponse<TestDriveResponse>> list(
            TestDriveFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "scheduledAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal DmsUserDetails principal) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(testDriveService.listTestDrives(filter, PageRequest.of(page, size, sort), principal));
    }

    @PostMapping
    public ResponseEntity<TestDriveResponse> create(@Valid @RequestBody TestDriveRequest request,
                                                     @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testDriveService.scheduleTestDrive(request, principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestDriveResponse> getById(@PathVariable Long id,
                                                      @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(testDriveService.getTestDrive(id, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestDriveResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody TestDriveRequest request,
                                                     @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(testDriveService.updateTestDrive(id, request, principal));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TestDriveResponse> cancel(@PathVariable Long id,
                                                     @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(testDriveService.cancelTestDrive(id, principal));
    }
}
