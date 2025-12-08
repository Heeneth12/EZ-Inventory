package com.ezh.Inventory.sales.invoice.repository;

import com.ezh.Inventory.sales.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>{

     Boolean existsByInvoiceNumber(String invoiceNumber);
}
