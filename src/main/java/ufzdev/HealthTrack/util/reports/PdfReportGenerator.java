package ufzdev.HealthTrack.util.reports;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfReportGenerator implements ReportGenerator {
    private final Path outputDir;

    public PdfReportGenerator() {
        // Carpeta por defecto en el escritorio del usuario o carpeta de la app
        this.outputDir = Paths.get(System.getProperty("user.home"), "HealthTrack", "Reports");
    }

    @Override
    public Path generate(String title, String[] headers, List<String[]> rows, String fileName) throws Exception {
        Files.createDirectories(outputDir);
        Path outputPath = outputDir.resolve(fileName + ".pdf");

        try (PdfWriter writer = new PdfWriter(outputPath.toFile());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Título
            document.add(new Paragraph(title).setBold().setFontSize(18));
            document.add(new Paragraph("Generado el: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            document.add(new Paragraph(" "));

            // Tabla
            Table table = new Table(UnitValue.createPercentArray(headers.length));
            table.useAllAvailableWidth();

            // Encabezados
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            // Datos
            for (String[] row : rows) {
                for (String cellData : row) {
                    table.addCell(new Cell().add(new Paragraph(cellData != null ? cellData : "")));
                }
            }

            document.add(table);
        }

        return outputPath;
    }
}
