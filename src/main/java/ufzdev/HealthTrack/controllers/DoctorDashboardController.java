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
import java.util.List;
import ufzdev.HealthTrack.dao.UserDao;
import ufzdev.HealthTrack.dao.UserFirestoreDao;
import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.util.UserSessionUtil;
import ufzdev.HealthTrack.models.UserRole;

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
        loadFromDatabase();
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

    private void loadFromDatabase() {
        UserDao userDao = new UserFirestoreDao();
        UserModel current = UserSessionUtil.getInstance().getUser();

        try {
            List<UserModel> patients = new ArrayList<>();
            if (current != null && current.getRole() == UserRole.MEDICO) {
                patients = userDao.listPatientsByDoctor(current.getId());
            } else {
                // Si no es medico, mostrar todos los pacientes (por defecto una vista reducida)
                patients = userDao.listByRole(UserRole.PACIENTE);
            }

            activePatientsLabel.setText(String.valueOf(patients.size()));
            criticalAlertsLabel.setText("0");
            pendingReviewLabel.setText("0");

            ArrayList<Map<String, String>> patientsRows = new ArrayList<>();
            for (UserModel p : patients) {
                Map<String, String> r = new HashMap<>();
                r.put("name", p.getName() != null ? p.getName() : p.getUsername());
                r.put("condition", "-");
                r.put("last", "-");
                r.put("trend", "-");
                patientsRows.add(r);
            }
            javafx.collections.ObservableList<Map<String, String>> patientsObs = javafx.collections.FXCollections.observableArrayList(patientsRows);
            patientsTable.setItems(patientsObs);

            // Vaciar o establecer datos mínimos para alertas y charts; se pueden completar con datos reales si se modelan mediciones
            alertsTable.setItems(javafx.collections.FXCollections.observableArrayList(new ArrayList<>()));
            recommendationsList.setItems(javafx.collections.FXCollections.observableArrayList(new ArrayList<>()));
            getStatusPie().setData(javafx.collections.FXCollections.observableArrayList(new ArrayList<>()));
            doctorTrendChart.getData().clear();

        } catch (Exception e) {
            System.out.println("Error cargando datos del doctor: " + e.getMessage());
            AlertsUtil.showError("Error de datos", "No se pudieron cargar los pacientes desde la base de datos.");
        }
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

