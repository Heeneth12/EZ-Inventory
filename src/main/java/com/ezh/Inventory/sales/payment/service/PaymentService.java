package com.ezh.Inventory.sales.payment.service;

import com.ezh.Inventory.contacts.entiry.Contact;
import com.ezh.Inventory.sales.payment.dto.InvoicePaymentHistoryDto;
import com.ezh.Inventory.sales.payment.dto.InvoicePaymentSummaryDto;
import com.ezh.Inventory.sales.payment.dto.PaymentCreateDto;
import com.ezh.Inventory.sales.payment.dto.PaymentDto;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.exception.CommonException;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    CommonResponse recordPayment(PaymentCreateDto dto) throws CommonException;

    List<InvoicePaymentHistoryDto> getPaymentsByInvoiceId(Long invoiceId) throws CommonException;

    InvoicePaymentSummaryDto getInvoicePaymentSummary(Long invoiceId) throws CommonException;

    Page<PaymentDto> getAllPayments(Integer page, Integer size) throws CommonException;

    CommonResponse createCreditNote(Contact customer, BigDecimal amount, String returnRefNumber) throws CommonException;
}
