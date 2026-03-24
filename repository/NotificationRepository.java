package com.hyundai.DMS.repository;

import com.hyundai.DMS.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByUserIdAndIsRead(Long userId, Boolean isRead, Pageable pageable);
    Page<Notification> findAllByUserId(Long userId, Pageable pageable);
    Optional<Notification> findByIdAndUserId(Long id, Long userId);
}
