package ufzdev.todo_list.reports.strategies;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ufzdev.todo_list.reports.ReportDataUtil;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ExcelExportStrategy implements ExportStrategy {
    private static final String[] HEADERS = {"TAREA", "DESCRIPCION", "CATEGORIAS", "ESTADO", "CREACION", "VENCIMIENTO"};

    private final Path outputDirectory;
    private final String fileNameBase;

    public ExcelExportStrategy(Path outputDirectory, String fileNameBase) {
        this.outputDirectory = outputDirectory;
        this.fileNameBase = fileNameBase;
    }

    @Override
    public Path export(List<TaskModel> data) throws Exception {
        Files.createDirectories(outputDirectory);
        Path outputPath = outputDirectory.resolve(fileNameBase + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook(); OutputStream outputStream = Files.newOutputStream(outputPath)) {
            Sheet sheet = workbook.createSheet("Tareas");
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell headerCell = headerRow.createCell(i);
                headerCell.setCellValue(HEADERS[i]);
                headerCell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (TaskModel task : data) {
                Row row = sheet.createRow(rowIndex++);
                String[] values = ReportDataUtil.toRow(task);
                for (int i = 0; i < values.length; i++) {
                    row.createCell(i).setCellValue(values[i]);
                }
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }

        return outputPath;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}

