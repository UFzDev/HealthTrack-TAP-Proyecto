package ufzdev.HealthTrack.dao;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import ufzdev.HealthTrack.config.FirebaseConfig;
import ufzdev.HealthTrack.models.SystemLogModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SystemLogFirestoreDao implements SystemLogDao {
    private static final String COLLECTION = "logs";
    private static final int MAX_LOGS = 100;
    private Firestore db;

    public SystemLogFirestoreDao() {
        this.db = FirebaseConfig.getInstance().getFirestore();
    }

    @Override
    public void save(SystemLogModel log) throws Exception {
        if (log.getId() == null) {
            log.setId(UUID.randomUUID().toString());
        }
        db.collection(COLLECTION).document(log.getId()).set(log).get();
        
        // Log rotation: Keep only the last 100
        rotateLogs();
    }

    private void rotateLogs() {
        try {
            QuerySnapshot snapshot = db.collection(COLLECTION)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().get();
            
            if (snapshot.size() > MAX_LOGS) {
                List<QueryDocumentSnapshot> docs = snapshot.getDocuments();
                for (int i = MAX_LOGS; i < docs.size(); i++) {
                    db.collection(COLLECTION).document(docs.get(i).getId()).delete();
                }
            }
        } catch (Exception e) {
            System.err.println("Error rotating logs: " + e.getMessage());
        }
    }

    @Override
    public List<SystemLogModel> listLatest(int limit) throws Exception {
        List<SystemLogModel> logs = new ArrayList<>();
        QuerySnapshot snapshot = db.collection(COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get().get();
        
        for (QueryDocumentSnapshot doc : snapshot) {
            logs.add(doc.toObject(SystemLogModel.class));
        }
        return logs;
    }

    @Override
    public List<SystemLogModel> listAll() throws Exception {
        List<SystemLogModel> logs = new ArrayList<>();
        QuerySnapshot snapshot = db.collection(COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().get();
        
        for (QueryDocumentSnapshot doc : snapshot) {
            logs.add(doc.toObject(SystemLogModel.class));
        }
        return logs;
    }
}
