package ufzdev.HealthTrack.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.util.UserSessionUtil;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserModel user = UserSessionUtil.getInstance().getUser();
        bpValue.setText("120/80 mmHg");
        glucoseValue.setText("96 mg/dL");
        heartRateValue.setText("72 bpm");
        imcValue.setText(user != null && user.getImc() > 0 ? String.format("%.1f", user.getImc()) : "22.4");

        lastRecordLabel.setText("Ultimo registro: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        setAlertStyle("Sin alertas criticas en este momento", "alert-ok");

        periodSelector.setItems(FXCollections.observableArrayList("Semana", "Mes", "Ano"));
        periodSelector.getSelectionModel().selectFirst();
        renderCharts("Semana");
    }

    @FXML
    private void handlePeriodChange() {
        String selected = periodSelector.getValue() == null ? "Semana" : periodSelector.getValue();
        renderCharts(selected);
    }


    private void renderCharts(String period) {
        trendChart.getData().clear();
        averagesChart.getData().clear();

        XYChart.Series<Integer, Number> trendSeries = new XYChart.Series<>();
        trendSeries.setName("Glucosa");

        if ("Mes".equalsIgnoreCase(period)) {
            for (int i = 1; i <= 7; i++) {
                trendSeries.getData().add(new XYChart.Data<>(i, 90 + i * 3));
            }
        } else if ("Ano".equalsIgnoreCase(period)) {
            for (int i = 1; i <= 7; i++) {
                trendSeries.getData().add(new XYChart.Data<>(i, 88 + i * 4));
            }
        } else {
            trendSeries.getData().add(new XYChart.Data<>(1, 94));
            trendSeries.getData().add(new XYChart.Data<>(2, 98));
            trendSeries.getData().add(new XYChart.Data<>(3, 101));
            trendSeries.getData().add(new XYChart.Data<>(4, 96));
            trendSeries.getData().add(new XYChart.Data<>(5, 93));
            trendSeries.getData().add(new XYChart.Data<>(6, 97));
            trendSeries.getData().add(new XYChart.Data<>(7, 95));
        }
        trendChart.getData().add(trendSeries);

        XYChart.Series<String, Number> averagesSeries = new XYChart.Series<>();
        averagesSeries.getData().add(new XYChart.Data<>("Presion", 120));
        averagesSeries.getData().add(new XYChart.Data<>("Glucosa", 96));
        averagesSeries.getData().add(new XYChart.Data<>("IMC", 22.4));
        averagesSeries.getData().add(new XYChart.Data<>("FC", 72));
        averagesChart.getData().add(averagesSeries);
    }

    private void updateCriticalAlert(double systolic, double diastolic, double glucose, double heartRate) {
        if (systolic >= 140 || diastolic >= 90 || glucose >= 180 || heartRate >= 110) {
            setAlertStyle("Alerta: se detectaron valores criticos. Contacta a tu medico.", "alert-critical");
            return;
        }

        if (glucose >= 126 || systolic >= 130 || diastolic >= 85) {
            setAlertStyle("Atencion: valores en rango de riesgo. Monitorea tus metricas.", "alert-warning");
            return;
        }

        setAlertStyle("Sin alertas criticas en este momento", "alert-ok");
    }

    private void setAlertStyle(String message, String styleClass) {
        trendStatusLabel.setText(message);
        trendStatusLabel.getStyleClass().removeAll("alert-ok", "alert-warning", "alert-critical");
        trendStatusLabel.getStyleClass().add(styleClass);
    }

    // Registro de medición ahora se realiza desde el modal `quick-record-modal.fxml`.
}

