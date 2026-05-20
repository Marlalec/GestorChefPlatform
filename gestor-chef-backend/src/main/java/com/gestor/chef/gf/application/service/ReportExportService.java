package com.gestor.chef.gf.application.service;

import com.gestor.chef.gf.domain.model.GeneratedReport;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportExportService {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("America/Bogota"));

    public byte[] exportToPdf(GeneratedReport report) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 60, 40);
            PdfWriter.getInstance(doc, bos);
            doc.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, Color.WHITE);
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
            Font metaFont  = new Font(Font.HELVETICA,  9, Font.ITALIC, Color.GRAY);

            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(100);
            PdfPCell titleCell = new PdfPCell(new Phrase(report.getTitle(), titleFont));
            titleCell.setBackgroundColor(new Color(0x2C, 0x3E, 0x50));
            titleCell.setPadding(12);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleTable.addCell(titleCell);
            doc.add(titleTable);
            doc.add(Chunk.NEWLINE);

            String generatedAt = report.getGeneratedAt() != null ? FMT.format(report.getGeneratedAt()) : "-";
            String period = (report.getPeriodStart() != null ? report.getPeriodStart() : "") +
                            (report.getPeriodEnd()   != null ? " — " + report.getPeriodEnd() : "");
            doc.add(new Paragraph("Generado: " + generatedAt, metaFont));
            if (!period.isBlank()) doc.add(new Paragraph("Período: " + period, metaFont));
            doc.add(Chunk.NEWLINE);

            if (report.getData() != null) {
                renderMapAsPdfTable(doc, report.getData(), headerFont, normalFont);
            }

            doc.close();
            log.info("[ReportExport] PDF generado para reporte id={}", report.getId());
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("[ReportExport] Error generando PDF: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo generar el PDF: " + e.getMessage(), e);
        }
    }

    private void renderMapAsPdfTable(Document doc, Map<String, Object> data,
                                     Font headerFont, Font normalFont) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 5f});

        addPdfCell(table, "Campo",  headerFont, new Color(0x34, 0x49, 0x5E), true);
        addPdfCell(table, "Valor",  headerFont, new Color(0x34, 0x49, 0x5E), true);

        boolean alternate = false;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Color bg = alternate ? new Color(0xF2, 0xF2, 0xF2) : Color.WHITE;
            addPdfCell(table, formatKey(entry.getKey()),         normalFont, bg, false);
            addPdfCell(table, formatValue(entry.getValue()),     normalFont, bg, false);
            alternate = !alternate;
        }
        doc.add(table);
    }

    private void addPdfCell(PdfPTable table, String text, Font font, Color bg, boolean bold) {
        PdfPCell cell = new PdfPCell(new Phrase(text,
                bold ? new Font(Font.HELVETICA, font.getSize(), Font.BOLD, font.getColor()) : font));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setBorderColor(new Color(0xCC, 0xCC, 0xCC));
        table.addCell(cell);
    }

    public byte[] exportToExcel(GeneratedReport report) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sanitizeSheetName(report.getTitle()));
            sheet.setColumnWidth(0, 10_000);
            sheet.setColumnWidth(1, 18_000);

            CellStyle titleStyle = buildStyle(workbook, (short)14, true,
                    IndexedColors.DARK_TEAL.getIndex(), IndexedColors.WHITE.getIndex());
            CellStyle headerStyle = buildStyle(workbook, (short)11, true,
                    IndexedColors.GREY_50_PERCENT.getIndex(), IndexedColors.WHITE.getIndex());
            CellStyle metaStyle   = buildStyle(workbook, (short)10, false,
                    IndexedColors.WHITE.getIndex(), IndexedColors.GREY_80_PERCENT.getIndex());
            CellStyle evenStyle   = buildStyle(workbook, (short)10, false,
                    IndexedColors.WHITE.getIndex(), IndexedColors.AUTOMATIC.getIndex());
            CellStyle oddStyle    = buildStyle(workbook, (short)10, false,
                    IndexedColors.GREY_25_PERCENT.getIndex(), IndexedColors.AUTOMATIC.getIndex());

            int row = 0;

            Row titleRow = sheet.createRow(row++);
            titleRow.setHeightInPoints(24);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(report.getTitle());
            titleCell.setCellStyle(titleStyle);

            String generatedAt = report.getGeneratedAt() != null ? FMT.format(report.getGeneratedAt()) : "-";
            row = addMetaRow(sheet, row, "Generado", generatedAt, metaStyle);
            if (report.getPeriodStart() != null || report.getPeriodEnd() != null) {
                String period = (report.getPeriodStart() != null ? report.getPeriodStart() : "") +
                                (report.getPeriodEnd()   != null ? " — " + report.getPeriodEnd() : "");
                row = addMetaRow(sheet, row, "Período", period, metaStyle);
            }
            row++;

            Row hdrRow = sheet.createRow(row++);
            hdrRow.setHeightInPoints(16);
            createCell(hdrRow, 0, "Campo", headerStyle);
            createCell(hdrRow, 1, "Valor", headerStyle);

            if (report.getData() != null) {
                boolean alternate = false;
                for (Map.Entry<String, Object> entry : report.getData().entrySet()) {
                    Row dataRow = sheet.createRow(row++);
                    CellStyle style = alternate ? oddStyle : evenStyle;
                    createCell(dataRow, 0, formatKey(entry.getKey()), style);
                    createCell(dataRow, 1, formatValue(entry.getValue()), style);
                    alternate = !alternate;
                }
            }

            workbook.write(bos);
            log.info("[ReportExport] XLSX generado para reporte id={}", report.getId());
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("[ReportExport] Error generando XLSX: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo generar el Excel: " + e.getMessage(), e);
        }
    }

    private CellStyle buildStyle(Workbook wb, short fontSize, boolean bold,
                                 short bgIndex, short fontColorIndex) {
        CellStyle style = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = wb.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setBold(bold);
        font.setColor(fontColorIndex);
        style.setFont(font);
        style.setFillForegroundColor(bgIndex);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private int addMetaRow(Sheet sheet, int row, String key, String value, CellStyle style) {
        Row r = sheet.createRow(row);
        createCell(r, 0, key,   style);
        createCell(r, 1, value, style);
        return row + 1;
    }

    private String formatKey(String key) {

        return key.replaceAll("([A-Z])", " $1")
                  .replaceFirst("^.", String.valueOf(Character.toUpperCase(key.charAt(0))));
    }

    @SuppressWarnings("unchecked")
    private String formatValue(Object value) {
        if (value == null) return "-";
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            StringBuilder sb = new StringBuilder();
            map.forEach((k, v) -> sb.append(k).append(": ").append(v).append(" | "));
            return sb.length() > 3 ? sb.substring(0, sb.length() - 3) : sb.toString();
        }
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            return list.stream()
                    .map(item -> item instanceof Map
                            ? ((Map<?,?>) item).values().toString()
                            : String.valueOf(item))
                    .reduce("", (a, b) -> a.isEmpty() ? b : a + " | " + b);
        }
        if (value instanceof Double d) {
            return String.format("%.2f", d);
        }
        return String.valueOf(value);
    }

    private String sanitizeSheetName(String name) {
        if (name == null) return "Reporte";
        return name.replaceAll("[\\\\/:*?\\[\\]]", "_")
                   .substring(0, Math.min(31, name.length()));
    }
}
