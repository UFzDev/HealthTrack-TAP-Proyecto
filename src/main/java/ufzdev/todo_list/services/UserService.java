package ufzdev.todo_list.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import ufzdev.todo_list.dao.UserDao;
import ufzdev.todo_list.dao.UserFirestoreDao;
import ufzdev.todo_list.models.StatusModel;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.util.AlertsUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UserService {
    private static final UserDao USER_DAO = new UserFirestoreDao();
    private static final StatusDao STATUS_DAO = new StatusFirestoreDao();

    public static UserModel autenticate(UserModel userModel) {
        try {
            UserModel userInDb = USER_DAO.findByUsername(userModel.getUsername());
            if (userInDb == null) {
                throw new Exception("Credenciales incorrectas");
            }

            if (userModel.getPassword().equals(userInDb.getPassword())) {
                ensureDefaultStatuses(userInDb.getId());
                return userInDb;
            }

            throw new Exception("Credenciales incorrectas");
        } catch (Exception e) {
            AlertsUtil.showError("Error de autenticacion", "No se pudo autenticar. Verifique sus credenciales.");
            System.out.println("Error durante la autenticacion: " + e.getMessage());
        }
        return null;
    }

    public static UserModel loginTest() {
        UserModel testUserModel = new UserModel();
        testUserModel.setUsername("test01");
        testUserModel.setPassword("123456");
        testUserModel.setHasSettings(false);
        return autenticate(testUserModel);
    }

    public static void completeSettings(String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        try {
            USER_DAO.updateHasSettings(userId, true);
        } catch (Exception e) {
            AlertsUtil.showError("Error al guardar configuracion", "No se pudo actualizar el estado de configuracion.");
            System.out.println("Error al completar settings: " + e.getMessage());
        }
    }

    public static void registerUser(UserModel userModel) throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(userModel.getEmail())
                .setPassword(userModel.getPassword())
                .setDisplayName(userModel.getName());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();

        USER_DAO.create(uid, userModel);

        // Crear los 3 estados por defecto
        createDefaultStatuses(uid);
    }

    private static void createDefaultStatuses(String userId) {
        try {
            String[] defaultStatusNames = {"No completada", "En progreso", "Completada"};
            for (String statusName : defaultStatusNames) {
                StatusModel status = new StatusModel();
                status.setId(UUID.randomUUID().toString());
                status.setUserId(userId);
                status.setName(statusName);
                STATUS_DAO.create(status);
            }
            System.out.println("Estados por defecto creados correctamente para el usuario: " + userId);
        } catch (Exception e) {
            System.out.println("Advertencia: No se pudieron crear los estados por defecto. " + e.getMessage());
        }
    }

    private static void ensureDefaultStatuses(String userId) {
        try {
            List<StatusModel> existingStatuses = STATUS_DAO.findByUserId(userId);

            Set<String> existingStatusNames = new HashSet<>();
            for (StatusModel status : existingStatuses) {
                if (status != null && status.getName() != null) {
                    existingStatusNames.add(status.getName().toLowerCase());
                }
            }

            String[] defaultStatusNames = {"no completada", "en progreso", "completada"};
            for (String statusName : defaultStatusNames) {
                if (!existingStatusNames.contains(statusName)) {
                    StatusModel status = new StatusModel();
                    status.setId(UUID.randomUUID().toString());
                    status.setUserId(userId);
                    status.setName(statusName);
                    STATUS_DAO.create(status);
                    System.out.println("Estado creado: " + statusName + " para usuario: " + userId);
                }
            }
        } catch (Exception e) {
            System.out.println("Advertencia: No se pudieron verificar/crear los estados por defecto. " + e.getMessage());
        }
    }
}
