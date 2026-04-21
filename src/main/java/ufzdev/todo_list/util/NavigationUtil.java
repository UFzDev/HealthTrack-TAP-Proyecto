package ufzdev.todo_list.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class NavigationUtil {

    // Ruta base
    private static final String BASE_PATH = "/ufzdev/todo_list/view/";

    private static void renderView(Stage stage, String fxmlFile, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(BASE_PATH + fxmlFile));
            Parent root = loader.load();

            Scene scene = new Scene(root, width, height);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
            //stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la vista " + fxmlFile + ": " + e.getMessage());
            AlertsUtil.showError("Error de Sistema", "No se pudo abrir la ventana solicitada.");
        }
    }

    public static void goToLogin(Stage stage) {
        renderView(stage, "login.fxml", "LoginController - ToDo List", 1200, 700);
    }


    public static void goToTasks(Stage stage) {
        renderView(stage, "tasks.fxml", "Gestión de Tareas - ToDo List", 1400, 900);
    }

    public static void goToReports(Stage stage) {
        renderView(stage, "reports.fxml", "Reportes - ToDo List", 1400, 900);
    }

    public static void goToStats(Stage stage) {
        renderView(stage, "stats.fxml", "Estadisticas - ToDo List", 1400, 900);
    }


    private static void showModal(String fxmlFile, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(BASE_PATH + fxmlFile));
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana de atrás
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(root, width, height));
            modalStage.centerOnScreen();

            modalStage.show();

        } catch (IOException e) {
            AlertsUtil.showError("Error de Sistema", "No se pudo abrir el formulario: " + fxmlFile);
            System.out.println("Error en showModal: " + e.getMessage());
        }
    }

    public static void closeModal(Stage modalStage) {
        if (modalStage != null) {
            modalStage.close();
        }
    }

    public static void goToRegister() {
        showModal("register.fxml", "Crear Cuenta - ToDo List", 1000, 500);
    }

    public static void goToNewTask() {
        showModal("new-task.fxml", "Nueva Tarea - ToDo List", 550, 800);
    }

    public static void goToSettings() {
        showModal("settings.fxml", "Configuración - ToDo List", 625, 725);
    }

}