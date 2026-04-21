package ufzdev.todo_list.services;

import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.reports.Report;
import ufzdev.todo_list.reports.ReportFactory;
import ufzdev.todo_list.util.UserSessionUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportService {
    private final UserSessionUtil session = UserSessionUtil.getInstance();

    public Path generatePdf(List<String> categoryFilters, List<String> statusFilters) throws Exception {
        return generate("PDF", categoryFilters, statusFilters);
    }

    public Path generateExcel(List<String> categoryFilters, List<String> statusFilters) throws Exception {
        return generate("XLSX", categoryFilters, statusFilters);
    }

    public Path generate(String type, List<String> categoryFilters,
                         List<String> statusFilters) throws Exception {
        UserModel currentUser = session.getUser();
        if (currentUser == null || currentUser.getId() == null || currentUser.getId().isBlank()) {
            throw new IllegalStateException("No hay sesión activa para generar el reporte.");
        }

        List<TaskModel> filteredTasks = filterTasksBySelection(
                session.getTasks(),
                currentUser.getId(),
                categoryFilters,
                statusFilters
        );

        if (filteredTasks.isEmpty()) {
            throw new IllegalStateException("No hay tareas para exportar con los filtros seleccionados.");
        }

        Path outputDirectory = resolveOutputDirectory(currentUser);
        String fileNameBase = buildFileName(type);

        ReportFactory reportFactory = new ReportFactory(outputDirectory, fileNameBase);
        Report report = reportFactory.getReport(type);
        return report.export(filteredTasks);
    }

    private List<TaskModel> filterTasksBySelection(List<TaskModel> tasks,
                                                   String userId,
                                                   List<String> selectedCategories,
                                                   List<String> selectedStatuses) {
        List<TaskModel> filtered = new ArrayList<>();
        for (TaskModel task : tasks) {
            if (task == null) {
                continue;
            }
            if (!matchesUser(task, userId)) {
                continue;
            }
            if (!matchesCategory(task, selectedCategories)) {
                continue;
            }
            if (!matchesStatus(task, selectedStatuses)) {
                continue;
            }
            filtered.add(task);
        }
        return filtered;
    }

    private boolean matchesUser(TaskModel task, String userId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        return userId.equals(task.getUserId());
    }

    private boolean matchesCategory(TaskModel task, List<String> selectedCategories) {
        if (selectedCategories == null || selectedCategories.isEmpty()) {
            return true;
        }
        if (task.getCategory() == null || task.getCategory().isEmpty()) {
            return false;
        }

        for (CategoryModel category : task.getCategory()) {
            if (category == null || category.getName() == null) {
                continue;
            }
            for (String selectedCategory : selectedCategories) {
                if (selectedCategory != null && selectedCategory.equalsIgnoreCase(category.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesStatus(TaskModel task, List<String> selectedStatuses) {
        if (selectedStatuses == null || selectedStatuses.isEmpty()) {
            return true;
        }
        if (task.getStatus() == null || task.getStatus().isBlank()) {
            return false;
        }

        for (String selectedStatus : selectedStatuses) {
            if (selectedStatus != null && selectedStatus.equalsIgnoreCase(task.getStatus())) {
                return true;
            }
        }

        return false;
    }

    private Path resolveOutputDirectory(UserModel user) {
        String folderUser = user.getUsername() == null || user.getUsername().isBlank()
                ? "usuario"
                : sanitize(user.getUsername());

        return Paths.get(System.getProperty("user.home"), "ToDoListReports", folderUser);
    }

    private String buildFileName(String type) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prefix = "PDF".equalsIgnoreCase(type) ? "reporte_tareas_pdf_" : "reporte_tareas_excel_";
        return prefix + timestamp;
    }

    private String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}

