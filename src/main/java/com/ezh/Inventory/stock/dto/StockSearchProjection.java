package com.ezh.Inventory.stock.dto;
import java.math.BigDecimal;

public interface StockSearchProjection {
    Long getItemId();
    String getItemName();
    String getItemCode();
    String getItemSku();
    String getBatchNumber();
    BigDecimal getBuyPrice();
    Integer getRemainingQty();
    Long getExpiryDate();
}