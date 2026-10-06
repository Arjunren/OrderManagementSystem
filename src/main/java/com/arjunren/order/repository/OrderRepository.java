package com.arjunren.order.repository;

import com.arjunren.order.domain.OrderStatus;
import com.arjunren.order.entity.CustomerOrder;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<CustomerOrder,Long> {
    Page<CustomerOrder> findByCustomerId(Long customerId,Pageable pageable);
    long countByStatus(OrderStatus status);
    @Query("select coalesce(sum(o.totalAmount),0) from CustomerOrder o where o.status=:status") BigDecimal revenue(@Param("status")OrderStatus status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select distinct o from CustomerOrder o left join fetch o.items i left join fetch i.product join fetch o.customer where o.id=:id")
    Optional<CustomerOrder> findByIdForUpdate(@Param("id")Long id);
}
