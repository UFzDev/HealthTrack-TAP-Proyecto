package ufzdev.HealthTrack.models;

import java.time.LocalDateTime;

/**
 * Modelo base de una medicion clinica para estadisticas y graficas.
 */
public class HealthMetricModel {
    private String id;
    private String userId;
    private String metricType;
    private double value;
    private LocalDateTime recordedAt;

    public HealthMetricModel() {
    }

    public HealthMetricModel(String userId, String metricType, double value, LocalDateTime recordedAt) {
        this.userId = userId;
        this.metricType = metricType;
        this.value = value;
        this.recordedAt = recordedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }
}

