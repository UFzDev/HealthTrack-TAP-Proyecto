package ufzdev.todo_list.reports;

import java.nio.file.Path;

public class ReportFactory {
    private final Path outputDirectory;
    private final String fileNameBase;

    public ReportFactory(Path outputDirectory, String fileNameBase) {
        this.outputDirectory = outputDirectory;
        this.fileNameBase = fileNameBase;
    }

    public Report getReport(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Tipo de reporte no válido");
        }

        if ("PDF".equalsIgnoreCase(type.trim())) {
            return new PdfReport(outputDirectory, fileNameBase);
        }

        if ("XLSX".equalsIgnoreCase(type.trim()) || "EXCEL".equalsIgnoreCase(type.trim())) {
            return new ExcelReport(outputDirectory, fileNameBase);
        }

        throw new IllegalArgumentException("Formato no soportado: " + type);
    }
}

