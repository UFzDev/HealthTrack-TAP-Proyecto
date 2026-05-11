package ufzdev.HealthTrack.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.models.UserRole;
import ufzdev.HealthTrack.services.UserService;
import ufzdev.HealthTrack.util.AlertsUtil;
import ufzdev.HealthTrack.util.NavigationUtil;
import ufzdev.HealthTrack.util.TaskExecutorUtil;
import ufzdev.HealthTrack.util.ValidationException;
import ufzdev.HealthTrack.validators.UserValidator;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private HBox rootPane;
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
    private Button btnRegister;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cargar los roles en el ComboBox
        roleComboBox.setItems(FXCollections.observableArrayList(UserRole.values()));
        roleComboBox.setValue(UserRole.PACIENTE); // Por defecto selecciona PACIENTE
    }

    @FXML
    public void handleRegister() {
        String name = nameField.getText();
        String user = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        UserRole selectedRole = roleComboBox.getValue();

        // Validar que todos los campos estén llenos
        if (name.isBlank() || user.isBlank() || email.isBlank() || password.isBlank()) {
            AlertsUtil.showError("Campos incompletos", "Por favor completa todos los campos.");
            return;
        }

        if (selectedRole == null) {
            AlertsUtil.showError("Rol no seleccionado", "Por favor selecciona un rol.");
            return;
        }

        UserModel newUserModel = new UserModel();
        newUserModel.setName(name);
        newUserModel.setUsername(user);
        newUserModel.setEmail(email);
        newUserModel.setPassword(password);
        newUserModel.setRole(selectedRole);

        // Validar datos antes de llamar al servicio
        try {
            UserValidator.validateForRegistration(newUserModel);
        } catch (ValidationException ve) {
            AlertsUtil.showError("Registro inválido", ve.getMessage());
            return;
        }

        btnRegister.setDisable(true);

        TaskExecutorUtil.execute(
                () -> {
                    UserService.registerUser(newUserModel);
                    return newUserModel;
                },
                ignored -> {
                    AlertsUtil.showSuccess("Registro exitoso", "Bienvenido a HealthTrack, " + newUserModel.getName() + "!");
                    System.out.println("Registro exitoso en Firebase para: " + newUserModel.getName() + " con rol: " + selectedRole);
                    btnRegister.setDisable(false);
                    handleCancel();
                },
                error -> {
                    AlertsUtil.showError("Error durante el registro", "Error: " + error.getMessage());
                    System.out.println("Error durante el registro: " + error.getMessage());
                    btnRegister.setDisable(false);
                }
        );
    }

    @FXML
    public void handleCancel() {
        NavigationUtil.closeModal((Stage) rootPane.getScene().getWindow());
    }
}
