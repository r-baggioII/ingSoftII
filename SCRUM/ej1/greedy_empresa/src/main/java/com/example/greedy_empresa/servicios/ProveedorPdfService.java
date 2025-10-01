package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Direccion;
import com.example.greedy_empresa.entidades.Proveedor;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorPdfService {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

    public byte[] generateProveedoresPdf(List<Proveedor> proveedores) throws DocumentException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Add title
            addTitle(document);
            
            // Add generation date
            addGenerationInfo(document);
            
            // Add proveedores table
            addProveedoresTable(document, proveedores);
            
        } finally {
            document.close();
        }
        
        return baos.toByteArray();
    }
    
    private void addTitle(Document document) throws DocumentException {
        Paragraph title = new Paragraph("Reporte de Proveedores", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);
    }
    
    private void addGenerationInfo(Document document) throws DocumentException {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph info = new Paragraph("Generado el: " + currentDate, SMALL_FONT);
        info.setAlignment(Element.ALIGN_RIGHT);
        info.setSpacingAfter(20f);
        document.add(info);
    }
    
    private void addProveedoresTable(Document document, List<Proveedor> proveedores) throws DocumentException {
        if (proveedores.isEmpty()) {
            Paragraph empty = new Paragraph("No hay proveedores registrados.", NORMAL_FONT);
            empty.setAlignment(Element.ALIGN_CENTER);
            document.add(empty);
            return;
        }
        
        // Create table with 3 columns: CUIT, Direcciones, Total Direcciones
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 4f, 1.5f});
        table.setSpacingBefore(10f);
        
        // Add headers
        addTableHeader(table);
        
        // Add data rows
        for (Proveedor proveedor : proveedores) {
            addProveedorRow(table, proveedor);
        }
        
        document.add(table);
        
        // Add summary
        addSummary(document, proveedores);
    }
    
    private void addTableHeader(PdfPTable table) {
        addHeaderCell(table, "CUIT");
        addHeaderCell(table, "Direcciones");
        addHeaderCell(table, "Cant. Direcciones");
    }
    
    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(8f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private void addProveedorRow(PdfPTable table, Proveedor proveedor) {
        // CUIT column
        PdfPCell cuitCell = new PdfPCell(new Phrase(proveedor.getCuit(), NORMAL_FONT));
        cuitCell.setPadding(8f);
        cuitCell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(cuitCell);
        
        // Direcciones column
        PdfPCell direccionesCell = new PdfPCell();
        direccionesCell.setPadding(8f);
        direccionesCell.setVerticalAlignment(Element.ALIGN_TOP);
        
        if (proveedor.getDirecciones().isEmpty()) {
            direccionesCell.addElement(new Phrase("Sin direcciones registradas", SMALL_FONT));
        } else {
            for (Direccion direccion : proveedor.getDirecciones()) {
                String direccionText = formatDireccion(direccion);
                direccionesCell.addElement(new Phrase(direccionText, SMALL_FONT));
                direccionesCell.addElement(new Phrase(" ", SMALL_FONT)); // Spacing
            }
        }
        table.addCell(direccionesCell);
        
        // Count column
        PdfPCell countCell = new PdfPCell(new Phrase(String.valueOf(proveedor.getDirecciones().size()), NORMAL_FONT));
        countCell.setPadding(8f);
        countCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        countCell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(countCell);
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
    
    private void addSummary(Document document, List<Proveedor> proveedores) throws DocumentException {
        int totalDirecciones = proveedores.stream()
                .mapToInt(p -> p.getDirecciones().size())
                .sum();
        
        Paragraph summary = new Paragraph(
            String.format("Total de proveedores: %d | Total de direcciones: %d", 
                proveedores.size(), totalDirecciones), 
            HEADER_FONT
        );
        summary.setAlignment(Element.ALIGN_CENTER);
        summary.setSpacingBefore(20f);
        document.add(summary);
    }
}