package ufzdev.todo_list.util;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class AlertsUtil {

    public static void showSuccess(String title, String message) {
        Platform.runLater(() -> {
            Notifications.create()
                    .title(title)
                    .text(message)
                    .position(Pos.TOP_RIGHT)
                    .hideAfter(Duration.seconds(3))
                    .showConfirm();
        });
    }

    public static void showError(String title, String message) {
        Platform.runLater(() -> {
            Notifications.create()
                    .title(title)
                    .text(message)
                    .position(Pos.TOP_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .showError();
        });
    }
}