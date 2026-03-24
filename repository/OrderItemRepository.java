package com.hyundai.DMS.repository;

import com.hyundai.DMS.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrderId(Long orderId);

    @Query("SELECT COALESCE(SUM(oi.lineTotal), 0) FROM OrderItem oi WHERE oi.order.id = :orderId")
    BigDecimal sumLineTotalByOrderId(@Param("orderId") Long orderId);
}
