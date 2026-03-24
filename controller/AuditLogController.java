package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.request.AuditLogFilter;
import com.hyundai.DMS.dto.response.AuditLogResponse;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<PagedResponse<AuditLogResponse>> queryLogs(
            AuditLogFilter filter,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(auditLogService.queryLogs(filter, pageable, principal));
    }
}
