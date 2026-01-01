package com.ezh.Inventory.sales.delivery.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteSummaryDto {
    private long totalRoutes;          // All Manifests
    private long pendingDeliveries;    // Items in SCHEDULED/SHIPPED
    private long completedDeliveries;  // Items in DELIVERED
    private long cancelledDeliveries;  // Items in CANCELLED
    private String routeEfficiency;    // Percentage of completed vs total
}
