package com.ezh.Inventory.sales.invoice.service;

import com.ezh.Inventory.sales.invoice.dto.InvoiceDto;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.exception.CommonException;

public interface InvoiceService {

   CommonResponse createInvoice(InvoiceDto invoiceDto) throws CommonException;
   InvoiceDto getInvoiceById(Long invoiceId) throws CommonException;
}
