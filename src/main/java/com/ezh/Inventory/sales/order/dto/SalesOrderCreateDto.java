package com.ezh.Inventory.sales.order.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderCreateDto {

    private Long id;
    private String orderNumber;  // optional if backend auto generates
    private Long customerId;
    private LocalDate orderDate;
    private Double discountPercent; // order level optional
    private String remarks;
    private List<SalesOrderItemDto> items;
}
