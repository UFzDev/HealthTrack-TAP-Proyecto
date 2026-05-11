package ufzdev.HealthTrack.models;

/**
 * StatusModel is a simple DTO that existed in the upstream template.
 * Currently it is unused in the application. Keeping it here as
 * a deprecated placeholder to avoid accidental deletions. If you
 * want it removed completely, we can move it to a future-packages
 * folder or delete it once you confirm no external references exist.
 */
@Deprecated
public class StatusModel {
    private String id;
    private String userId;
    private String name;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
