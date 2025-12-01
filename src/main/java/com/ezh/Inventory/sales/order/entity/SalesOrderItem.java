package com.ezh.Inventory.sales.order.entity;

import com.ezh.Inventory.items.entity.Item;
import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sales_order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderItem extends CommonSerializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_order_id", nullable = false)
    private SalesOrder salesOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;   // price before discount

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "line_total", nullable = false)
    private BigDecimal lineTotal;  // (unit_price * qty) − discountAmount
}
