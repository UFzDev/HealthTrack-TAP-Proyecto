package ufzdev.todo_list.reports;

import ufzdev.todo_list.models.TaskModel;
import ufzdev.todo_list.reports.strategies.ExportStrategy;
import ufzdev.todo_list.reports.strategies.PdfExportStrategy;

import java.nio.file.Path;
import java.util.List;

public class PdfReport implements Report {
    private final ExportStrategy exportStrategy;

    public PdfReport(Path outputDirectory, String fileNameBase) {
        this.exportStrategy = new PdfExportStrategy(outputDirectory, fileNameBase);
    }

    @Override
    public Path export(List<TaskModel> data) throws Exception {
        return exportStrategy.export(data);
    }
}

