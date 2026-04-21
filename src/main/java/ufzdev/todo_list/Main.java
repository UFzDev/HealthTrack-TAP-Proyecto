package ufzdev.todo_list;

import javafx.application.Application;
import javafx.stage.Stage;

import com.google.cloud.firestore.Firestore;
import ufzdev.todo_list.util.AlertsUtil;
import ufzdev.todo_list.config.FirebaseConfig;
import ufzdev.todo_list.util.NavigationUtil;

import java.io.IOException;

public class Main extends Application {
    private Firestore firestore;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            firestore = FirebaseConfig.initialize();
        } catch (Exception e) {
            AlertsUtil.showError("Error de configuración", "No se pudo inicializar Firebase. Verifique la configuración.");
            System.out.println("Error: " + e.getMessage());
        }

        NavigationUtil.goToLogin(stage);
    }

    @Override
    public void stop() {
        if (firestore != null) {
            try {
                firestore.close();
                AlertsUtil.showError("Cierre de Firestore", "Firestore cerrado correctamente.");
                System.out.println("Firestore cerrado correctamente.");
            } catch (Exception e) {
                AlertsUtil.showError("Error al cerrar Firestore", "No se pudo cerrar Firestore. Verifique la configuración.");
                System.out.println("No se pudo cerrar Firestore: " + e.getMessage());
            }
        }
    }
}
