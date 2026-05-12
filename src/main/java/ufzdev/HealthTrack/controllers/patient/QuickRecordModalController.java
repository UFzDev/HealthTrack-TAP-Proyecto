package ufzdev.HealthTrack.controllers.patient;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ufzdev.HealthTrack.dao.HealthMetricFirestoreDao;
import ufzdev.HealthTrack.models.HealthMetricModel;
import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.util.AlertsUtil;
import ufzdev.HealthTrack.util.UserSessionUtil;
import ufzdev.HealthTrack.util.ValidationException;
import ufzdev.HealthTrack.validators.HealthMetricValidator;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controlador del modal para registrar mediciones rápidas.
 * Guarda cada métrica en Firestore con el DAO HealthMetricFirestoreDao.
 */
public class QuickRecordModalController implements Initializable {

    @FXML
    private Label patientNameLabel;
    @FXML
    private Label recordedAtLabel;
    @FXML
    private Label imcPreviewLabel;

    @FXML
    private TextField bpSystolicInput;
    @FXML
    private TextField bpDiastolicInput;
    @FXML
    private TextField glucoseInput;
    @FXML
    private TextField heartRateInput;
    @FXML
    private TextField weightInput;
    @FXML
    private TextField heightInput;

    private HealthMetricFirestoreDao metricDao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        metricDao = new HealthMetricFirestoreDao();

        UserModel currentUser = UserSessionUtil.getInstance().getUser();
        if (currentUser != null) {
            String displayName = currentUser.getName() != null && !currentUser.getName().isBlank()
                    ? currentUser.getName()
                    : currentUser.getUsername();
            patientNameLabel.setText(displayName != null && !displayName.isBlank() ? displayName : "Usuario activo");
        } else {
            patientNameLabel.setText("Sin sesion activa");
        }

        recordedAtLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        weightInput.setOnKeyReleased(event -> refreshImcPreview());
        heightInput.setOnKeyReleased(event -> refreshImcPreview());
    }

    @FXML
    private void handleSave() {
        UserModel currentUser = UserSessionUtil.getInstance().getUser();
        if (currentUser == null || currentUser.getId() == null) {
            AlertsUtil.showError("Sesión", "No hay sesión activa para registrar la medición.");
            return;
        }

        // Validar plausibilidad de las mediciones para evitar datos inventados
        try {
            HealthMetricValidator.validateQuickRecord(bpSystolicInput, bpDiastolicInput, glucoseInput, heartRateInput, weightInput, heightInput);
        } catch (ValidationException ve) {
            AlertsUtil.showError("Entrada inválida", ve.getMessage());
            return;
        }

        try {
            LocalDateTime now = LocalDateTime.now();

            // Extraer valores
            double systolic = Double.parseDouble(bpSystolicInput.getText().trim());
            double diastolic = Double.parseDouble(bpDiastolicInput.getText().trim());
            double glucose = Double.parseDouble(glucoseInput.getText().trim());
            double heartRate = Double.parseDouble(heartRateInput.getText().trim());
            double weight = Double.parseDouble(weightInput.getText().trim());
            double height = Double.parseDouble(heightInput.getText().trim());

            // Guardar cada métrica en Firestore
            saveMetric(currentUser.getId(), "Presión", systolic, now);
            saveMetric(currentUser.getId(), "Glucosa", glucose, now);
            saveMetric(currentUser.getId(), "Frecuencia Cardíaca", heartRate, now);

            // Calcular y guardar IMC
            if (height > 0) {
                double imc = weight / (height * height);
                currentUser.setImc(imc);
                saveMetric(currentUser.getId(), "IMC", imc, now);
            }

            AlertsUtil.showSuccess("Registro", "Medición guardada correctamente en la base de datos.");
            closeModal();

        } catch (Exception e) {
            System.err.println("Error guardando métricas: " + e.getMessage());
            AlertsUtil.showError("Error", "No se pudo guardar la medición. Intenta de nuevo.");
        }
    }

    /**
     * Guarda una métrica en Firestore.
     */
    private void saveMetric(String userId, String metricType, double value, LocalDateTime recordedAt) throws Exception {
        HealthMetricModel metric = new HealthMetricModel(userId, metricType, value, recordedAt);
        metricDao.save(metric);
    }

    @FXML
    private void handleCancel() {
        closeModal();
    }

    private void refreshImcPreview() {
        if (hasInvalidNumber(weightInput, false) || hasInvalidNumber(heightInput, false)) {
            imcPreviewLabel.setText("--");
            return;
        }

        double weight = Double.parseDouble(weightInput.getText().trim());
        double height = Double.parseDouble(heightInput.getText().trim());

        if (height <= 0) {
            imcPreviewLabel.setText("--");
            return;
        }

        double imc = weight / (height * height);
        imcPreviewLabel.setText(String.format("%.1f", imc));
    }

    private boolean hasInvalidNumber(TextField field, boolean required) {
        if (field == null || field.getText() == null || field.getText().trim().isEmpty()) {
            return required;
        }

        try {
            Double.parseDouble(field.getText().trim());
            return false;
        } catch (NumberFormatException ex) {
            return true;
        }
    }

    private void closeModal() {
        Stage stage = (Stage) bpSystolicInput.getScene().getWindow();
        stage.close();
    }
}
