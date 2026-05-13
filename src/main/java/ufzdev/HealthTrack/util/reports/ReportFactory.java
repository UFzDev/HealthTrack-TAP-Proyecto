package ufzdev.HealthTrack.util.reports;

public class ReportFactory {
    public enum ReportType {
        PDF, EXCEL
    }

    public static ReportGenerator getGenerator(ReportType type) {
        switch (type) {
            case PDF:
                return new PdfReportGenerator();
            case EXCEL:
                return new ExcelReportGenerator();
            default:
                throw new IllegalArgumentException("Tipo de reporte no soportado: " + type);
        }
    }
}
