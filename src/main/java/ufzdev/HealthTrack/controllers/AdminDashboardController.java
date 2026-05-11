package ufzdev.HealthTrack.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import ufzdev.HealthTrack.util.AlertsUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {
    @FXML
    private Label usersCount;
    @FXML
    private Label doctorsCount;
    @FXML
    private Label reportsCount;
    @FXML
    private Label syncStatusValue;
    @FXML
    private Label apiStatusValue;
    @FXML
    private ListView<String> activityLogs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usersCount.setText("142");
        doctorsCount.setText("18");
        reportsCount.setText("27");

        syncStatusValue.setText("Conectado");
        apiStatusValue.setText("Parcial");

        activityLogs.setItems(FXCollections.observableArrayList(
                "09:45 - Se registro nuevo paciente con medico asignado.",
                "09:30 - Se genero historial clinico en PDF.",
                "09:15 - Sincronizacion Firebase completada.",
                "08:58 - API de clima sin respuesta (reintento programado).",
                "08:40 - Exportacion de metricas a Excel completada."
        ));
    }

    @FXML
    private void handleExportPdf() {
        AlertsUtil.showSuccess("Reporte", "Exportacion PDF iniciada desde panel admin.");
    }

    @FXML
    private void handleExportExcel() {
        AlertsUtil.showSuccess("Reporte", "Exportacion Excel iniciada desde panel admin.");
    }

    @FXML
    private void handleCheckSync() {
        AlertsUtil.showSuccess("Sincronizacion", "Firebase operativo. Ultima comprobacion correcta.");
    }
}

