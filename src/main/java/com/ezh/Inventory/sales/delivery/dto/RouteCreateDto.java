package com.ezh.Inventory.sales.delivery.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteCreateDto {
    private String areaName; // Optional
    private Long employeeId;
    private String vehicleNumber;
    private List<Long> deliveryIds; // The batch of scheduled deliveries
}
