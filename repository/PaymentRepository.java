package com.hyundai.DMS.repository;

import com.hyundai.DMS.domain.entity.Payment;
import com.hyundai.DMS.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.order.id = :orderId AND p.status = :status")
    BigDecimal sumAmountByOrderIdAndStatus(@Param("orderId") Long orderId, @Param("status") PaymentStatus status);
}
