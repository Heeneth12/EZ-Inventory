package com.ezh.Inventory.sales.order.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesOrderFilter {
    private Long id;
    private String searchQuery;
    private String status;
    private Long customerId;
    private Long warehouseId;
    private Date fromDate;
    private Date toDate;
}