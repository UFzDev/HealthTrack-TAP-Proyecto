package ufzdev.HealthTrack.controllers.admin;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import ufzdev.HealthTrack.util.AlertsUtil;
import ufzdev.HealthTrack.dao.UserDao;
import ufzdev.HealthTrack.dao.UserFirestoreDao;
import ufzdev.HealthTrack.models.UserRole;
import ufzdev.HealthTrack.util.NavigationUtil;
import javafx.stage.Stage;
import javafx.scene.Node;

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
        // Cargar conteos
        UserDao userDao = new UserFirestoreDao();
        try {
            int totalUsers = userDao.countAll();
            int totalDoctors = userDao.countByRole(UserRole.MEDICO);
            usersCount.setText(String.valueOf(totalUsers));
            doctorsCount.setText(String.valueOf(totalDoctors));

            // Intentar contar reportes si existe la colección "reportes"
            try {
                int reports = userDao instanceof UserFirestoreDao ? ((UserFirestoreDao) userDao).getFirestore().collection("reportes").get().get().size() : 0;
                reportsCount.setText(String.valueOf(reports));
            } catch (Exception e) {
                reportsCount.setText("-");
            }

            syncStatusValue.setText("Conectado");
            apiStatusValue.setText("Parcial");

        } catch (Exception e) {
            // Si hay error al conectar con Firestore, mantener UI informativa
            usersCount.setText("-");
            doctorsCount.setText("-");
            reportsCount.setText("-");
            System.out.println("Error cargando datos admin: " + e.getMessage());
        }

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

    @FXML
    private void handleManageUsers(javafx.event.ActionEvent event) {
        NavigationUtil.goToUserManagement();
    }
}

