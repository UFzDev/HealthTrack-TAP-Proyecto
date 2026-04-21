package ufzdev.todo_list.controllers;

import java.awt.Desktop;
import java.net.URI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.services.UserService;

import ufzdev.todo_list.util.AlertsUtil;
import ufzdev.todo_list.util.NavigationUtil;
import ufzdev.todo_list.util.TaskExecutorUtil;
import ufzdev.todo_list.util.UserSessionUtil;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnGit;
    @FXML
    private Button btnTest;

    @FXML
    public void handleLogin() {
        UserModel userModel = new UserModel();
        userModel.setUsername(usernameField.getText());
        userModel.setPassword(passwordField.getText());
        btnLogin.setDisable(true);
        TaskExecutorUtil.execute(
                () -> {
                    UserModel authenticated = UserService.autenticate(userModel);
                    if (authenticated != null) {
                        UserSessionUtil.getInstance().loadSessionData(authenticated);
                    }
                    return authenticated;
                },
                userModelAuthenticated -> {
                    if (userModelAuthenticated == null) {
                        AlertsUtil.showError("Error durante la autenticación",
                                "No se pudo autenticar. Verifique sus credenciales.");
                        btnLogin.setDisable(false);
                        return;
                    }
                    AlertsUtil.showSuccess("Login Exitoso",
                            "Bienvenido, " + userModelAuthenticated.getUsername() + "!");
                    System.out.println("Login exitoso para el usuario: " + userModelAuthenticated.getUsername());
                    btnLogin.setDisable(false);

                    Stage stage = (Stage) btnLogin.getScene().getWindow();
                    afterLogin(stage, userModelAuthenticated);
                },
                error -> {
                    AlertsUtil.showError("Error durante la autenticación", "Error: " + error.getMessage());
                    System.out.println("Error durante la autenticación: " + error.getMessage());
                    btnLogin.setDisable(false);
                }
        );
    }

    @FXML
    public void handleLoginTest() {
        btnTest.setDisable(true);
        TaskExecutorUtil.execute(
                () -> {
                    UserModel testUser = UserService.loginTest();
                    if (testUser != null) {
                        testUser.setHasSettings(false);
                        UserSessionUtil.getInstance().loadSessionData(testUser);
                    }
                    return testUser;
                },
                userAuthenticated -> {
                    if (userAuthenticated == null) {
                        AlertsUtil.showError("Error en el inicio de sesión de prueba", "No se pudo autenticar con el usuario de prueba.");
                        btnTest.setDisable(false);
                        return;
                    }
                    AlertsUtil.showSuccess("Inicio de sesión de prueba exitoso", "Se ha autenticado correctamente con el usuario de prueba.");
                    System.out.println("Login exitoso con el usuario de prueba.");
                    btnTest.setDisable(false);

                    Stage stage = (Stage) btnTest.getScene().getWindow();
                    afterLogin(stage, userAuthenticated);
                },
                error -> {
                    AlertsUtil.showError("Error en el inicio de sesión de prueba",
                            "No se pudo autenticar con el usuario de prueba. Error: " + error.getMessage());
                    System.out.println("Error durante el inicio de sesión de prueba: " + error.getMessage());
                    btnTest.setDisable(false);
                }
        );
    }

    private void afterLogin(Stage stage, UserModel user) {
        if (user == null) {
            NavigationUtil.goToLogin(stage);
            return;
        }

        NavigationUtil.goToTasks(stage);

        if (!user.isHasSettings()) {
            NavigationUtil.goToSettings();
        }
    }

    @FXML
    private void handleOpenRegister() {
        NavigationUtil.goToRegister();
    }


    @FXML
    public void handleOpenGithub() {
        btnGit.setDisable(true);
        String githubUrl = "https://github.com/UFzDev/ToDo-List-con-JavaFX";
        TaskExecutorUtil.execute(
                () -> {
                     if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI(githubUrl));
                        return true;
                    } else {
                        return false;
                    }
                },
                success -> {
                    if (success) {
                        AlertsUtil.showSuccess("GitHub abierto", "Se ha abierto el repositorio de GitHub en tu navegador.");
                        System.out.println("GitHub abierto correctamente. URL: " + githubUrl);
                    } else {
                        AlertsUtil.showError("Navegador no soportado", "Tu sistema no soporta abrir el navegador automáticamente. Por favor, visita: " + githubUrl);
                        System.out.println("Navegador no soportado para abrir GitHub. URL: " + githubUrl);
                    }
                    btnGit.setDisable(false);
                },
                error -> {
                    AlertsUtil.showError("Error al abrir GitHub", "No se pudo abrir GitHub. Inténtalo de nuevo.");
                    System.out.println("Error al abrir GitHub: " + error.getMessage());
                    btnGit.setDisable(false);
                }
        );

    }
}
