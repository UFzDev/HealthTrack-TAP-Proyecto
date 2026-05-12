package ufzdev.HealthTrack.controllers.admin;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.models.UserRole;
import ufzdev.HealthTrack.services.UserService;
import ufzdev.HealthTrack.util.AlertsUtil;
import ufzdev.HealthTrack.util.TaskExecutorUtil;
import ufzdev.HealthTrack.util.ValidationException;
import ufzdev.HealthTrack.validators.UserValidator;
import ufzdev.HealthTrack.services.LogService;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminUserModalController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<UserRole> roleComboBox;
    @FXML
    private Button btnSave;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.setItems(FXCollections.observableArrayList(UserRole.values()));
        roleComboBox.setValue(UserRole.MEDICO); // Por defecto sugiere Médico para Admin
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText();
        String user = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        UserRole selectedRole = roleComboBox.getValue();

        if (name.isBlank() || user.isBlank() || email.isBlank() || password.isBlank() || selectedRole == null) {
            AlertsUtil.showError("Campos incompletos", "Por favor completa todos los campos.");
            return;
        }

        UserModel newUser = new UserModel();
        newUser.setName(name);
        newUser.setUsername(user);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(selectedRole);

        try {
            UserValidator.validateForRegistration(newUser);
        } catch (ValidationException ve) {
            AlertsUtil.showError("Datos inválidos", ve.getMessage());
            return;
        }

        btnSave.setDisable(true);

        TaskExecutorUtil.execute(
                () -> {
                    UserService.registerUser(newUser);
                    return newUser;
                },
                res -> {
                    AlertsUtil.showSuccess("Éxito", "Usuario " + selectedRole + " creado correctamente.");
                    LogService.log("Creación de Usuario", "Se ha creado el usuario " + newUser.getUsername() + " con rol " + selectedRole);
                    closeModal();
                },
                err -> {
                    AlertsUtil.showError("Error", "No se pudo crear el usuario: " + err.getMessage());
                    btnSave.setDisable(false);
                }
        );
    }

    @FXML
    private void handleCancel() {
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }
}
