package ufzdev.todo_list.util;

import ufzdev.todo_list.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserSessionUtil {
    private static UserSessionUtil instance;
    private UserModel currentUserModel;


    private UserSessionUtil() {
    }

    // Devuelve la misma instancia de UserSessionUtil para toda la aplicación
    public static synchronized UserSessionUtil getInstance() {
        if (instance == null) {
            instance = new UserSessionUtil();
        }
        return instance;
    }

    public synchronized void setSessionData(UserModel userModel){
    }

    // Carga catalogos y tareas una sola vez al iniciar sesion.
    public synchronized void loadSessionData(UserModel userModel) throws Exception {
    }

    public synchronized UserModel getUser() {
        return currentUserModel;
    }

    public synchronized void cleanSession() {
        currentUserModel = null;
    }
}