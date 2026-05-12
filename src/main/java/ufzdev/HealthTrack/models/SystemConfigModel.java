package ufzdev.HealthTrack.models;

import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

@IgnoreExtraProperties
public class SystemConfigModel {
    private String aiProvider;
    private String aiApiKey;
    private String aiBaseUrl;
    private boolean maintenanceMode;
    private String systemVersion;
    private String supportEmail;

    public SystemConfigModel() {
        this.aiProvider = "Groq";
        this.aiBaseUrl = "https://api.groq.com/openai/v1/chat/completions";
        this.maintenanceMode = false;
        this.systemVersion = "1.0.0";
        this.supportEmail = "soporte@healthtrack.local";
    }

    // Getters and Setters
    public String getAiProvider() { return aiProvider; }
    public void setAiProvider(String aiProvider) { this.aiProvider = aiProvider; }

    public String getAiApiKey() { return aiApiKey; }
    public void setAiApiKey(String aiApiKey) { this.aiApiKey = aiApiKey; }

    public String getAiBaseUrl() { return aiBaseUrl; }
    public void setAiBaseUrl(String aiBaseUrl) { this.aiBaseUrl = aiBaseUrl; }

    public boolean isMaintenanceMode() { return maintenanceMode; }
    public void setMaintenanceMode(boolean maintenanceMode) { this.maintenanceMode = maintenanceMode; }

    public String getSystemVersion() { return systemVersion; }
    public void setSystemVersion(String systemVersion) { this.systemVersion = systemVersion; }

    public String getSupportEmail() { return supportEmail; }
    public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }
}
