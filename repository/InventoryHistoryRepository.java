package com.hyundai.DMS.repository;

import com.hyundai.DMS.domain.entity.InventoryHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {
    Page<InventoryHistory> findAllByInventoryIdOrderByCreatedAtDesc(Long inventoryId, Pageable pageable);
}
