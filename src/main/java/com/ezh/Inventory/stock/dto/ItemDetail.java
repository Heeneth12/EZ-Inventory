package com.ezh.Inventory.stock.dto;

import com.ezh.Inventory.stock.entity.AdjustmentType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDetail {
    private Long itemId;
    private String itemName;
    private Integer systemQty;
    private Integer countedQty;
    private Integer differenceQty;
    private AdjustmentType reasonType;
}
