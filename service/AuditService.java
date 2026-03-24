package com.hyundai.DMS.service;

import com.hyundai.DMS.domain.entity.AuditLog;
import com.hyundai.DMS.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async("auditExecutor")
    public void log(Long userId, Long dealershipId, String action, String tableName,
                    Long recordId, String oldValues, String newValues,
                    String ipAddress, String userAgent) {
        try {
            AuditLog entry = AuditLog.builder()
                    .userId(userId)
                    .dealershipId(dealershipId)
                    .action(action)
                    .tableName(tableName)
                    .recordId(recordId)
                    .oldValues(oldValues)
                    .newValues(newValues)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();
            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Failed to write audit log: action={}, table={}, recordId={}", action, tableName, recordId, e);
        }
    }
}
