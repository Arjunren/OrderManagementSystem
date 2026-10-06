package com.arjunren.order.repository;

import com.arjunren.order.entity.Product;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsBySkuIgnoreCase(String sku);
    @Query("select p from Product p where p.active=true and (lower(p.name) like lower(concat('%',:q,'%')) or lower(p.sku) like lower(concat('%',:q,'%'))) ")
    Page<Product> searchActive(@Param("q") String query, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :ids order by p.id")
    List<Product> findAllByIdForUpdate(@Param("ids") Collection<Long> ids);
}
