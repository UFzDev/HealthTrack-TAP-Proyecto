package ufzdev.HealthTrack.controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.chart.PieChart;
import ufzdev.HealthTrack.util.AlertsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoctorDashboardController {
    @javafx.fxml.FXML
    private TableView<Map<String, String>> alertsTable;
    @javafx.fxml.FXML
    private TableColumn<Map<String, String>, String> alertPatientColumn;
    @javafx.fxml.FXML
    private TableColumn<Map<String, String>, String> alertReadingColumn;
    @javafx.fxml.FXML
    private TableColumn<Map<String, String>, String> alertRiskColumn;
    @javafx.fxml.FXML
    private TableColumn<Map<String, String>, String> alertTimeColumn;
    @javafx.fxml.FXML
    private TableView<Map<String, String>> patientsTable;
    @javafx.fxml.FXML
    private TableColumn<Map<String, String>, String> patientNameColumn;
    @javafx.fxml.FXML
    private TableColumn<Map<String, String>, String> patientConditionColumn;
    @javafx.fxml.FXML
    private TableColumn<Map<String, String>, String> patientLastMetricColumn;
    @javafx.fxml.FXML
    private TableColumn<Map<String, String>, String> patientTrendColumn;
    @javafx.fxml.FXML
    private PieChart statusPie;
    @javafx.fxml.FXML
    private javafx.scene.chart.LineChart<Integer, Number> doctorTrendChart;
    @javafx.fxml.FXML
    private ListView<String> recommendationsList;
    @javafx.fxml.FXML
    private Label activePatientsLabel;
    @javafx.fxml.FXML
    private Label criticalAlertsLabel;
    @javafx.fxml.FXML
    private Label pendingReviewLabel;

    @javafx.fxml.FXML
    private void initialize() {
        bindTables();
        loadMockData();
    }

    @javafx.fxml.FXML
    private void handleExportSummary() {
        AlertsUtil.showSuccess("Reporte", "Se preparo el resumen clinico para exportacion PDF.");
    }

    private void bindTables() {
        alertPatientColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOrDefault("patient", "-")));
        alertReadingColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOrDefault("reading", "-")));
        alertRiskColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOrDefault("risk", "-")));
        alertTimeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOrDefault("time", "-")));

        patientNameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOrDefault("name", "-")));
        patientConditionColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOrDefault("condition", "-")));
        patientLastMetricColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOrDefault("last", "-")));
        patientTrendColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOrDefault("trend", "-")));
    }

    private void loadMockData() {
        activePatientsLabel.setText("18");
        criticalAlertsLabel.setText("3");
        pendingReviewLabel.setText("6");

        ArrayList<Map<String, String>> alertsSeed = new ArrayList<Map<String, String>>();
        alertsSeed.add(row("patient", "Ana Perez", "reading", "PA 152/98 - FC 112", "risk", "Alto", "time", "Hace 5 min"));
        alertsSeed.add(row("patient", "Luis Gomez", "reading", "Glucosa 210 mg/dL", "risk", "Alto", "time", "Hace 12 min"));
        alertsSeed.add(row("patient", "Elena Ruiz", "reading", "PA 140/90", "risk", "Moderado", "time", "Hace 20 min"));
        javafx.collections.ObservableList<Map<String, String>> alerts = javafx.collections.FXCollections.observableArrayList(alertsSeed);
        alertsTable.setItems(alerts);

        ArrayList<Map<String, String>> patientsSeed = new ArrayList<Map<String, String>>();
        patientsSeed.add(row("name", "Ana Perez", "condition", "Hipertension", "last", "152/98 - 112 bpm", "trend", "Subiendo"));
        patientsSeed.add(row("name", "Luis Gomez", "condition", "Diabetes tipo 2", "last", "210 mg/dL", "trend", "Inestable"));
        patientsSeed.add(row("name", "Marta Diaz", "condition", "Obesidad", "last", "IMC 30.2", "trend", "Bajando"));
        patientsSeed.add(row("name", "Carlos Vega", "condition", "Riesgo mixto", "last", "Glucosa 126 mg/dL", "trend", "Estable"));
        javafx.collections.ObservableList<Map<String, String>> patients = javafx.collections.FXCollections.observableArrayList(patientsSeed);
        patientsTable.setItems(patients);

        ArrayList<PieChart.Data> pieSeed = new ArrayList<PieChart.Data>();
        pieSeed.add(new PieChart.Data("Estable", 9));
        pieSeed.add(new PieChart.Data("En riesgo", 6));
        pieSeed.add(new PieChart.Data("Critico", 3));
        getStatusPie().setLegendVisible(true);
        getStatusPie().setLabelsVisible(true);
        javafx.collections.ObservableList<PieChart.Data> pieData = javafx.collections.FXCollections.observableArrayList(pieSeed);
        getStatusPie().setData(pieData);

        ArrayList<String> recommendationsSeed = new ArrayList<String>();
        recommendationsSeed.add("Priorizar seguimiento de pacientes con PA > 140/90.");
        recommendationsSeed.add("Reforzar plan alimenticio bajo en sodio y azucar.");
        recommendationsSeed.add("Sugerir actividad en casa por pronostico de lluvia.");
        recommendationsSeed.add("Validar medicacion en pacientes con tendencia ascendente.");
        javafx.collections.ObservableList<String> recommendations = javafx.collections.FXCollections.observableArrayList(recommendationsSeed);
        recommendationsList.setItems(recommendations);

        XYChart.Series<Integer, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(1, 132));
        series.getData().add(new XYChart.Data<>(2, 129));
        series.getData().add(new XYChart.Data<>(3, 134));
        series.getData().add(new XYChart.Data<>(4, 128));
        series.getData().add(new XYChart.Data<>(5, 126));
        series.getData().add(new XYChart.Data<>(6, 124));
        doctorTrendChart.getData().setAll(series);
    }

    private Map<String, String> row(String... pairs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            map.put(pairs[i], pairs[i + 1]);
        }
        return map;
    }

    private PieChart getStatusPie() {
        return statusPie;
    }
}

