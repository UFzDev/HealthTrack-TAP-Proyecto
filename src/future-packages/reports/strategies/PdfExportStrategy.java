package ufzdev.todo_list.reports.strategies;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import ufzdev.todo_list.reports.ReportDataUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

public class PdfExportStrategy implements ExportStrategy {
    private static final String[] HEADERS = {"TAREA", "DESCRIPCION", "CATEGORIAS", "ESTADO", "CREACION", "VENCIMIENTO"};

    private final Path outputDirectory;
    private final String fileNameBase;

    public PdfExportStrategy(Path outputDirectory, String fileNameBase) {
        this.outputDirectory = outputDirectory;
        this.fileNameBase = fileNameBase;
    }

    @Override
    public Path export(List<TaskModel> data) throws Exception {
        Files.createDirectories(outputDirectory);
        Path outputPath = outputDirectory.resolve(fileNameBase + ".pdf");

        try (PdfWriter writer = new PdfWriter(outputPath.toFile());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("Reporte de tareas").setBold().setFontSize(16));
            document.add(new Paragraph("Generado: " + ReportDataUtil.formatDate(new Date())));
            document.add(new Paragraph(" "));

            Table table = new Table(UnitValue.createPercentArray(new float[] {18, 28, 20, 12, 11, 11}));
            table.useAllAvailableWidth();

            for (String header : HEADERS) {
                table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            for (TaskModel task : data) {
                String[] row = ReportDataUtil.toRow(task);
                for (String value : row) {
                    table.addCell(new Cell().add(new Paragraph(value)));
                }
            }

            document.add(table);
        }

        return outputPath;
    }
}

