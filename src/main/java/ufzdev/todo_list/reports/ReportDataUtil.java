package ufzdev.todo_list.reports;

import ufzdev.todo_list.models.CategoryModel;
import ufzdev.todo_list.models.TaskModel;

import java.text.SimpleDateFormat;
import java.util.List;

public final class ReportDataUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private ReportDataUtil() {
    }

    public static String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    public static String formatDate(java.util.Date date) {
        if (date == null) {
            return "-";
        }
        return DATE_FORMAT.format(date);
    }

    public static String categoriesAsText(List<CategoryModel> categories) {
        if (categories == null || categories.isEmpty()) {
            return "-";
        }

        StringBuilder builder = new StringBuilder();
        for (CategoryModel category : categories) {
            if (category == null || category.getName() == null || category.getName().isBlank()) {
                continue;
            }

            if (!builder.isEmpty()) {
                builder.append(", ");
            }
            builder.append(category.getName());
        }

        return builder.isEmpty() ? "-" : builder.toString();
    }

    public static String[] toRow(TaskModel task) {
        return new String[] {
                safe(task.getName()),
                safe(task.getDescription()),
                categoriesAsText(task.getCategory()),
                safe(task.getStatus()),
                formatDate(task.getCreatedAt()),
                formatDate(task.getLimitDate())
        };
    }
}

