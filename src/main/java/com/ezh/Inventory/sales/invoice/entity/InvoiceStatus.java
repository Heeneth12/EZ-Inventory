package com.ezh.Inventory.sales.invoice.entity;

public enum InvoiceStatus {
    PENDING,            // Invoice created but not delivered and not paid
    MOVED_TO_DELIVERY,  // Invoice moved to delivery
    DELIVERED,          // Delivery completed
    WAITING_PAYMENT,    // Waiting for payment after delivery
    PARTIALLY_PAID,     // Some payment received
    PAID,               // Fully paid
    CANCELLED           // Invoice cancelled
}