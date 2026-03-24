package com.hyundai.DMS.repository;

import com.hyundai.DMS.domain.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {
    boolean existsByVin(String vin);
    boolean existsByVinAndIdNot(String vin, Long id);
}
