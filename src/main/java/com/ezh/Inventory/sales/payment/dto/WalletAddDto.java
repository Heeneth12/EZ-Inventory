package com.ezh.Inventory.sales.payment.dto;

import com.ezh.Inventory.sales.payment.entity.PaymentMethod;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletAddDto {
    private Long customerId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String referenceNumber; // e.g., Cheque No or Transaction ID
    private String remarks;
}
