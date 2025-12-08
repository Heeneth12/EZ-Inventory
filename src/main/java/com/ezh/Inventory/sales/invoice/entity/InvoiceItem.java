package com.ezh.Inventory.sales.invoice.entity;

import com.ezh.Inventory.items.entity.Item;
import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem extends CommonSerializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount; // optional per item

    @Column(name = "tax_amount")
    private BigDecimal taxAmount; // tax per item

    @Column(name = "line_total", nullable = false)
    private BigDecimal lineTotal; // qty × price − discount + tax
}