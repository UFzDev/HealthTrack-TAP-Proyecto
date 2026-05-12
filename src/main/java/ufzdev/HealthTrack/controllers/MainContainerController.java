package ufzdev.HealthTrack.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.models.UserRole;
import ufzdev.HealthTrack.util.UserSessionUtil;
import ufzdev.HealthTrack.util.NavigationUtil;
import ufzdev.HealthTrack.util.AlertsUtil;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainContainerController implements Initializable {

    @FXML
    private VBox sidebar;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label userRoleLabel;
    @FXML
    private AnchorPane contentArea;
    @FXML
    private Button btnHome, btnUserMgmt, btnReports, btnSystemStatus, btnProfile, btnSettings;
    @FXML
    private Button fabRecord;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserModel currentUser = UserSessionUtil.getInstance().getUser();
        if (currentUser != null) {
            String userName = currentUser.getName() != null && !currentUser.getName().isBlank()
                    ? currentUser.getName()
                    : currentUser.getUsername();
            welcomeLabel.setText("Bienvenido, " + userName);

            String roleLabel = currentUser.getRole() != null
                    ? "• " + getRoleLabel(currentUser.getRole())
                    : "• Usuario";
            userRoleLabel.setText(roleLabel);
        } else {
            welcomeLabel.setText("Bienvenido");
            userRoleLabel.setText("• Usuario");
        }
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

        // Load role-specific content
        UserRole role = currentUser != null ? currentUser.getRole() : null;
        setupSidebarByRole(role);
        
        try {
            if (role == UserRole.MEDICO) {
                loadContent("doctor/doctor-dashboard.fxml");
                fabRecord.setVisible(false);
            } else if (role == UserRole.ADMINISTRADOR) {
                loadContent("admin/admin-dashboard.fxml");
                fabRecord.setVisible(false);
            } else {
                loadContent("patient/patient-dashboard.fxml");
                fabRecord.setVisible(true);
            }
        } catch (Exception e) {
            AlertsUtil.showError("Error de carga", "No se pudo cargar el panel inicial: " + e.getMessage());
        }
    }

    private void setupSidebarByRole(UserRole role) {
        // Reset all to visible or hidden as needed
        boolean isAdmin = (role == UserRole.ADMINISTRADOR);
        
        btnUserMgmt.setVisible(isAdmin);
        btnUserMgmt.setManaged(isAdmin);
        
        btnReports.setVisible(isAdmin);
        btnReports.setManaged(isAdmin);
        
        btnSystemStatus.setVisible(isAdmin);
        btnSystemStatus.setManaged(isAdmin);
        
        // Profile is mostly for patients/doctors
        btnProfile.setVisible(role != UserRole.ADMINISTRADOR);
        btnProfile.setManaged(role != UserRole.ADMINISTRADOR);
    }

    private void loadContent(String fxml) throws IOException {
        contentArea.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ufzdev/HealthTrack/view/" + fxml));
        Node node = loader.load();
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        contentArea.getChildren().add(node);
    }

    @FXML
    private void openHome() {
        // reload main content
        initialize(null, null);
        updateActiveButton(btnHome);
    }

    @FXML
    private void openProfile() {
        // placeholder for profile view
        try {
            loadContent("patient/patient-dashboard.fxml");
            fabRecord.setVisible(true);
            updateActiveButton(btnProfile);
        } catch (IOException e) {
            AlertsUtil.showError("Error", "No se pudo abrir Perfil.");
        }
    }

    @FXML
    private void openUserManagement() {
        try {
            loadContent("admin/admin-user-management.fxml");
            updateActiveButton(btnUserMgmt);
        } catch (IOException e) {
            AlertsUtil.showError("Error", "No se pudo abrir Gestión de Usuarios.");
        }
    }

    @FXML
    private void openReports() {
        try {
            loadContent("admin/admin-reports.fxml");
            updateActiveButton(btnReports);
        } catch (IOException e) {
            AlertsUtil.showError("Error", "No se pudo abrir Reportes.");
        }
    }

    @FXML
    private void openSystemStatus() {
        try {
            loadContent("admin/admin-system-status.fxml");
            updateActiveButton(btnSystemStatus);
        } catch (IOException e) {
            AlertsUtil.showError("Error", "No se pudo abrir Estado del Sistema.");
        }
    }

    @FXML
    private void openSettings() {
        // NavigationUtil.goToSettings();
        updateActiveButton(btnSettings);
    }

    private void updateActiveButton(Button activeBtn) {
        // Clear active class from all
        btnHome.getStyleClass().remove("nav-active");
        btnUserMgmt.getStyleClass().remove("nav-active");
        btnReports.getStyleClass().remove("nav-active");
        btnSystemStatus.getStyleClass().remove("nav-active");
        btnProfile.getStyleClass().remove("nav-active");
        btnSettings.getStyleClass().remove("nav-active");
        
        // Add to active
        if (!activeBtn.getStyleClass().contains("nav-active")) {
            activeBtn.getStyleClass().add("nav-active");
        }
    }

    @FXML
    private void handleLogout() {
        UserSessionUtil.getInstance().cleanSession();
        NavigationUtil.goToLogin((javafx.stage.Stage) sidebar.getScene().getWindow());
    }

    @FXML
    private void handleQuickRecord() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ufzdev/HealthTrack/view/patient/quick-record-modal.fxml"));
            Parent root = loader.load();

            Stage owner = (Stage) sidebar.getScene().getWindow();
            Stage modal = new Stage();
            modal.initOwner(owner);
            modal.initModality(Modality.WINDOW_MODAL);
            modal.setTitle("Registrar medicion");
            modal.setScene(new Scene(root, 700, 540));
            modal.setResizable(false);
            modal.centerOnScreen();
            modal.showAndWait();

            // Después de cerrar el modal, recargar el panel del paciente para reflejar
            // cambios en sesión
            try {
                UserModel currentUser = UserSessionUtil.getInstance().getUser();
                if (currentUser != null && currentUser.getRole() == UserRole.PACIENTE) {
                    loadContent("patient/patient-dashboard.fxml");
                }
            } catch (Exception ex) {
                // No bloquear la UX por fallo al recargar
            }
        } catch (IOException e) {
            AlertsUtil.showError("Error", "No se pudo abrir el formulario de medicion.");
        }
    }

    private String getRoleLabel(UserRole role) {
        return switch (role) {
            case PACIENTE -> "Paciente";
            case MEDICO -> "Médico";
            case ADMINISTRADOR -> "Administrador";
            default -> "Usuario";
        };
    }
}
