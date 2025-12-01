package com.ezh.Inventory.sales.order.repository;


import com.ezh.Inventory.sales.order.entity.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {
}
