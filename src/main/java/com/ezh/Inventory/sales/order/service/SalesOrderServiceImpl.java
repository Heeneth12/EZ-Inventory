package com.ezh.Inventory.sales.order.service;

import com.ezh.Inventory.contacts.entiry.Contact;
import com.ezh.Inventory.contacts.repository.ContactRepository;
import com.ezh.Inventory.items.entity.Item;
import com.ezh.Inventory.items.repository.ItemRepository;
import com.ezh.Inventory.sales.order.dto.SalesOrderDto;
import com.ezh.Inventory.sales.order.dto.SalesOrderFilter;
import com.ezh.Inventory.sales.order.dto.SalesOrderItemDto;
import com.ezh.Inventory.sales.order.entity.SalesOrder;
import com.ezh.Inventory.sales.order.entity.SalesOrderItem;
import com.ezh.Inventory.sales.order.entity.SalesOrderStatus;
import com.ezh.Inventory.sales.order.repository.SalesOrderRepository;
import com.ezh.Inventory.utils.UserContextUtil;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.exception.BadRequestException;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final ContactRepository contactRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public CommonResponse createSalesOrder(SalesOrderDto dto) throws CommonException {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();

        Contact contact = contactRepository.findByIdAndTenantId(dto.getCustomerId(), tenantId)
                .orElseThrow(() -> new CommonException("", HttpStatus.BAD_REQUEST));

        // 1. Initialize Header
        SalesOrder salesOrder = SalesOrder.builder()
                .tenantId(tenantId)
                .warehouseId(dto.getWarehouseId())
                .customer(contact)
                .orderNumber("SO-" + System.currentTimeMillis())
                .orderDate(LocalDate.now())
                .status(SalesOrderStatus.CREATED)
                .remarks(dto.getRemarks())
                .build();

        // 2. Process Items & Calculate Totals
        processItemsAndTotals(salesOrder, dto.getItems());

        salesOrderRepository.save(salesOrder);

        return CommonResponse.builder()
                .id(salesOrder.getId().toString())
                .message("Sales Order Created Successfully")
                .build();
    }

    @Override
    @Transactional
    public CommonResponse updateSalesOrder(Long id, SalesOrderDto dto) throws CommonException {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();

        SalesOrder salesOrder = salesOrderRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new CommonException("Sales Order not found", HttpStatus.BAD_REQUEST));

        Contact contact = contactRepository.findByIdAndTenantId(dto.getCustomerId(), tenantId)
                .orElseThrow(() -> new CommonException("", HttpStatus.BAD_REQUEST));

        // Validation: Only update if CREATED
        if (salesOrder.getStatus() != SalesOrderStatus.CREATED) {
            throw new BadRequestException("Cannot edit Sales Order after processing has started.");
        }

        // Update basic fields
        salesOrder.setWarehouseId(dto.getWarehouseId());
        salesOrder.setCustomer(contact);
        salesOrder.setRemarks(dto.getRemarks());
        if (dto.getOrderDate() != null) salesOrder.setOrderDate(dto.getOrderDate());

        // CLEAR OLD ITEMS (Hibernate will delete them from DB because of orphanRemoval=true)
        salesOrder.getItems().clear();
        // Re-add new items and Recalculate
        processItemsAndTotals(salesOrder, dto.getItems());

        salesOrderRepository.save(salesOrder);

        return CommonResponse.builder()
                .id(salesOrder.getId().toString())
                .message("Sales Order Updated")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SalesOrderDto getSalesOrderById(Long id) throws CommonException {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();

        SalesOrder so = salesOrderRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new CommonException("Order not found", HttpStatus.BAD_REQUEST));

        return mapToDto(so);
    }



    @Override
    @Transactional(readOnly = true)
    public Page<SalesOrderDto> getAllSalesOrders(SalesOrderFilter filter, int page, int size) {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<SalesOrder> result = salesOrderRepository.findByTenantId(tenantId, pageable);

        return result.map(this::mapToDto);
    }


    @Override
    @Transactional
    public CommonResponse cancelSalesOrder(Long id) throws CommonException {
        Long tenantId = UserContextUtil.getTenantIdOrThrow();
        SalesOrder so = salesOrderRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new CommonException("Order not found", HttpStatus.BAD_REQUEST));

        if (so.getStatus() == SalesOrderStatus.FULLY_INVOICED) {
            throw new BadRequestException("Cannot cancel a fully invoiced order");
        }

        // Check if any partial invoicing happened
        boolean hasInvoicedItems = so.getItems().stream().anyMatch(i -> i.getInvoicedQty() > 0);
        if (hasInvoicedItems) {
            throw new BadRequestException("Cannot cancel order with invoiced items. Please return items first.");
        }

        so.setStatus(SalesOrderStatus.CANCELLED);
        salesOrderRepository.save(so);

        return CommonResponse.builder().message("Order Cancelled").build();
    }



    private void processItemsAndTotals(SalesOrder salesOrder, List<SalesOrderItemDto> itemDtos) {
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        // If list is null (update scenario), initialize it
        if (salesOrder.getItems() == null) {
            salesOrder.setItems(new ArrayList<>());
        }

        for (SalesOrderItemDto itemDto : itemDtos) {

            Item item = itemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new CommonException("", HttpStatus.BAD_REQUEST));

            BigDecimal qty = BigDecimal.valueOf(itemDto.getOrderedQty());
            BigDecimal price = item.getMrp();
            BigDecimal discount = itemDto.getDiscount() != null ? itemDto.getDiscount() : BigDecimal.ZERO;
            BigDecimal tax = itemDto.getTax() != null ? itemDto.getTax() : BigDecimal.ZERO;

            // Math: (Price * Qty) - Discount + Tax
            BigDecimal rawTotal = price.multiply(qty);
            BigDecimal lineTotal = rawTotal.subtract(discount).add(tax);

            // Accumulate Header Totals
            subTotal = subTotal.add(rawTotal);
            totalDiscount = totalDiscount.add(discount);
            totalTax = totalTax.add(tax);


            // Create Entity
            SalesOrderItem soItem = SalesOrderItem.builder()
                    .salesOrder(salesOrder) // <--- Link Child to Parent
                    .item(item)
                    .orderedQty(itemDto.getOrderedQty())
                    .invoicedQty(0)
                    .unitPrice(price)
                    .discount(discount)
                    //.tax(0)
                    .lineTotal(lineTotal)
                    .build();

            // Add to Parent's list
            salesOrder.getItems().add(soItem);
        }

        // Set Calculated Totals
        salesOrder.setSubTotal(subTotal);
        salesOrder.setTotalDiscount(totalDiscount);
        salesOrder.setTotalTax(totalTax);
        salesOrder.setGrandTotal(subTotal.subtract(totalDiscount).add(totalTax));
    }


    private SalesOrderDto mapToDto(SalesOrder so) {
        List<SalesOrderItemDto> itemDtos = new ArrayList<>();

        if (so.getItems() != null) {
            for (SalesOrderItem item : so.getItems()) {
                itemDtos.add(SalesOrderItemDto.builder()
                        .itemId(item.getItem().getId())
                        .itemName(item.getItem().getName())
                        .orderedQty(item.getOrderedQty())
                        .invoicedQty(item.getInvoicedQty())
                        .unitPrice(item.getUnitPrice())
                        .discount(item.getDiscount())
                        .tax(BigDecimal.ZERO)  // adjust if you store tax in entity
                        .lineTotal(item.getLineTotal())
                        .build()
                );
            }
        }

        return SalesOrderDto.builder()
                .id(so.getId())
                .orderNumber(so.getOrderNumber())
                .orderDate(so.getOrderDate())
                .status(so.getStatus())
                .warehouseId(so.getWarehouseId())
                .customerId(so.getCustomer().getId())
                .customerName(so.getCustomer().getName())
                .remarks(so.getRemarks())
                .subTotal(so.getSubTotal())
                .totalDiscount(so.getTotalDiscount())
                .grandTotal(so.getGrandTotal())
                .items(itemDtos)
                .build();
    }

}