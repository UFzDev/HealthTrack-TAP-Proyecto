package ufzdev.HealthTrack.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.util.AlertsUtil;
import ufzdev.HealthTrack.util.UserSessionUtil;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        if (UserSessionUtil.getInstance().getUser() == null) {
            AlertsUtil.showError("Sesion", "No hay sesion activa para registrar la medicion.");
            return;
        }

        if (hasInvalidNumber(bpSystolicInput, true)
                || hasInvalidNumber(bpDiastolicInput, true)
                || hasInvalidNumber(glucoseInput, true)
                || hasInvalidNumber(heartRateInput, true)
                || hasInvalidNumber(weightInput, true)
                || hasInvalidNumber(heightInput, true)) {
            AlertsUtil.showError("Formulario invalido", "Revisa los campos. Todos deben contener numeros validos.");
            return;
        }

        double weight = Double.parseDouble(weightInput.getText().trim());
        double height = Double.parseDouble(heightInput.getText().trim());

        if (height <= 0) {
            AlertsUtil.showError("Altura invalida", "La altura debe ser mayor que cero.");
            return;
        }

        double imc = weight / (height * height);

        UserModel currentUser = UserSessionUtil.getInstance().getUser();
        currentUser.setImc(imc);

        AlertsUtil.showSuccess("Registro", "Medicion capturada correctamente.");
        closeModal();
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


