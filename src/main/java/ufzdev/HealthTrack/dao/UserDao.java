package ufzdev.HealthTrack.dao;

import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.models.UserRole;
import java.util.List;

public interface UserDao {
    UserModel findById(String uid) throws Exception;

    UserModel findByUsername(String username) throws Exception;

    void create(String uid, UserModel userModel) throws Exception;

    // Lista usuarios por rol
    java.util.List<UserModel> listByRole(UserRole role) throws Exception;

    // Lista pacientes asignados a un medico
    java.util.List<UserModel> listPatientsByDoctor(String doctorId) throws Exception;

    // Conteos utilitarios
    int countAll() throws Exception;

    int countByRole(UserRole role) throws Exception;
}
