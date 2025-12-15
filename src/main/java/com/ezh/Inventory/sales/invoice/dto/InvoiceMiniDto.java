package com.ezh.Inventory.sales.invoice.dto;

import com.ezh.Inventory.contacts.dto.ContactMiniDto;
import com.ezh.Inventory.sales.invoice.entity.InvoicePaymentStatus;
import com.ezh.Inventory.sales.invoice.entity.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceMiniDto {
    private Long id;
    private String invoiceNumber;
    private ContactMiniDto contactMini;
    private Long customerId;
    private InvoiceStatus status;
    private InvoicePaymentStatus paymentStatus;
    private Date invoiceDate;
    private BigDecimal subTotal; // qty × price (sum of all line totals before tax)
    private BigDecimal discountAmount; // optional (invoice level)
    private BigDecimal taxAmount; // total tax
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    private BigDecimal totalTax = BigDecimal.ZERO;
    private BigDecimal grandTotal; // subTotal - discount + tax
    private BigDecimal amountPaid; // how much customer paid
    private BigDecimal balance; // outstanding amount
    private String remarks;
}
