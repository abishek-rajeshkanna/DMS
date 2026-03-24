package com.hyundai.DMS.service;

import com.hyundai.DMS.domain.entity.AuditLog;
import com.hyundai.DMS.dto.request.AuditLogFilter;
import com.hyundai.DMS.dto.response.AuditLogResponse;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.repository.AuditLogRepository;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.specification.AuditLogSpecification;
import com.hyundai.DMS.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public PagedResponse<AuditLogResponse> queryLogs(AuditLogFilter filter, Pageable pageable, DmsUserDetails principal) {
        // Non-super-admin admins can only see their own dealership's logs
        if (!principal.isSuperAdmin()) {
            filter.setDealershipId(principal.getDealershipId());
        }

        Specification<AuditLog> spec = Specification
                .where(AuditLogSpecification.hasUserId(filter.getUserId()))
                .and(AuditLogSpecification.hasDealershipId(filter.getDealershipId()))
                .and(AuditLogSpecification.hasAction(filter.getAction()))
                .and(AuditLogSpecification.hasTableName(filter.getTableName()))
                .and(AuditLogSpecification.hasRecordId(filter.getRecordId()))
                .and(AuditLogSpecification.createdAfter(filter.getCreatedFrom()))
                .and(AuditLogSpecification.createdBefore(filter.getCreatedTo()));

        return PaginationUtils.toPagedResponse(
                auditLogRepository.findAll(spec, pageable).map(AuditLogResponse::from)
        );
    }
}
