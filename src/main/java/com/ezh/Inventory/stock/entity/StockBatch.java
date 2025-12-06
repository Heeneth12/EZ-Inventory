package com.ezh.Inventory.stock.entity;

import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "stock_batch")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockBatch extends CommonSerializable {

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "batch_number", nullable = false)
    private String batchNumber; // e.g., "GRN-101-BATCH" or "BATCH-JAN-001"

    @Column(name = "grn_id")
    private Long grnId; // Link to where we bought it

    @Column(name = "buy_price", precision = 18, scale = 2)
    private BigDecimal buyPrice; // <--- The Specific Cost (e.g., 1000)

    @Column(name = "initial_qty", nullable = false)
    private Integer initialQty; // 10

    @Column(name = "remaining_qty", nullable = false)
    private Integer remainingQty; // Starts at 10, goes down as you sell

    @Column(name = "expiry_date")
    private Long expiryDate;
}