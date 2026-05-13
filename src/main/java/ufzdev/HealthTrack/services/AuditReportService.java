package ufzdev.HealthTrack.services;

import ufzdev.HealthTrack.models.SystemLogModel;
import ufzdev.HealthTrack.util.reports.ReportFactory;
import ufzdev.HealthTrack.util.reports.ReportGenerator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AuditReportService {
    private static final String[] HEADERS = {"FECHA", "USUARIO", "ACCION", "DETALLES"};

    public static Path exportLogs(List<SystemLogModel> logs, ReportFactory.ReportType type) throws Exception {
        String title = "Reporte de Auditoría de Sistema";
        String fileName = "Audit_Report_" + System.currentTimeMillis();
        
        List<String[]> rows = new ArrayList<>();
        for (SystemLogModel log : logs) {
            rows.add(new String[]{
                log.getFormattedTimestamp(),
                log.getUser(),
                log.getAction(),
                log.getDetails()
            });
        }

        ReportGenerator generator = ReportFactory.getGenerator(type);
        return generator.generate(title, HEADERS, rows, fileName);
    }
}
