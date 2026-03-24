package com.hyundai.DMS.repository;

import com.hyundai.DMS.domain.entity.ServiceTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceTicketRepository extends JpaRepository<ServiceTicket, Long>, JpaSpecificationExecutor<ServiceTicket> {
}
