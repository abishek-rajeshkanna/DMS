package com.hyundai.DMS.repository;

import com.hyundai.DMS.domain.entity.Dealership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DealershipRepository extends JpaRepository<Dealership, Long>, JpaSpecificationExecutor<Dealership> {
    boolean existsByEmail(String email);
    boolean existsByLicenseNo(String licenseNo);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByLicenseNoAndIdNot(String licenseNo, Long id);
}
