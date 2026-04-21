package ufzdev.todo_list.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import ufzdev.todo_list.util.AlertsUtil;

import java.io.InputStream;

public class FirebaseConfig {
    private static FirebaseConfig instance;
    private final Firestore db;

    public static Firestore initialize() {
        if (FirebaseApp.getApps().isEmpty()) {
            try (InputStream serviceAccount = FirebaseConfig.class.getClassLoader()
                    .getResourceAsStream("firebase-key.json")) {
                if (serviceAccount == null) {
                    throw new IllegalStateException("No se encontro firebase-key.json");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
            }catch (Exception e) {
                AlertsUtil.showError("Error de configuración", "No se pudo inicializar Firebase. Verifique la configuración.");
                System.out.println("Error al inicializar Firebase: " + e.getMessage());
            }
        }

        return FirestoreClient.getFirestore();
    }

    private FirebaseConfig() {
        this.db = initialize();
    }

    public static synchronized FirebaseConfig getInstance() {
        if (instance == null) {
            instance = new FirebaseConfig();
        }
        return instance;
    }

    public Firestore getFirestore() {
        return db;
    }
}
