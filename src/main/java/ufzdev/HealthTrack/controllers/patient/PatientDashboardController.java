package ufzdev.HealthTrack.controllers.patient;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import ufzdev.HealthTrack.dao.HealthMetricFirestoreDao;
import ufzdev.HealthTrack.models.HealthMetricModel;
import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.util.UserSessionUtil;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controlador del dashboard del paciente.
 * Carga exclusivamente datos reales desde Firestore (mediciones del usuario actual).
 * Sin datos inventados ni hardcodeados.
 */
public class PatientDashboardController implements Initializable {

    @FXML
    private Label bpValue;
    @FXML
    private Label glucoseValue;
    @FXML
    private Label imcValue;
    @FXML
    private Label heartRateValue;
    @FXML
    private Label trendStatusLabel;
    @FXML
    private Label lastRecordLabel;
    @FXML
    private LineChart<Integer, Number> trendChart;
    @FXML
    private BarChart<String, Number> averagesChart;
    @FXML
    private ComboBox<String> periodSelector;

    private HealthMetricFirestoreDao metricDao;
    private UserModel currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        metricDao = new HealthMetricFirestoreDao();
        currentUser = UserSessionUtil.getInstance().getUser();

        periodSelector.setItems(FXCollections.observableArrayList("Semana", "Mes", "Año"));
        periodSelector.getSelectionModel().selectFirst();

