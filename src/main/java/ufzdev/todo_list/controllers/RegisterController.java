package ufzdev.todo_list.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.services.UserService;
import ufzdev.todo_list.util.AlertsUtil;
import ufzdev.todo_list.util.NavigationUtil;
import ufzdev.todo_list.util.TaskExecutorUtil;

public class RegisterController {

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
    private Button btnRegister;

    @FXML
    public void handleRegister() {
        String name = nameField.getText();
        String user = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        btnRegister.setDisable(true);

        UserModel newUserModel = new UserModel();
        newUserModel.setName(name);
        newUserModel.setUsername(user);
        newUserModel.setEmail(email);
        newUserModel.setPassword(password);

        TaskExecutorUtil.execute(
                () -> {
                    UserService.registerUser(newUserModel);
                    return newUserModel;
                },
                resultUserModel -> {
                    AlertsUtil.showSuccess("Registro exitoso", "Bienvenido a ToDo List, " + newUserModel.getName() + "!");
                    System.out.println("Registro exitoso en Firebase para: " + newUserModel.getName());
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
