package ufzdev.HealthTrack.controllers.admin;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import ufzdev.HealthTrack.dao.*;
import ufzdev.HealthTrack.models.*;
import javafx.scene.control.*;
import ufzdev.HealthTrack.util.AlertsUtil;
import ufzdev.HealthTrack.util.TaskExecutorUtil;
import ufzdev.HealthTrack.util.reports.ReportFactory;
import ufzdev.HealthTrack.util.reports.ReportGenerator;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminReportsController implements Initializable {

    @FXML
    private CheckBox chkUsers;
    @FXML
    private CheckBox chkDoctors;
    @FXML
    private CheckBox chkPatients;
    @FXML
    private CheckBox chkLogs;
    @FXML
    private CheckBox chkMetrics;
    @FXML
    private CheckBox chkConfig;
    @FXML
    private ComboBox<String> formatSelector;
    @FXML
    private Button btnGenerate;
    @FXML
    private Label statusLabel;

    private UserDao userDao;
    private SystemLogDao logDao;
    private HealthMetricDao metricDao;
    private SystemConfigDao configDao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDao = new UserFirestoreDao();
        logDao = new SystemLogFirestoreDao();
        metricDao = new HealthMetricFirestoreDao();
        configDao = new SystemConfigFirestoreDao();

        // Eliminado selector de tipo único

        formatSelector.setItems(FXCollections.observableArrayList("PDF", "Excel"));
        formatSelector.setValue("PDF");

        statusLabel.setText("");
    }

    @FXML
    private void handleGenerateReport() {
        String format = formatSelector.getValue();
        
        btnGenerate.setDisable(true);
        statusLabel.setText("Generando respaldo/reporte...");

        TaskExecutorUtil.execute(
            () -> {
                List<ReportGenerator.ReportSection> sections = new ArrayList<>();
                String fileName = "Backup_HealthTrack_" + System.currentTimeMillis();

                // 1. Usuarios
                if (chkUsers.isSelected()) {
                    List<String[]> rows = new ArrayList<>();
                    List<UserModel> users = userDao.listAll();
                    for (UserModel u : users) {
                        rows.add(new String[]{
                            u.getName() != null ? u.getName() : "N/A",
                            u.getUsername(),
                            u.getEmail() != null ? u.getEmail() : "N/A",
                            u.getRole() != null ? u.getRole().name() : "PACIENTE"
                        });
                    }
                    sections.add(new ReportGenerator.ReportSection("Usuarios", new String[]{"Nombre", "Usuario", "Email", "Rol"}, rows));
                }

                // 2. Médicos
                if (chkDoctors.isSelected()) {
                    List<String[]> rows = new ArrayList<>();
                    List<UserModel> users = userDao.listByRole(UserRole.MEDICO);
                    for (UserModel u : users) {
                        rows.add(new String[]{
                            u.getName() != null ? u.getName() : "N/A",
                            u.getUsername(),
                            u.getEmail() != null ? u.getEmail() : "N/A"
                        });
                    }
                    sections.add(new ReportGenerator.ReportSection("Médicos", new String[]{"Nombre", "Usuario", "Email"}, rows));
                }

                // 3. Pacientes
                if (chkPatients.isSelected()) {
                    List<String[]> rows = new ArrayList<>();
                    List<UserModel> users = userDao.listByRole(UserRole.PACIENTE);
                    for (UserModel u : users) {
                        rows.add(new String[]{
                            u.getName() != null ? u.getName() : "N/A",
                            u.getUsername(),
                            u.getEmail() != null ? u.getEmail() : "N/A"
                        });
                    }
                    sections.add(new ReportGenerator.ReportSection("Pacientes", new String[]{"Nombre", "Usuario", "Email"}, rows));
                }

                // 4. Logs
                if (chkLogs.isSelected()) {
                    List<String[]> rows = new ArrayList<>();
                    List<SystemLogModel> logs = logDao.listAll();
                    for (SystemLogModel l : logs) {
                        rows.add(new String[]{
                            l.getFormattedTimestamp(), 
                            l.getUser() != null ? l.getUser() : "Sistema", 
                            l.getAction(), 
                            l.getDetails() != null ? l.getDetails() : ""
                        });
                    }
                    sections.add(new ReportGenerator.ReportSection("Logs de Auditoria", new String[]{"Fecha", "Usuario", "Acción", "Detalles"}, rows));
                }

                // 5. Métricas de Salud
                if (chkMetrics.isSelected()) {
                    List<String[]> rows = new ArrayList<>();
                    List<HealthMetricModel> metrics = metricDao.listAll();
                    for (HealthMetricModel m : metrics) {
                        rows.add(new String[]{
                            m.getRecordedAt() != null ? m.getRecordedAt().toString() : "N/A", 
                            m.getUserId() != null ? m.getUserId() : "N/A", 
                            m.getMetricType() != null ? m.getMetricType() : "N/A", 
                            String.valueOf(m.getValue())
                        });
                    }
                    sections.add(new ReportGenerator.ReportSection("Metricas de Salud", new String[]{"Fecha", "ID Usuario", "Tipo", "Valor"}, rows));
                }

                // 6. Configuración
                if (chkConfig.isSelected()) {
                    List<String[]> rows = new ArrayList<>();
                    SystemConfigModel config = configDao.get();
                    if (config != null) {
                        rows.add(new String[]{"Proveedor IA", config.getAiProvider() != null ? config.getAiProvider() : "N/A"});
                        rows.add(new String[]{"API Key IA", config.getAiApiKey() != null ? "CONFIGURADA (****)" : "No configurada"});
                        rows.add(new String[]{"Versión Sistema", config.getSystemVersion() != null ? config.getSystemVersion() : "N/A"});
                        rows.add(new String[]{"Modo Mantenimiento", config.isMaintenanceMode() ? "ACTIVADO" : "Desactivado"});
                        rows.add(new String[]{"Email Soporte", config.getSupportEmail() != null ? config.getSupportEmail() : "N/A"});
                    }
                    sections.add(new ReportGenerator.ReportSection("Configuracion", new String[]{"Parámetro", "Valor"}, rows));
                }

                if (sections.isEmpty()) {
                    throw new Exception("Debes seleccionar al menos una entidad para exportar.");
                }

                ReportFactory.ReportType type = format.equals("PDF") ? ReportFactory.ReportType.PDF : ReportFactory.ReportType.EXCEL;
                ReportGenerator generator = ReportFactory.getGenerator(type);
                
                return generator.generateMultiSection("Respaldo del Sistema HealthTrack", sections, fileName);
            },
            path -> {
                statusLabel.setText("Reporte generado: " + path.getFileName());
                btnGenerate.setDisable(false);
                AlertsUtil.showSuccess("Éxito", "Reporte generado correctamente.\nUbicación: " + path.toString());
                
                // Intentar abrir la carpeta
                try {
                    Desktop.getDesktop().open(path.getParent().toFile());
                } catch (Exception e) {
                    System.err.println("No se pudo abrir la carpeta: " + e.getMessage());
                }
            },
            error -> {
                statusLabel.setText("Error al generar reporte.");
                btnGenerate.setDisable(false);
                AlertsUtil.showError("Error", "No se pudo generar el reporte: " + error.getMessage());
            }
        );
    }
}
