package ufzdev.HealthTrack.dao;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import ufzdev.HealthTrack.config.FirebaseConfig;
import ufzdev.HealthTrack.models.HealthMetricModel;

import java.util.*;

/**
 * Implementación de HealthMetricDao usando Firebase Firestore.
 */
public class HealthMetricFirestoreDao implements HealthMetricDao {
    private static final String COLLECTION = "mediciones";
    private Firestore db;

    public HealthMetricFirestoreDao() {
        db = FirebaseConfig.getInstance().getFirestore();
    }

    @Override
    public void save(HealthMetricModel metric) throws Exception {
        if (metric.getId() == null || metric.getId().isEmpty()) {
            metric.setId(UUID.randomUUID().toString());
        }
        db.collection(COLLECTION).document(metric.getId()).set(metric).get();
    }

    @Override
    public HealthMetricModel getById(String id) throws Exception {
        DocumentSnapshot doc = db.collection(COLLECTION).document(id).get().get();
        if (doc.exists()) {
            return doc.toObject(HealthMetricModel.class);
        }
        return null;
    }

    @Override
    public List<HealthMetricModel> getByUserId(String userId) throws Exception {
        List<HealthMetricModel> result = new ArrayList<>();
        if (userId == null || userId.isEmpty()) {
            return result;
        }

        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("recordedAt", Query.Direction.DESCENDING)
                .get()
                .get();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            result.add(doc.toObject(HealthMetricModel.class));
        }
        return result;
    }

    @Override
    public List<HealthMetricModel> getByUserIdAndType(String userId, String metricType) throws Exception {
        List<HealthMetricModel> result = new ArrayList<>();
        if (userId == null || userId.isEmpty() || metricType == null || metricType.isEmpty()) {
            return result;
        }

        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("metricType", metricType)
                .orderBy("recordedAt", Query.Direction.DESCENDING)
                .get()
                .get();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            result.add(doc.toObject(HealthMetricModel.class));
        }
        return result;
    }

    @Override
    public List<HealthMetricModel> getLatestByUserId(String userId, int limit) throws Exception {
        List<HealthMetricModel> result = new ArrayList<>();
        if (userId == null || userId.isEmpty()) {
            return result;
        }

        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("recordedAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .get();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            result.add(doc.toObject(HealthMetricModel.class));
        }
        return result;
    }

    @Override
    public List<HealthMetricModel> listAll() throws Exception {
        List<HealthMetricModel> result = new ArrayList<>();
        QuerySnapshot snapshot = db.collection(COLLECTION).get().get();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            result.add(doc.toObject(HealthMetricModel.class));
        }
        return result;
    }

    @Override
    public void delete(String id) throws Exception {
        db.collection(COLLECTION).document(id).delete().get();
    }
}
