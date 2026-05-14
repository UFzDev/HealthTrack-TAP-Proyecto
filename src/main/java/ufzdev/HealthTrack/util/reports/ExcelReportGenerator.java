package ufzdev.HealthTrack.util.reports;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExcelReportGenerator implements ReportGenerator {
    private final Path outputDir;

    public ExcelReportGenerator() {
        this.outputDir = Paths.get(System.getProperty("user.home"), "HealthTrack", "Reports");
    }

    @Override
    public Path generate(String title, String[] headers, List<String[]> rows, String fileName) throws Exception {
        Files.createDirectories(outputDir);
        Path outputPath = outputDir.resolve(fileName + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook();
             OutputStream outputStream = Files.newOutputStream(outputPath)) {
            
            createSheet(workbook, title, headers, rows);
            workbook.write(outputStream);
        }
        return outputPath;
    }

    @Override
    public Path generateMultiSection(String mainTitle, List<ReportSection> sections, String fileName) throws Exception {
        Files.createDirectories(outputDir);
        Path outputPath = outputDir.resolve(fileName + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook();
             OutputStream outputStream = Files.newOutputStream(outputPath)) {
            
            for (ReportSection section : sections) {
                createSheet(workbook, section.title, section.headers, section.rows);
            }
            workbook.write(outputStream);
        }
        return outputPath;
    }

    private void createSheet(Workbook workbook, String title, String[] headers, List<String[]> rows) {
        String safeTitle = title != null ? title.replaceAll("[\\\\/*?:\\[\\]]", "_") : "Hoja";
        if (safeTitle.length() > 31) safeTitle = safeTitle.substring(0, 31);
        
        Sheet sheet = workbook.createSheet(safeTitle);
        
        // Estilo de encabezado
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Crear filas de datos
        int rowIndex = 1;
        for (String[] rowData : rows) {
            Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < rowData.length; i++) {
                row.createCell(i).setCellValue(rowData[i] != null ? rowData[i] : "");
            }
        }

        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception e) {
                // Ignore if auto-size fails
            }
        }
    }
}
