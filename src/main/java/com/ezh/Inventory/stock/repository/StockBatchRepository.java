package com.ezh.Inventory.stock.repository;

import com.ezh.Inventory.stock.entity.StockBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockBatchRepository extends JpaRepository<StockBatch, Long> {

    // Find specific batch to sell from
    Optional<StockBatch> findByItemIdAndBatchNumberAndWarehouseId(Long itemId, String batchNumber, Long warehouseId);

    // Find all available batches for an item (Useful for FIFO)
    @Query("SELECT b FROM StockBatch b WHERE b.itemId = :itemId AND b.warehouseId = :warehouseId AND b.remainingQty > 0 ORDER BY b.createdAt ASC")
    List<StockBatch> findAvailableBatches(Long itemId, Long warehouseId);
}
