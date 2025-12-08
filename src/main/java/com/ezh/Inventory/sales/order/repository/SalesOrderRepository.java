package com.ezh.Inventory.sales.order.repository;

import com.ezh.Inventory.sales.order.entity.SalesOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    Optional<SalesOrder> findByIdAndTenantId(Long id, Long tenantId);

    Page<SalesOrder> findByTenantId(Long tenantId, Pageable pageable);
}