        loadMetricsFromDatabase();
    }

    /**
     * Carga todas las métricas del usuario desde Firestore.
     */
    private void loadMetricsFromDatabase() {
        if (currentUser == null || currentUser.getId() == null) {
            showNoData();
            return;
        }

        try {
            List<HealthMetricModel> allMetrics = metricDao.getByUserId(currentUser.getId());

            if (allMetrics == null || allMetrics.isEmpty()) {
                showNoData();
                setAlertStyle("Sin datos registrados aún. Registra tu primera medición.", "alert-ok");
                return;
            }

            // Extrae la última medición de cada tipo
            updateLatestMetrics(allMetrics);

            // Carga historial y gráficos
            String selectedPeriod = periodSelector.getValue();
            renderCharts(selectedPeriod != null ? selectedPeriod : "Semana", allMetrics);

            // Actualiza alertas basadas en los datos más recientes
            updateAlertStatus(allMetrics);

        } catch (Exception e) {
            System.err.println("Error cargando métricas: " + e.getMessage());
            showNoData();
            setAlertStyle("No se pudo cargar los datos. Intenta de nuevo.", "alert-warning");
        }
    }

    /**
     * Actualiza los valores de las métricas más recientes en el UI.
     */
    private void updateLatestMetrics(List<HealthMetricModel> allMetrics) {
        Map<String, Double> latestByType = new HashMap<>();
        LocalDateTime latestTime = null;

        // Busca la última medición de cada tipo
        for (HealthMetricModel metric : allMetrics) {
            String type = metric.getMetricType();
            if (!latestByType.containsKey(type)) {
                latestByType.put(type, metric.getValue());
                if (latestTime == null) {
                    latestTime = metric.getRecordedAt();
                }
            }
        }

        // Actualiza los Labels con los valores más recientes o "-" si no existen
        bpValue.setText(latestByType.getOrDefault("Presión", null) != null
            ? String.valueOf(latestByType.get("Presión").intValue()) + " mmHg"
            : "-");
        glucoseValue.setText(latestByType.getOrDefault("Glucosa", null) != null
            ? String.valueOf(latestByType.get("Glucosa").intValue()) + " mg/dL"
            : "-");
        heartRateValue.setText(latestByType.getOrDefault("Frecuencia Cardíaca", null) != null
            ? String.valueOf(latestByType.get("Frecuencia Cardíaca").intValue()) + " bpm"
            : "-");

        // IMC desde la sesión del usuario
        imcValue.setText(currentUser.getImc() > 0 ? String.format("%.1f", currentUser.getImc()) : "-");

        // Última fecha de registro
        if (latestTime != null) {
            lastRecordLabel.setText("Último registro: " + latestTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        } else {
            lastRecordLabel.setText("Último registro: -");
        }
    }

    /**
     * Muestra placeholders cuando no hay datos.
     */
    private void showNoData() {
        bpValue.setText("-");
        glucoseValue.setText("-");
        heartRateValue.setText("-");
        imcValue.setText("-");
        lastRecordLabel.setText("Último registro: -");
        trendChart.getData().clear();
        averagesChart.getData().clear();
    }

    /**
     * Renderiza los gráficos basados en datos reales.
     */
    private void renderCharts(String period, List<HealthMetricModel> metrics) {
        trendChart.getData().clear();
        averagesChart.getData().clear();

        // Filtra métricas de Glucosa para la tendencia
        XYChart.Series<Integer, Number> glucosaSeries = new XYChart.Series<>();
        glucosaSeries.setName("Glucosa");

        int dayCounter = 1;
        for (HealthMetricModel metric : metrics) {
            if ("Glucosa".equalsIgnoreCase(metric.getMetricType())) {
                glucosaSeries.getData().add(new XYChart.Data<>(dayCounter++, metric.getValue()));
                if (dayCounter > 7) break; // Limita a 7 días en la gráfica
            }
        }

        if (!glucosaSeries.getData().isEmpty()) {
            trendChart.getData().add(glucosaSeries);
        }

        // Calcula promedios para el gráfico de barras
        Map<String, List<Double>> valuesByType = new HashMap<>();
        for (HealthMetricModel metric : metrics) {
            valuesByType.computeIfAbsent(metric.getMetricType(), k -> new ArrayList<>())
                    .add(metric.getValue());
        }

        XYChart.Series<String, Number> averagesSeries = new XYChart.Series<>();
        for (Map.Entry<String, List<Double>> entry : valuesByType.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            averagesSeries.getData().add(new XYChart.Data<>(entry.getKey(), avg));
        }

        if (!averagesSeries.getData().isEmpty()) {
            averagesChart.getData().add(averagesSeries);
        }
    }

    @FXML
    private void handlePeriodChange() {
        loadMetricsFromDatabase();
    }

    /**
     * Actualiza el estado de alerta basado en los valores más recientes.
     */
    private void updateAlertStatus(List<HealthMetricModel> allMetrics) {
        if (allMetrics.isEmpty()) {
            setAlertStyle("Sin datos registrados aún", "alert-ok");
            return;
        }

        Map<String, Double> latestByType = new HashMap<>();
        for (HealthMetricModel metric : allMetrics) {
            String type = metric.getMetricType();
            if (!latestByType.containsKey(type)) {
                latestByType.put(type, metric.getValue());
            }
        }

        Double pressure = latestByType.get("Presión");
        Double glucose = latestByType.get("Glucosa");
        Double heartRate = latestByType.get("Frecuencia Cardíaca");

        // Validación de alertas
        if (pressure != null && pressure >= 140) {
            setAlertStyle("⚠ Alerta: Presión muy alta. Contacta a tu médico.", "alert-critical");
            return;
        }
        if (glucose != null && glucose >= 180) {
            setAlertStyle("⚠ Alerta: Glucosa muy alta. Contacta a tu médico.", "alert-critical");
            return;
        }
        if (heartRate != null && heartRate >= 110) {
            setAlertStyle("⚠ Alerta: Frecuencia cardíaca muy alta. Contacta a tu médico.", "alert-critical");
            return;
        }

        if ((pressure != null && pressure >= 130) || (glucose != null && glucose >= 126)) {
            setAlertStyle("⚠ Atención: Valores en rango de riesgo. Monitorea tus métricas.", "alert-warning");
            return;
        }

        setAlertStyle("✓ Sin alertas críticas en este momento", "alert-ok");
    }

    /**
     * Establece el estilo de la alerta.
     */
    private void setAlertStyle(String message, String styleClass) {
        trendStatusLabel.setText(message);
        trendStatusLabel.getStyleClass().removeAll("alert-ok", "alert-warning", "alert-critical");
        trendStatusLabel.getStyleClass().add(styleClass);
    }

    // Registro de medición se realiza desde el modal `quick-record-modal.fxml`.
}

