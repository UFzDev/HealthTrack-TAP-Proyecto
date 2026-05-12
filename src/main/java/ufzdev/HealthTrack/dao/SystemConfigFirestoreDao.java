package ufzdev.HealthTrack.dao;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import ufzdev.HealthTrack.config.FirebaseConfig;
import ufzdev.HealthTrack.models.SystemConfigModel;
import ufzdev.HealthTrack.util.EncryptionUtil;

public class SystemConfigFirestoreDao implements SystemConfigDao {
    private static final String COLLECTION = "config";
    private static final String DOCUMENT = "system";
    private final Firestore db;

    public SystemConfigFirestoreDao() {
        this.db = FirebaseConfig.getInstance().getFirestore();
    }

    @Override
    public SystemConfigModel get() throws Exception {
        DocumentSnapshot doc = db.collection(COLLECTION).document(DOCUMENT).get().get();
        if (doc.exists()) {
            SystemConfigModel config = doc.toObject(SystemConfigModel.class);
            if (config != null && config.getAiApiKey() != null) {
                try {
                    config.setAiApiKey(EncryptionUtil.decrypt(config.getAiApiKey()));
                } catch (Exception e) {
                    System.err.println("Error decrypting API Key: " + e.getMessage());
                }
            }
            return config;
        }
        return new SystemConfigModel(); // Default config
    }

    @Override
    public void save(SystemConfigModel config) throws Exception {
        if (config.getAiApiKey() != null) {
            config.setAiApiKey(EncryptionUtil.encrypt(config.getAiApiKey()));
        }
        db.collection(COLLECTION).document(DOCUMENT).set(config).get();
        // Restaurar texto plano en el objeto para que el controlador siga funcionando
        if (config.getAiApiKey() != null) {
            config.setAiApiKey(EncryptionUtil.decrypt(config.getAiApiKey()));
        }
    }
}
