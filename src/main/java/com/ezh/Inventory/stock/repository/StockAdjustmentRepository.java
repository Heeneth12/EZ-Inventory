package com.ezh.Inventory.stock.repository;

import com.ezh.Inventory.stock.entity.StockAdjustment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {

    Page<StockAdjustment> findAllByTenantId(Long tenantId, Pageable pageable);

    @Query(
            value = """
                    SELECT * FROM stock_adjustment sa
                    WHERE sa.tenant_id = :tenantId
                      AND (:id IS NULL OR sa.id = :id)
                      AND (CAST(:status AS text) IS NULL OR sa.adjustment_status = CAST(:status AS text))
                      AND (:warehouseId IS NULL OR sa.warehouse_id = :warehouseId)
                      AND (
                            (CAST(:fromDate AS date) IS NULL OR sa.adjustment_date >= CAST(:fromDate AS date))
                            AND (CAST(:toDate AS date) IS NULL OR sa.adjustment_date <= CAST(:toDate AS date))
                          )
                      AND (
                            CAST(:searchQuery AS text) IS NULL
                            OR LOWER(sa.adjustment_number) LIKE LOWER(CONCAT('%', CAST(:searchQuery AS text), '%'))
                            OR LOWER(sa.reference) LIKE LOWER(CONCAT('%', CAST(:searchQuery AS text), '%'))
                            OR LOWER(sa.remarks) LIKE LOWER(CONCAT('%', CAST(:searchQuery AS text), '%'))
                          )
                    """,
            nativeQuery = true
    )
    Page<StockAdjustment> findAllStockAdjustment(
            @Param("tenantId") Long tenantId,
            @Param("id") Long id,
            @Param("status") String status,
            @Param("warehouseId") Long warehouseId,
            @Param("searchQuery") String searchQuery,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            Pageable pageable
    );
}
