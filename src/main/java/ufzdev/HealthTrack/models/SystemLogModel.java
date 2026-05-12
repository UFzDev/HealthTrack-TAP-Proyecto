package ufzdev.HealthTrack.models;

import com.google.cloud.firestore.annotation.Exclude;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@IgnoreExtraProperties
public class SystemLogModel {
    private String id;
    private String timestamp;
    private String action;
    private String user;
    private String details;

    public SystemLogModel() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public SystemLogModel(String action, String user, String details) {
        this();
        this.action = action;
        this.user = user;
        this.details = details;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    @Exclude
    public String getFormattedTimestamp() {
        try {
            LocalDateTime ldt = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return ldt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        } catch (Exception e) {
            return timestamp;
        }
    }
}
