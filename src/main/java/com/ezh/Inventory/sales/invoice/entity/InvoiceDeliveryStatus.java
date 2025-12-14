package com.ezh.Inventory.sales.invoice.entity;

public enum InvoiceDeliveryStatus {
    PENDING,        // Not yet delivered
    IN_PROGRESS,    // Moved to delivery
    DELIVERED,
    CANCELLED
}
