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
    @FXML
    private Button btnDelete;

    private static UserModel userToEditStatic;
    private UserModel userToEdit;

    public static void setUserToEdit(UserModel user) {
        userToEditStatic = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.setItems(FXCollections.observableArrayList(UserRole.values()));
        roleComboBox.setValue(UserRole.MEDICO); 
        
        // Cargar usuario si se pasó estáticamente
        if (userToEditStatic != null) {
            setUser(userToEditStatic);
            userToEditStatic = null; // Limpiar para la próxima vez
        }
    }

    public void setUser(UserModel user) {
        this.userToEdit = user;
        if (user != null) {
            nameField.setText(user.getName());
            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());
            roleComboBox.setValue(user.getRole());
            passwordField.setText(user.getPassword()); 
            
            btnSave.setText("Actualizar Datos");
            btnDelete.setVisible(true);
            btnDelete.setManaged(true);
        }
    }

    @FXML
    private void handleDelete() {
        if (userToEdit == null) return;

        boolean confirm = AlertsUtil.showConfirmation("Eliminar Usuario", 
            "¿Estás seguro de que deseas eliminar permanentemente a " + userToEdit.getName() + "? Esta acción no se puede deshacer.");
        
        if (confirm) {
            btnDelete.setDisable(true);
            TaskExecutorUtil.execute(
                () -> {
                    UserService.deleteUser(userToEdit.getId());
                    return null;
                },
                res -> {
                    AlertsUtil.showSuccess("Eliminado", "Usuario eliminado correctamente.");
                    LogService.log("Eliminación de Usuario", "Se ha eliminado el usuario " + userToEdit.getUsername());
                    closeModal();
                },
                err -> {
                    AlertsUtil.showError("Error", "No se pudo eliminar: " + err.getMessage());
                    btnDelete.setDisable(false);
                }
            );
        }
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText();
        String user = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        UserRole selectedRole = roleComboBox.getValue();

        if (name.isBlank() || user.isBlank() || email.isBlank() || selectedRole == null) {
            AlertsUtil.showError("Campos incompletos", "Por favor completa todos los campos.");
            return;
        }

        UserModel userToSave = (userToEdit != null) ? userToEdit : new UserModel();
        userToSave.setName(name);
        userToSave.setUsername(user);
        userToSave.setEmail(email);
        userToSave.setPassword(password);
        userToSave.setRole(selectedRole);

        btnSave.setDisable(true);

        TaskExecutorUtil.execute(
                () -> {
                    if (userToEdit == null) {
                        UserValidator.validateForRegistration(userToSave);
                        UserService.registerUser(userToSave);
                    } else {
                        UserService.updateUser(userToSave);
                    }
                    return userToSave;
                },
                res -> {
                    String action = (userToEdit == null) ? "creado" : "actualizado";
                    AlertsUtil.showSuccess("Éxito", "Usuario " + action + " correctamente.");
                    LogService.log("Gestión de Usuario", "Se ha " + action + " el usuario " + userToSave.getUsername());
                    closeModal();
                },
                err -> {
                    AlertsUtil.showError("Error", "No se pudo procesar el usuario: " + err.getMessage());
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
