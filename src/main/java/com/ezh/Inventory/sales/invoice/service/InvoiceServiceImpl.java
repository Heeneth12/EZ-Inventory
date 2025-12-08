package com.ezh.Inventory.sales.invoice.service;

import com.ezh.Inventory.contacts.entiry.Contact;
import com.ezh.Inventory.contacts.repository.ContactRepository;
import com.ezh.Inventory.items.entity.Item;
import com.ezh.Inventory.items.repository.ItemRepository;
import com.ezh.Inventory.sales.invoice.dto.InvoiceDto;
import com.ezh.Inventory.sales.invoice.dto.InvoiceItemDto;
import com.ezh.Inventory.sales.invoice.entity.Invoice;
import com.ezh.Inventory.sales.invoice.entity.InvoiceItem;
import com.ezh.Inventory.sales.invoice.entity.InvoiceStatus;
import com.ezh.Inventory.sales.invoice.repository.InvoiceItemRepository;
import com.ezh.Inventory.sales.invoice.repository.InvoiceRepository;
import com.ezh.Inventory.sales.order.entity.SalesOrder;
import com.ezh.Inventory.sales.order.entity.SalesOrderStatus;
import com.ezh.Inventory.sales.order.repository.SalesOrderRepository;
import com.ezh.Inventory.sales.order.service.SalesOrderService;
import com.ezh.Inventory.stock.dto.StockUpdateDto;
import com.ezh.Inventory.stock.entity.MovementType;
import com.ezh.Inventory.stock.entity.ReferenceType;
import com.ezh.Inventory.stock.entity.Stock;
import com.ezh.Inventory.stock.repository.StockLedgerRepository;
import com.ezh.Inventory.stock.repository.StockRepository;
import com.ezh.Inventory.stock.service.StockService;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.Status;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final ItemRepository itemRepository;
    private final StockRepository stockRepository;
    private final ContactRepository contactRepository;
    private final StockLedgerRepository stockLedgerRepository;
    private final StockService stockService;
    private final SalesOrderService salesOrderService;

    @Override
    @Transactional
    public CommonResponse createInvoice(InvoiceDto invoiceDto) throws CommonException {

        // 1. Generate invoice number
        String invoiceNum = generateInvoiceNumber();

        // 2. Get salesOrder
        SalesOrder salesOrder = salesOrderRepository.findById(invoiceDto.getSalesOrderId())
                .orElseThrow(() -> new CommonException("Sales order not found", HttpStatus.BAD_REQUEST));

        // 3. Get contact
        Contact contact = contactRepository.findById(invoiceDto.getCustomerId())
                .orElseThrow(() -> new CommonException("Contact not found", HttpStatus.BAD_REQUEST));

        // 4. Stock validation
        for (InvoiceItemDto invoiceItemDto : invoiceDto.getItems()) {
            Stock stock = stockRepository.findByItemId(invoiceItemDto.getItemId())
                    .orElseThrow(() -> new CommonException("Item not found in Stock: " + invoiceItemDto.getItemId(), HttpStatus.BAD_REQUEST));

            if (stock.getClosingQty().compareTo(invoiceItemDto.getQuantity()) < 0) {
                throw new CommonException("Not enough stock for " + invoiceItemDto.getItemName(), HttpStatus.BAD_REQUEST);
            }
        }

        // 5. Build Invoice Items first
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        for (InvoiceItemDto itemDto : invoiceDto.getItems()) {
            Item item = itemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new CommonException("Item not found: " + itemDto.getItemId(), HttpStatus.BAD_REQUEST));

            InvoiceItem invoiceItem = InvoiceItem.builder()
                    .item(item)
                    .quantity(itemDto.getQuantity())
                    .unitPrice(item.getMrp())
                    .discountAmount(itemDto.getDiscountAmount())
                    .taxAmount(itemDto.getTaxAmount())
                    .lineTotal(item.getMrp()
                            .multiply(BigDecimal.valueOf(itemDto.getQuantity()))
                            .subtract(itemDto.getDiscountAmount())
                            .add(itemDto.getTaxAmount()))
                    .build();

            invoiceItems.add(invoiceItem);
        }

        // 5. Build Invoice entity
        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNum)
                .salesOrder(salesOrder)
                .customer(contact)
                .status(InvoiceStatus.PENDING)
                .subTotal(invoiceDto.getSubTotal())
                .discountAmount(invoiceDto.getDiscountAmount())
                .taxAmount(invoiceDto.getTaxAmount())
                .grandTotal(invoiceDto.getGrandTotal())
                .amountPaid(invoiceDto.getAmountPaid())
                .balance(invoiceDto.getBalance())
                .items(invoiceItems)
                .remarks(invoiceDto.getRemarks())
                .build();

        Invoice finalInvoice = invoice;
        invoiceItems.forEach(item -> item.setInvoice(finalInvoice));
        invoice = invoiceRepository.save(invoice);


        // 7. Update stock for each item
        for (InvoiceItem item : invoiceItems) {
            StockUpdateDto stockUpdateDto = new StockUpdateDto();
            stockUpdateDto.setItemId(item.getItem().getId());
            stockUpdateDto.setQuantity(item.getQuantity());
            stockUpdateDto.setTransactionType(MovementType.OUT);
            stockUpdateDto.setReferenceType(ReferenceType.SALE);
            stockUpdateDto.setReferenceId(invoice.getId());
            stockUpdateDto.setWarehouseId(1L); // Set appropriate warehouse ID
            stockUpdateDto.setRemarks("Invoice: " + invoice.getInvoiceNumber());

            // This will automatically reduce stock
            stockService.updateStock(stockUpdateDto);
        }

        // 8. Update sales order status
        salesOrder.setStatus(SalesOrderStatus.FULLY_INVOICED);
        salesOrderRepository.save(salesOrder);

        return CommonResponse.builder()
                .status(Status.SUCCESS)
                .message("Invoice created successfully")
                .id(invoice.getId().toString())
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(Long invoiceId) throws CommonException {
        log.info("");
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new CommonException("Invoice not found with ID :" + invoiceId, HttpStatus.BAD_REQUEST));

        return convertToDto(invoice);
    }

    private InvoiceDto convertToDto(Invoice invoice) {
        // Convert invoice items
        List<InvoiceItemDto> itemDtos = invoice.getItems().stream()
                .map(this::convertToItemDto)
                .toList();

        return InvoiceDto.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .salesOrderId(invoice.getSalesOrder().getId())
                .customerId(invoice.getCustomer().getId())
                .status(invoice.getStatus())
                .items(itemDtos)
                .subTotal(invoice.getSubTotal())
                .discountAmount(invoice.getDiscountAmount())
                .taxAmount(invoice.getTaxAmount())
                .grandTotal(invoice.getGrandTotal())
                .amountPaid(invoice.getAmountPaid())
                .balance(invoice.getBalance())
                .remarks(invoice.getRemarks())
                .build();
    }

    private InvoiceItemDto convertToItemDto(InvoiceItem invoiceItem) {
        return InvoiceItemDto.builder()
                .id(invoiceItem.getId())
                .invoice(invoiceItem.getInvoice())
                .itemId(invoiceItem.getItem().getId())
                .itemName(invoiceItem.getItem().getName())
                .quantity(invoiceItem.getQuantity())
                .unitPrice(invoiceItem.getUnitPrice())
                .discountAmount(invoiceItem.getDiscountAmount())
                .taxAmount(invoiceItem.getTaxAmount())
                .lineTotal(invoiceItem.getLineTotal())
                .build();
    }

    private String generateInvoiceNumber() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        int random = new Random().nextInt(9000) + 1000; // always 4-digit random number
        return "INV-" + year + "-" + random;
    }

}