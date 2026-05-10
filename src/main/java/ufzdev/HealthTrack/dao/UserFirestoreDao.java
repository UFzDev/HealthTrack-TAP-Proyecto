package ufzdev.HealthTrack.dao;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import ufzdev.HealthTrack.config.FirebaseConfig;
import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.models.UserRole;

import java.util.HashMap;
import java.util.Map;

public class UserFirestoreDao implements UserDao {
    private final Firestore db;

    public UserFirestoreDao() {
        this.db = FirebaseConfig.getInstance().getFirestore();
    }

    @Override
    public UserModel findById(String uid) throws Exception {
        if (uid == null || uid.isBlank()) {
            return null;
        }

        DocumentSnapshot doc = db.collection("usuarios").document(uid).get().get();
        if (!doc.exists()) {
            return null;
        }

        UserModel user = new UserModel();
        user.setId(uid);
        user.setName(firstNonBlank(doc.getString("nombre"), doc.getString("name")));
        user.setUsername(firstNonBlank(doc.getString("usuario"), doc.getString("username")));
        user.setEmail(firstNonBlank(doc.getString("correo"), doc.getString("email")));
        user.setPassword(doc.getString("password"));
        user.setRole(parseRole(doc.getString("role")));

        return user;
    }

    @Override
    public void create(String uid, UserModel userModel) throws Exception {
        if (uid == null || uid.isBlank() || userModel == null) {
            return;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", userModel.getName());
        userData.put("usuario", userModel.getUsername());
        userData.put("correo", userModel.getEmail());
        userData.put("password", userModel.getPassword());
        userData.put("role", userModel.getRole() != null ? userModel.getRole().name() : UserRole.PACIENTE.name());

        db.collection("usuarios").document(uid).set(userData).get();
    }

    @Override
    public UserModel findByUsername(String username) throws Exception {
        if (username == null || username.isBlank()) {
            return null;
        }

        QuerySnapshot snapshot = db.collection("usuarios")
                .whereEqualTo("usuario", username.trim())
                .limit(1)
                .get()
                .get();

        if (snapshot.isEmpty()) {
            return null;
        }

        DocumentSnapshot doc = snapshot.getDocuments().getFirst();
        UserModel user = new UserModel();
        user.setId(doc.getId());
        user.setName(firstNonBlank(doc.getString("nombre"), doc.getString("name")));
        user.setUsername(firstNonBlank(doc.getString("usuario"), doc.getString("username")));
        user.setEmail(firstNonBlank(doc.getString("correo"), doc.getString("email")));
        user.setPassword(doc.getString("password"));
        user.setRole(parseRole(doc.getString("role")));

        return user;
    }

    private UserRole parseRole(String roleString) {
        if (roleString == null || roleString.isBlank()) {
            return UserRole.PACIENTE;
        }

        try {
            return UserRole.valueOf(roleString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UserRole.PACIENTE;
        }
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback;
    }
}
