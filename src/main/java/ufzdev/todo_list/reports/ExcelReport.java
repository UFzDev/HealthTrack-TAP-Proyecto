package ufzdev.todo_list.reports;

import ufzdev.todo_list.models.TaskModel;
import ufzdev.todo_list.reports.strategies.ExcelExportStrategy;
import ufzdev.todo_list.reports.strategies.ExportStrategy;

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

