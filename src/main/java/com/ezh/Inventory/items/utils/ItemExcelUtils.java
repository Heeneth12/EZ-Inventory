package com.ezh.Inventory.items.utils;

import com.ezh.Inventory.items.dto.ItemDto;
import com.ezh.Inventory.items.entity.Item;
import com.ezh.Inventory.items.entity.ItemType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemExcelUtils {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String SHEET = "Items";
    static String[] HEADERS = {"ID", "Name", "Item Code", "SKU", "Barcode", "Item Type", "Category",
            "Unit", "Brand", "Manufacturer", "Purchase Price", "Selling Price",
            "MRP", "Tax %", "Discount %", "HSN", "Description"};

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static ByteArrayInputStream itemsToExcel(List<Item> items) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowIdx = 1;
            for (Item item : items) {
                Row row = sheet.createRow(rowIdx++);

                // Col 0: ID (Crucial for updates)
                row.createCell(0).setCellValue(item.getId());
                // Shifted Columns
                row.createCell(1).setCellValue(item.getName());
                row.createCell(2).setCellValue(item.getItemCode());
                row.createCell(3).setCellValue(item.getSku());
                row.createCell(4).setCellValue(item.getBarcode());
                row.createCell(5).setCellValue(item.getItemType() != null ? item.getItemType().name() : "");
                row.createCell(6).setCellValue(item.getCategory());
                row.createCell(7).setCellValue(item.getUnitOfMeasure());
                row.createCell(8).setCellValue(item.getBrand());
                row.createCell(9).setCellValue(item.getManufacturer());
                row.createCell(10).setCellValue(getDouble(item.getPurchasePrice()));
                row.createCell(11).setCellValue(getDouble(item.getSellingPrice()));
                row.createCell(12).setCellValue(getDouble(item.getMrp()));
                row.createCell(13).setCellValue(getDouble(item.getTaxPercentage()));
                row.createCell(14).setCellValue(getDouble(item.getDiscountPercentage()));
                row.createCell(15).setCellValue(item.getHsnSacCode());
                row.createCell(16).setCellValue(item.getDescription());
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public static List<ItemDto> excelToItems(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            if (sheet == null) sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.iterator();
            List<ItemDto> itemDtos = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                } // skip header

                if (isRowEmpty(currentRow)) continue;

                ItemDto itemDto = new ItemDto();

                // Col 0: ID
                // If ID is present, we set it. If blank, it remains null (treated as new item)
                Cell idCell = currentRow.getCell(0);
                if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                    itemDto.setId((long) idCell.getNumericCellValue());
                }
                itemDto.setName(getString(currentRow.getCell(1)));
                itemDto.setItemCode(getString(currentRow.getCell(2)));
                itemDto.setSku(getString(currentRow.getCell(3)));
                itemDto.setBarcode(getString(currentRow.getCell(4)));
                String typeStr = getString(currentRow.getCell(5));
                if (typeStr != null && !typeStr.isEmpty()) {
                    try {
                        itemDto.setItemType(ItemType.valueOf(typeStr.toUpperCase()));
                    } catch (Exception ignored) {
                    }
                }
                itemDto.setCategory(getString(currentRow.getCell(6)));
                itemDto.setUnitOfMeasure(getString(currentRow.getCell(7)));
                itemDto.setBrand(getString(currentRow.getCell(8)));
                itemDto.setManufacturer(getString(currentRow.getCell(9)));
                itemDto.setPurchasePrice(getBigDecimal(currentRow.getCell(10)));
                itemDto.setSellingPrice(getBigDecimal(currentRow.getCell(11)));
                itemDto.setMrp(getBigDecimal(currentRow.getCell(12)));
                itemDto.setTaxPercentage(getBigDecimal(currentRow.getCell(13)));
                itemDto.setDiscountPercentage(getBigDecimal(currentRow.getCell(14)));
                itemDto.setHsnSacCode(getString(currentRow.getCell(15)));
                itemDto.setDescription(getString(currentRow.getCell(16)));
                itemDto.setIsActive(true);
                itemDtos.add(itemDto);
            }
            workbook.close();
            return itemDtos;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }


    public static ByteArrayInputStream generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET);

            //Create Header Row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
                cell.setCellStyle(headerStyle);
            }

            //Add a Sample Row (Optional - Helps users understand format)
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue(""); // ID (Empty for new)
            sampleRow.createCell(1).setCellValue("Sample Item Name");
            sampleRow.createCell(2).setCellValue("ITEM-001");
            sampleRow.createCell(3).setCellValue("SKU-1234");
            sampleRow.createCell(4).setCellValue("123456789"); // Barcode
            sampleRow.createCell(5).setCellValue("GOODS"); // Item Type
            sampleRow.createCell(6).setCellValue("Electronics");
            sampleRow.createCell(7).setCellValue("NOS");
            sampleRow.createCell(8).setCellValue("Samsung");
            sampleRow.createCell(9).setCellValue("Samsung Corp");
            sampleRow.createCell(10).setCellValue(100.00); // Purchase Price
            sampleRow.createCell(11).setCellValue(150.00); // Selling Price
            sampleRow.createCell(12).setCellValue(160.00); // MRP
            sampleRow.createCell(13).setCellValue(18.00);  // Tax
            sampleRow.createCell(14).setCellValue(5.00);   // Discount
            sampleRow.createCell(15).setCellValue("8517"); // HSN
            sampleRow.createCell(16).setCellValue("Description goes here");

            //Auto-size columns for better visibility
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error generating template: " + e.getMessage());
        }
    }

    //HELPER METHODS
    private static String getString(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((long) cell.getNumericCellValue());
        return null;
    }

    private static BigDecimal getBigDecimal(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        if (cell.getCellType() == CellType.NUMERIC) return BigDecimal.valueOf(cell.getNumericCellValue());
        if (cell.getCellType() == CellType.STRING) {
            try {
                return new BigDecimal(cell.getStringCellValue());
            } catch (Exception e) {
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    private static double getDouble(BigDecimal val) {
        return val != null ? val.doubleValue() : 0.0;
    }

    // Check if a row is essentially empty
    private static boolean isRowEmpty(Row row) {
        if (row == null) return true;
        if (row.getLastCellNum() <= 0) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty())
                return false;
        }
        return true;
    }
}