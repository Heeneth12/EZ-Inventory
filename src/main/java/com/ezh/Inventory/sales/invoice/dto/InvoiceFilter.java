package com.ezh.Inventory.sales.invoice.dto;

import com.ezh.Inventory.sales.invoice.entity.InvoiceStatus;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceFilter {
    private Long id;
    private String searchQuery;
    private Long salesOrderId;
    private InvoiceStatus status;
    private Long customerId;
    private Long warehouseId;
    private Date fromDate;
    private Date toDate;
}