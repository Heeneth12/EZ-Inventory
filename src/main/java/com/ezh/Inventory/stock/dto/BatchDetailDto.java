package com.ezh.Inventory.stock.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchDetailDto {
    private String batchNumber;
    private BigDecimal buyPrice;    // Cost Price
    private Integer remainingQty;
    private Long expiryDate;
}