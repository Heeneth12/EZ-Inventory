package com.ezh.Inventory.stock.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockDashboardDto {
    private BigDecimal totalStockValue;
    private Long totalItemsOutOfStock;
    private Integer totalInQty;
    private Integer totalOutQty;
    private Integer netMovementQty;
    private List<StockDto> fastMovingItems;
}
