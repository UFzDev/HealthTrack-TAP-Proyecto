package ufzdev.HealthTrack.controllers.admin;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ufzdev.HealthTrack.dao.SystemConfigDao;
import ufzdev.HealthTrack.dao.SystemConfigFirestoreDao;
import ufzdev.HealthTrack.models.SystemConfigModel;
import ufzdev.HealthTrack.util.AlertsUtil;
import ufzdev.HealthTrack.util.TaskExecutorUtil;
import ufzdev.HealthTrack.services.LogService;

import java.net.URL;
import java.util.ResourceBundle;
import ufzdev.HealthTrack.services.AiService;

public class AdminSettingsController implements Initializable {

    @FXML
    private ComboBox<String> aiProviderSelector;
    @FXML
    private PasswordField aiApiKeyField;
    @FXML
    private TextField aiBaseUrlField;
    @FXML
    private Button btnSave;

    private SystemConfigDao configDao;
    private SystemConfigModel currentConfig;
    private AiService aiService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configDao = new SystemConfigFirestoreDao();
        aiService = new AiService();
        aiProviderSelector.setItems(FXCollections.observableArrayList("Gemini", "OpenAI", "Claude", "Local LLM"));
        loadConfig();
    }

    private void loadConfig() {
        TaskExecutorUtil.execute(
            () -> configDao.get(),
            config -> {
                this.currentConfig = config;
                aiProviderSelector.setValue(config.getAiProvider());
                aiBaseUrlField.setText(config.getAiBaseUrl());
                aiApiKeyField.setText(config.getAiApiKey());
            },
            error -> AlertsUtil.showError("Error", "No se pudo cargar la configuración: " + error.getMessage())
        );
    }

    @FXML
    private void handleSave() {
        if (currentConfig == null) return;

        currentConfig.setAiProvider(aiProviderSelector.getValue());
        currentConfig.setAiBaseUrl(aiBaseUrlField.getText());
        currentConfig.setAiApiKey(aiApiKeyField.getText());

        btnSave.setDisable(true);
        TaskExecutorUtil.execute(
            () -> {
                configDao.save(currentConfig);
                LogService.log("Configuración del Sistema", "Se actualizó la configuración global (IA: " + currentConfig.getAiProvider() + ")");
                return true;
            },
            success -> {
                AlertsUtil.showSuccess("Éxito", "Configuración guardada correctamente.");
                btnSave.setDisable(false);
            },
            error -> {
                AlertsUtil.showError("Error", "No se pudo guardar la configuración: " + error.getMessage());
                btnSave.setDisable(false);
            }
        );
    }

    @FXML
    private void handleTestConnection() {
        String testKey = aiApiKeyField.getText();
        String testUrl = aiBaseUrlField.getText();

        if (testKey == null || testKey.trim().isEmpty()) {
            AlertsUtil.showError("Prueba de Conexión", "Por favor ingresa una API Key para probar.");
            return;
        }

        aiService.askAiWithKeyAndUrl("Hola", "Prueba de conexión", testKey, testUrl)
            .thenAccept(response -> {
                javafx.application.Platform.runLater(() -> 
                    AlertsUtil.showSuccess("Conexión Exitosa", "La API respondió correctamente: " + response)
                );
            })
            .exceptionally(ex -> {
                javafx.application.Platform.runLater(() -> 
                    AlertsUtil.showError("Error de Conexión", "Detalle: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()))
                );
                return null;
            });
    }
}
