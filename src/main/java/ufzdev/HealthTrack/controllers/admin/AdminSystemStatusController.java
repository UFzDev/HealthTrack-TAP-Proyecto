package ufzdev.HealthTrack.controllers.admin;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ufzdev.HealthTrack.models.SystemLogModel;
import ufzdev.HealthTrack.services.LogService;
import ufzdev.HealthTrack.util.TaskExecutorUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminSystemStatusController implements Initializable {

    @FXML
    private TableView<SystemLogModel> logsTable;
    @FXML
    private TableColumn<SystemLogModel, String> timestampColumn;
    @FXML
    private TableColumn<SystemLogModel, String> userColumn;
    @FXML
    private TableColumn<SystemLogModel, String> actionColumn;
    @FXML
    private TableColumn<SystemLogModel, String> detailsColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadLogs();
    }

    private void setupTable() {
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("formattedTimestamp"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
    }

    private void loadLogs() {
        TaskExecutorUtil.execute(
            () -> LogService.getLatestLogs(100),
            logs -> logsTable.setItems(FXCollections.observableArrayList(logs)),
            error -> System.err.println("Error loading logs: " + error.getMessage())
        );
    }
}
