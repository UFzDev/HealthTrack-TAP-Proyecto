package ufzdev.HealthTrack.reports;

import ufzdev.HealthTrack.models.TaskModel;

import java.nio.file.Path;
import java.util.List;

public interface Report {
    Path export(List<TaskModel> data) throws Exception;
}

