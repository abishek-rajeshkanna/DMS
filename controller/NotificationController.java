package com.hyundai.DMS.controller;

import com.hyundai.DMS.dto.response.NotificationResponse;
import com.hyundai.DMS.dto.response.PagedResponse;
import com.hyundai.DMS.security.DmsUserDetails;
import com.hyundai.DMS.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<PagedResponse<NotificationResponse>> listNotifications(
            @RequestParam(required = false) Boolean isRead,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(
                notificationService.listNotifications(principal.getUserId(), isRead, pageable)
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal DmsUserDetails principal) {
        return ResponseEntity.ok(notificationService.markAsRead(id, principal.getUserId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long id,
            @AuthenticationPrincipal DmsUserDetails principal) {
        notificationService.deleteNotification(id, principal.getUserId());
        return ResponseEntity.noContent().build();
    }
}
