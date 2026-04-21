package ufzdev.todo_list.reports;

import ufzdev.todo_list.models.TaskModel;

import java.nio.file.Path;
import java.util.List;

public interface Report {
    Path export(List<TaskModel> data) throws Exception;
}

