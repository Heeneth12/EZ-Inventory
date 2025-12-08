package com.ezh.Inventory.sales.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceDto {
    private Long salesOrderId;
    private Long customerId;
    private BigDecimal subTotal; // qty × price (sum of all line totals before tax)
    private BigDecimal discountAmount; // optional (invoice level)
    private BigDecimal taxAmount; // total tax
    private BigDecimal grandTotal; // subTotal - discount + tax
    private BigDecimal amountPaid; // how much customer paid
    private BigDecimal balance; // outstanding amount
    private String remarks;
    private List<InvoiceItemDto> items;
}