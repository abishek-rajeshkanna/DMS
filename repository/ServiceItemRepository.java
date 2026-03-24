package com.hyundai.DMS.repository;

import com.hyundai.DMS.domain.entity.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
    List<ServiceItem> findAllByTicketId(Long ticketId);
}
