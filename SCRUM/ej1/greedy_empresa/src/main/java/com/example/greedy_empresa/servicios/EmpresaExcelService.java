package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Direccion;
import com.example.greedy_empresa.entidades.Empresa;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaExcelService {

    public byte[] generateEmpresasExcel(List<Empresa> empresas) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            // Create main sheet
            Sheet sheet = workbook.createSheet("Empresas");
            
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);
            
            int rowNum = 0;
            
            // Add title and generation info
            rowNum = addTitleAndInfo(sheet, rowNum, headerStyle, dateStyle);
            
            // Add headers
            rowNum = addHeaders(sheet, rowNum, headerStyle);
            
            // Add data rows
            addDataRows(sheet, rowNum, empresas, normalStyle);
            
            // Auto-size columns
            autoSizeColumns(sheet, 4); // 4 columns total
            
            workbook.write(baos);
            return baos.toByteArray();
        }
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setItalic(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
    
    private CellStyle createNormalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }
    
    private int addTitleAndInfo(Sheet sheet, int rowNum, CellStyle headerStyle, CellStyle dateStyle) {
        // Title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("REPORTE DE EMPRESAS REGISTRADAS");
        titleCell.setCellStyle(headerStyle);
        
        // Merge title cells
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), 0, 3));
        
        // Date row
        Row dateRow = sheet.createRow(rowNum++);
        Cell dateCell = dateRow.createCell(3);
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        dateCell.setCellValue("Generado el: " + currentDate);
        dateCell.setCellStyle(dateStyle);
        
        // Empty row
        sheet.createRow(rowNum++);
        
        return rowNum;
    }
    
    private int addHeaders(Sheet sheet, int rowNum, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(rowNum++);
        
        String[] headers = {"ID", "Razón Social", "Direcciones", "Cantidad de Direcciones"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        return rowNum;
    }
    
    private void addDataRows(Sheet sheet, int startRowNum, List<Empresa> empresas, CellStyle normalStyle) {
        int rowNum = startRowNum;
        
        if (empresas.isEmpty()) {
            Row emptyRow = sheet.createRow(rowNum);
            Cell emptyCell = emptyRow.createCell(0);
            emptyCell.setCellValue("No hay empresas registradas");
            emptyCell.setCellStyle(normalStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum, rowNum, 0, 3));
            return;
        }
        
        for (Empresa empresa : empresas) {
            Row row = sheet.createRow(rowNum++);
            
            // ID
            Cell idCell = row.createCell(0);
            idCell.setCellValue(empresa.getId());
            idCell.setCellStyle(normalStyle);
            
            // Razón Social
            Cell razonSocialCell = row.createCell(1);
            razonSocialCell.setCellValue(empresa.getRazonSocial());
            razonSocialCell.setCellStyle(normalStyle);
            
            // Direcciones
            Cell direccionesCell = row.createCell(2);
            String direccionesText = formatDirecciones(empresa.getDirecciones());
            direccionesCell.setCellValue(direccionesText);
            direccionesCell.setCellStyle(normalStyle);
            
            // Cantidad de direcciones
            Cell cantidadCell = row.createCell(3);
            cantidadCell.setCellValue(empresa.getDirecciones().size());
            cantidadCell.setCellStyle(normalStyle);
        }
        
        // Add summary row
        Row summaryRow = sheet.createRow(rowNum + 1);
        Cell summaryCell = summaryRow.createCell(0);
        
        int totalDirecciones = empresas.stream()
                .mapToInt(e -> e.getDirecciones().size())
                .sum();
        
        String summaryText = String.format("RESUMEN: %d empresas registradas | %d direcciones totales", 
                empresas.size(), totalDirecciones);
        
        summaryCell.setCellValue(summaryText);
        
        CellStyle summaryStyle = sheet.getWorkbook().createCellStyle();
        Font summaryFont = sheet.getWorkbook().createFont();
        summaryFont.setBold(true);
        summaryStyle.setFont(summaryFont);
        summaryStyle.setAlignment(HorizontalAlignment.CENTER);
        summaryCell.setCellStyle(summaryStyle);
        
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(summaryRow.getRowNum(), summaryRow.getRowNum(), 0, 3));
    }
    
    private String formatDirecciones(List<Direccion> direcciones) {
        if (direcciones.isEmpty()) {
            return "Sin direcciones registradas";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < direcciones.size(); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            Direccion dir = direcciones.get(i);
            sb.append(formatDireccion(dir));
        }
        
        return sb.toString();
    }
    
    private String formatDireccion(Direccion direccion) {
        StringBuilder sb = new StringBuilder();
        sb.append(direccion.getCalle()).append(" ").append(direccion.getNumero());
        
        if (direccion.getNumeroInterno() != null && !direccion.getNumeroInterno().isBlank()) {
            sb.append(" Int. ").append(direccion.getNumeroInterno());
        }
        
        if (direccion.getCasaPiso() != null && !direccion.getCasaPiso().isBlank()) {
            sb.append(" - ").append(direccion.getCasaPiso());
        }
        
        if (direccion.getLocalidad() != null) {
            sb.append(", ").append(direccion.getLocalidad().getNombre());
            if (direccion.getLocalidad().getDepartamento() != null) {
                sb.append(" (").append(direccion.getLocalidad().getDepartamento().getNombre()).append(")");
            }
        }
        
        return sb.toString();
    }
    
    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            
            // Set minimum and maximum column widths
            int currentWidth = sheet.getColumnWidth(i);
            int minWidth = 2000; // ~10 characters
            int maxWidth = 15000; // ~75 characters
            
            if (currentWidth < minWidth) {
                sheet.setColumnWidth(i, minWidth);
            } else if (currentWidth > maxWidth) {
                sheet.setColumnWidth(i, maxWidth);
            }
        }
    }
}