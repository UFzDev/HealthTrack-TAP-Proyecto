package ufzdev.todo_list.dao;

import ufzdev.todo_list.models.UserModel;

public interface UserDao {
    UserModel findById(String uid) throws Exception;

    UserModel findByUsername(String username) throws Exception;

    void create(String uid, UserModel userModel) throws Exception;

    void updateHasSettings(String userId, boolean hasSettings) throws Exception;
}
