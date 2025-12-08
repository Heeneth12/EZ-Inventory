package com.ezh.Inventory.sales.invoice.controller;


import com.ezh.Inventory.sales.invoice.dto.InvoiceDto;
import com.ezh.Inventory.sales.invoice.service.InvoiceService;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.ResponseResource;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> createInvoice(@RequestBody InvoiceDto invoiceDto) throws CommonException {
        log.info("Entering createInvoice with : {}", invoiceDto);
        CommonResponse response = invoiceService.createInvoice(invoiceDto);
        return ResponseResource.success(HttpStatus.CREATED, response, "Item created successfully");
    }

    @GetMapping(value = "/{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<InvoiceDto> getInvoice(@PathVariable Long invoiceId) throws CommonException {
        log.info("getInvoice → {}", invoiceId);
        InvoiceDto response = invoiceService.getInvoiceById(invoiceId);
        return ResponseResource.success(HttpStatus.OK, response, "Invoice fetched successfully");
    }
}
