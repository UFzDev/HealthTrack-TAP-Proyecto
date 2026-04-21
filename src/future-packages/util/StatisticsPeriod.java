package ufzdev.todo_list.util;

public enum StatisticsPeriod {
    WEEK("Semana"),
    MONTH("Mes"),
    YEAR("Año");

    private final String label;

    StatisticsPeriod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static StatisticsPeriod fromLabel(String value) {
        if (value == null || value.isBlank()) {
            return MONTH;
        }

        for (StatisticsPeriod period : values()) {
            if (period.getLabel().equalsIgnoreCase(value.trim())) {
                return period;
            }
        }

        return MONTH;
    }
}

