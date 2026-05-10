package ufzdev.HealthTrack.dao;

import ufzdev.HealthTrack.models.UserModel;

public interface UserDao {
    UserModel findById(String uid) throws Exception;

    UserModel findByUsername(String username) throws Exception;

    void create(String uid, UserModel userModel) throws Exception;
}
