package com.ezh.Inventory.stock.dto;

public enum AdjustmentMode {
    ABSOLUTE, // "I counted X items total" (Stock Take)
    ADD,      // "I found X extra items"
    REMOVE    // "I lost/broke X items" (Damage)
}