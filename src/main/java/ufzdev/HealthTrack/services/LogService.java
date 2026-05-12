package ufzdev.HealthTrack.services;

import ufzdev.HealthTrack.dao.SystemLogDao;
import ufzdev.HealthTrack.dao.SystemLogFirestoreDao;
import ufzdev.HealthTrack.models.SystemLogModel;
import ufzdev.HealthTrack.util.UserSessionUtil;

import java.util.List;

public class LogService {
    private static SystemLogDao dao = new SystemLogFirestoreDao();

    public static void log(String action, String details) {
        String username = "Sistema";
        if (UserSessionUtil.getInstance().getUser() != null) {
            username = UserSessionUtil.getInstance().getUser().getUsername();
        }
        
        SystemLogModel logEntry = new SystemLogModel(action, username, details);
        try {
            dao.save(logEntry);
        } catch (Exception e) {
            System.err.println("Critical: Could not save system log: " + e.getMessage());
        }
    }

    public static List<SystemLogModel> getLatestLogs(int limit) throws Exception {
        return dao.listLatest(limit);
    }
}
