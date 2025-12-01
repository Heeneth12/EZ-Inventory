package com.ezh.Inventory.sales.order.entity;

public enum SalesOrderStatus {
    PENDING,                 // Order created but no billing started
    PARTIALLY_BILLED,        // Some items billed
    FULLY_BILLED,            // All items billed / converted to bill
    CANCELLED                // Order cancelled
}

