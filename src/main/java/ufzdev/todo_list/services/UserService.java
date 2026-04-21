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

    public static UserModel autenticate(UserModel userModel) {
        try {
            UserModel userInDb = USER_DAO.findByUsername(userModel.getUsername());
            if (userInDb == null) {
                throw new Exception("Credenciales incorrectas");
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

    public static void registerUser(UserModel userModel) throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(userModel.getEmail())
                .setPassword(userModel.getPassword())
                .setDisplayName(userModel.getName());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();

        USER_DAO.create(uid, userModel);
    }

}
