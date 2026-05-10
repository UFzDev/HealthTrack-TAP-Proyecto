package ufzdev.HealthTrack.reports;

import ufzdev.HealthTrack.models.TaskModel;
import ufzdev.HealthTrack.reports.strategies.ExcelExportStrategy;
import ufzdev.HealthTrack.reports.strategies.ExportStrategy;

import java.nio.file.Path;
import java.util.List;

public class ExcelReport implements Report {
    private final ExportStrategy exportStrategy;

    public ExcelReport(Path outputDirectory, String fileNameBase) {
        this.exportStrategy = new ExcelExportStrategy(outputDirectory, fileNameBase);
    }

    @Override
    public Path export(List<TaskModel> data) throws Exception {
        return exportStrategy.export(data);
    }
}

