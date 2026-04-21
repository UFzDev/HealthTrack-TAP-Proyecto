package ufzdev.todo_list.util;

import ufzdev.todo_list.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserSessionUtil {
    private static UserSessionUtil instance;
    private UserModel currentUserModel;
    private List<CategoryModel> categories = new ArrayList<>();
    private List<StatusModel> statuses = new ArrayList<>();
    private List<TaskModel> tasks = new ArrayList<>();
    private TaskModel editingTask;

    private final CategoryDao categoryDao;
    private final StatusDao statusDao;
    private final TaskDao taskDao;

    private UserSessionUtil() {
        this.categoryDao = new CategoryFirestoreDao();
        this.statusDao = new StatusFirestoreDao();
        this.taskDao = new TaskFirestoreDao();
    }

    // Devuelve la misma instancia de UserSessionUtil para toda la aplicación
    public static synchronized UserSessionUtil getInstance() {
        if (instance == null) {
            instance = new UserSessionUtil();
        }
        return instance;
    }

    public synchronized void setSessionData(UserModel userModel,
                                            List<CategoryModel> categories,
                                            List<StatusModel> statuses,
                                            List<TaskModel> tasks) {
        this.currentUserModel = userModel;
        this.categories = categories == null ? new ArrayList<>() : new ArrayList<>(categories);
        this.statuses = statuses == null ? new ArrayList<>() : new ArrayList<>(statuses);
        this.tasks = tasks == null ? new ArrayList<>() : new ArrayList<>(tasks);
    }

    // Carga catalogos y tareas una sola vez al iniciar sesion.
    public synchronized void loadSessionData(UserModel userModel) throws Exception {
        List<CategoryModel> loadedCategories = userModel == null ? new ArrayList<>() : categoryDao.findByUserId(userModel.getId());
        List<StatusModel> loadedStatuses = userModel == null ? new ArrayList<>() : statusDao.findByUserId(userModel.getId());
        List<TaskModel> loadedTasks = userModel == null ? new ArrayList<>() : taskDao.findByUserId(userModel.getId());
        setSessionData(userModel, loadedCategories, loadedStatuses, loadedTasks);
    }

    public synchronized UserModel getUser() {
        return currentUserModel;
    }

    public synchronized List<CategoryModel> getCategories() {
        return categories;
    }

    public synchronized List<StatusModel> getStatuses() {
        return statuses;
    }

    public synchronized List<TaskModel> getTasks() {
        return tasks;
    }

    public synchronized void addCategory(CategoryModel category) {
        if (category != null) {
            categories.add(category);
        }
    }

    public synchronized void addStatus(StatusModel status) {
        if (status != null) {
            statuses.add(status);
        }
    }

    public synchronized void removeCategoryByName(String name) {
        if (name == null || name.isBlank()) {
            return;
        }
        categories.removeIf(item -> name.equalsIgnoreCase(item.getName()));
    }

    public synchronized void removeStatusByName(String name) {
        if (name == null || name.isBlank()) {
            return;
        }
        statuses.removeIf(item -> name.equalsIgnoreCase(item.getName()));
    }

    public synchronized void addTask(TaskModel task) {
        if (task != null) {
            tasks.add(task);
        }
    }

    public synchronized void updateTask(TaskModel updatedTask) {
        if (updatedTask == null || updatedTask.getId() == null || updatedTask.getId().isBlank()) {
            return;
        }

        for (int i = 0; i < tasks.size(); i++) {
            TaskModel current = tasks.get(i);
            if (current != null && updatedTask.getId().equals(current.getId())) {
                tasks.set(i, updatedTask);
                return;
            }
        }
    }

    public synchronized void removeTaskById(String taskId) {
        if (taskId == null || taskId.isBlank()) {
            return;
        }
        tasks.removeIf(task -> task != null && taskId.equals(task.getId()));
    }

    public synchronized void setEditingTask(TaskModel task) {
        this.editingTask = task;
    }

    public synchronized TaskModel getEditingTask() {
        return editingTask;
    }

    public synchronized void clearEditingTask() {
        editingTask = null;
    }

    public synchronized void cleanSession() {
        currentUserModel = null;
        categories = new ArrayList<>();
        statuses = new ArrayList<>();
        tasks = new ArrayList<>();
        editingTask = null;
    }
}