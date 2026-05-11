package ufzdev.HealthTrack.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class StatisticsChartUtil {

    public static final class MetricPoint {
        private final String metricType;
        private final double value;
        private final LocalDateTime recordedAt;

        public MetricPoint(String metricType, double value, LocalDateTime recordedAt) {
            this.metricType = metricType;
            this.value = value;
            this.recordedAt = recordedAt;
        }

        public String getMetricType() {
            return metricType;
        }

        public double getValue() {
            return value;
        }

        public LocalDateTime getRecordedAt() {
            return recordedAt;
        }
    }

    private static final String RISK_CRITICAL = "Critico";
    private static final String RISK_WARNING = "Riesgo";
    private static final String RISK_NORMAL = "Normal";

    private StatisticsChartUtil() {
    }

    public static List<MetricPoint> filterByPeriod(List<MetricPoint> source, StatisticsPeriod period) {
        List<MetricPoint> filtered = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            return filtered;
        }

        StatisticsPeriod currentPeriod = period == null ? StatisticsPeriod.MONTH : period;
        LocalDate now = LocalDate.now();

        for (MetricPoint metric : source) {
            if (metric == null || metric.getRecordedAt() == null) {
                continue;
            }

            LocalDate metricDate = metric.getRecordedAt().toLocalDate();
            if (isInsidePeriod(metricDate, now, currentPeriod)) {
                filtered.add(metric);
            }
        }

        return filtered;
    }

    public static Map<String, Integer> buildRiskCounts(List<MetricPoint> metrics) {
        int normal = 0;
        int warning = 0;
        int critical = 0;

        if (metrics != null) {
            for (MetricPoint metric : metrics) {
                if (metric == null) {
                    continue;
                }

                String risk = resolveRisk(metric);
                if (RISK_CRITICAL.equals(risk)) {
                    critical++;
                } else if (RISK_WARNING.equals(risk)) {
                    warning++;
                } else {
                    normal++;
                }
            }
        }

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put(RISK_NORMAL, normal);
        result.put(RISK_WARNING, warning);
        result.put(RISK_CRITICAL, critical);
        return result;
    }

    public static Map<String, Integer> buildMetricTypeCounts(List<MetricPoint> metrics) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        if (metrics == null || metrics.isEmpty()) {
            return counts;
        }

        for (MetricPoint metric : metrics) {
            if (metric == null) {
                continue;
            }

            String metricType = sanitizeMetricType(metric.getMetricType());
            counts.merge(metricType, 1, Integer::sum);
        }

        return counts;
    }

    // Alias de compatibilidad con código migrado desde future-packages.
    public static Map<String, Integer> buildCompletionCounts(List<MetricPoint> metrics) {
        return buildRiskCounts(metrics);
    }

    // Alias de compatibilidad con código migrado desde future-packages.
    public static Map<String, Integer> buildCategoryCounts(List<MetricPoint> metrics) {
        return buildMetricTypeCounts(metrics);
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

    private static String resolveRisk(MetricPoint metric) {
        String type = sanitizeMetricType(metric.getMetricType());
        double value = metric.getValue();

        if ("PRESION_SISTOLICA".equals(type)) {
            if (value >= 140) {
                return RISK_CRITICAL;
            }
            if (value >= 130) {
                return RISK_WARNING;
            }
            return RISK_NORMAL;
        }

        if ("PRESION_DIASTOLICA".equals(type)) {
            if (value >= 90) {
                return RISK_CRITICAL;
            }
            if (value >= 85) {
                return RISK_WARNING;
            }
            return RISK_NORMAL;
        }

        if ("GLUCOSA".equals(type)) {
            if (value >= 180) {
                return RISK_CRITICAL;
            }
            if (value >= 126) {
                return RISK_WARNING;
            }
            return RISK_NORMAL;
        }

        if ("FRECUENCIA_CARDIACA".equals(type)) {
            if (value >= 110 || value < 45) {
                return RISK_CRITICAL;
            }
            if (value >= 100 || value < 55) {
                return RISK_WARNING;
            }
            return RISK_NORMAL;
        }

        if ("IMC".equals(type)) {
            if (value >= 35 || value < 16) {
                return RISK_CRITICAL;
            }
            if (value >= 30 || value < 18.5) {
                return RISK_WARNING;
            }
            return RISK_NORMAL;
        }

        return RISK_NORMAL;
    }

    private static String sanitizeMetricType(String metricType) {
        if (metricType == null || metricType.isBlank()) {
            return "DESCONOCIDA";
        }
        return metricType.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
    }
}

