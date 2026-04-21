package ufzdev.todo_list.util;

import ufzdev.todo_list.models.CategoryModel;
import ufzdev.todo_list.models.TaskModel;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class StatisticsChartUtil {

    private static final String STATUS_COMPLETED = "Completada";
    private static final String STATUS_IN_PROGRESS = "En progreso";

    private StatisticsChartUtil() {
    }

    public static List<TaskModel> filterByPeriod(List<TaskModel> source, StatisticsPeriod period) {
        List<TaskModel> filtered = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            return filtered;
        }

        StatisticsPeriod currentPeriod = period == null ? StatisticsPeriod.MONTH : period;
        LocalDate now = LocalDate.now();

        for (TaskModel task : source) {
            if (task == null || task.getLimitDate() == null) {
                continue;
            }

            LocalDate taskDate = toLocalDate(task.getLimitDate());
            if (isInsidePeriod(taskDate, now, currentPeriod)) {
                filtered.add(task);
            }
        }

        return filtered;
    }

    public static Map<String, Integer> buildCompletionCounts(List<TaskModel> tasks) {
        int completed = 0;
        int inProgress = 0;
        int notStarted = 0;

        if (tasks != null) {
            for (TaskModel task : tasks) {
                if (task == null) {
                    continue;
                }

                String status = task.getStatus();
                if (isCompletedStatus(status)) {
                    completed++;
                } else if (isInProgressStatus(status)) {
                    inProgress++;
                } else {
                    notStarted++;
                }
            }
        }

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("No completada", notStarted);
        result.put("En progreso", inProgress);
        result.put("Completada", completed);
        return result;
    }

    public static Map<String, Integer> buildCategoryCounts(List<TaskModel> tasks) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        if (tasks == null || tasks.isEmpty()) {
            return counts;
        }

        for (TaskModel task : tasks) {
            if (task == null) {
                continue;
            }

            List<CategoryModel> categories = task.getCategory();
            if (categories == null || categories.isEmpty()) {
                counts.merge("Sin categoria", 1, Integer::sum);
                continue;
            }

            boolean countedAny = false;
            for (CategoryModel category : categories) {
                if (category == null || category.getName() == null || category.getName().isBlank()) {
                    continue;
                }
                counts.merge(category.getName().trim(), 1, Integer::sum);
                countedAny = true;
            }

            if (!countedAny) {
                counts.merge("Sin categoria", 1, Integer::sum);
            }
        }

        return counts;
    }

    private static LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static boolean isInsidePeriod(LocalDate taskDate, LocalDate now, StatisticsPeriod period) {
        return switch (period) {
            case WEEK -> isSameWeek(taskDate, now);
            case MONTH -> taskDate.getYear() == now.getYear() && taskDate.getMonthValue() == now.getMonthValue();
            case YEAR -> taskDate.getYear() == now.getYear();
        };
    }

    private static boolean isSameWeek(LocalDate a, LocalDate b) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekA = a.get(weekFields.weekOfWeekBasedYear());
        int weekB = b.get(weekFields.weekOfWeekBasedYear());
        int weekYearA = a.get(weekFields.weekBasedYear());
        int weekYearB = b.get(weekFields.weekBasedYear());

        return weekYearA == weekYearB && weekA == weekB;
    }

    private static boolean isCompletedStatus(String status) {
        if (status == null) {
            return false;
        }
        return STATUS_COMPLETED.equalsIgnoreCase(status.trim());
    }

    private static boolean isInProgressStatus(String status) {
        if (status == null) {
            return false;
        }
        return STATUS_IN_PROGRESS.equalsIgnoreCase(status.trim());
    }
}
