package ufzdev.HealthTrack.controllers.admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ufzdev.HealthTrack.dao.UserDao;
import ufzdev.HealthTrack.dao.UserFirestoreDao;
import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.util.AlertsUtil;
import ufzdev.HealthTrack.util.NavigationUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminUserManagementController implements Initializable {

    @FXML
    private TableView<UserModel> usersTable;
    @FXML
    private TableColumn<UserModel, String> nameColumn;
    @FXML
    private TableColumn<UserModel, String> usernameColumn;
    @FXML
    private TableColumn<UserModel, String> emailColumn;
    @FXML
    private TableColumn<UserModel, String> roleColumn;

    private UserDao userDao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDao = new UserFirestoreDao();
        setupTable();
        loadUsers();
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        usernameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getUsername()));
        emailColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEmail()));
        roleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().getRole() != null ? data.getValue().getRole().name() : "PACIENTE"
        ));

        // Evento de doble clic para editar
        usersTable.setRowFactory(tv -> {
            javafx.scene.control.TableRow<UserModel> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    UserModel rowData = row.getItem();
                    handleEditUser(rowData);
                }
            });
            return row;
        });
    }

    private void handleEditUser(UserModel user) {
        AdminUserModalController.setUserToEdit(user);
        NavigationUtil.goToAdminUserModal();
        loadUsers(); // Recargar tabla tras cerrar modal
    }

    private void loadUsers() {
        try {
            List<UserModel> users = userDao.listAll();
            ObservableList<UserModel> obsUsers = FXCollections.observableArrayList(users);
            usersTable.setItems(obsUsers);
        } catch (Exception e) {
            AlertsUtil.showError("Error de carga", "No se pudieron obtener los usuarios.");
            System.err.println("Error cargando usuarios: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateUser(javafx.event.ActionEvent event) {
        AdminUserModalController.setUserToEdit(null);
        NavigationUtil.goToAdminUserModal();
        loadUsers();
    }

    @FXML
    private void handleClose(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
