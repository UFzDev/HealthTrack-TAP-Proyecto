package ufzdev.HealthTrack.reports.strategies;

import java.nio.file.Path;
import java.util.List;

public interface ExportStrategy {
    Path export(List<TaskModel> data) throws Exception;
}

