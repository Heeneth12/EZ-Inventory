package com.ezh.Inventory.sales.order.service;

import com.ezh.Inventory.sales.order.dto.SalesOrderCreateDto;
import com.ezh.Inventory.sales.order.dto.SalesOrderDto;
import com.ezh.Inventory.sales.order.dto.SalesOrderFilter;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.exception.CommonException;
import org.springframework.data.domain.Page;

public interface SalesOrderService {


    CommonResponse createSalesOrder(SalesOrderCreateDto salesOrderCreateDto) throws CommonException;
    CommonResponse updateSalesOrder(Long id, SalesOrderCreateDto salesOrderCreateDto) throws CommonException;
    Page<SalesOrderDto> getAllSalesOrders(SalesOrderFilter filter, int page, int size) throws CommonException;
    SalesOrderDto getSalesOrder(Long id) throws CommonException;

}
