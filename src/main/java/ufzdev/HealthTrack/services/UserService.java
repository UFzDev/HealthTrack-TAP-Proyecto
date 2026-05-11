package ufzdev.HealthTrack.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import ufzdev.HealthTrack.dao.UserDao;
import ufzdev.HealthTrack.dao.UserFirestoreDao;
import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.models.UserRole;
import ufzdev.HealthTrack.util.AlertsUtil;

public class UserService {
    private static final UserDao USER_DAO = new UserFirestoreDao();

    public static UserModel autenticate(UserModel userModel) {
        return authenticate(userModel);
    }

    public static UserModel authenticate(UserModel userModel) {
        try {
            if (userModel == null || userModel.getUsername() == null || userModel.getUsername().isBlank()
                    || userModel.getPassword() == null || userModel.getPassword().isBlank()) {
                throw new Exception("Credenciales incompletas");
            }

            UserModel userInDb = USER_DAO.findByUsername(userModel.getUsername().trim());
            if (userInDb == null) {
                throw new Exception("Credenciales incorrectas");
            }

            if (userInDb.getPassword() == null || !userInDb.getPassword().equals(userModel.getPassword())) {
                throw new Exception("Credenciales incorrectas");
            }

            if (userInDb.getRole() == null) {
                userInDb.setRole(UserRole.PACIENTE);
            }

            return userInDb;
        } catch (Exception e) {
            AlertsUtil.showError("Error de autenticacion", "No se pudo autenticar. Verifique sus credenciales.");
            System.out.println("Error durante la autenticacion: " + e.getMessage());
        }
        return null;
    }

    public static UserModel loginTest() {
        UserModel testUserModel = new UserModel();
        testUserModel.setUsername("test");
        testUserModel.setPassword("123456");
        testUserModel.setName("Usuario de prueba");
        testUserModel.setEmail("test01@healthtrack.local");
        testUserModel.setRole(UserRole.PACIENTE);
        return testUserModel;
    }

    public static void registerUser(UserModel userModel) throws Exception {
        if (userModel.getRole() == null) {
            userModel.setRole(UserRole.PACIENTE);
        }

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(userModel.getEmail())
                .setPassword(userModel.getPassword())
                .setDisplayName(userModel.getName());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();

        USER_DAO.create(uid, userModel);
    }

}
