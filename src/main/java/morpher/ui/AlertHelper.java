package morpher.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertHelper {
    public static void showError(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        generateAlert(alert, title, content);
    }

    public static void showInfo(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        generateAlert(alert, title, content);
    }

    public static void generateAlert(Alert alert, String title, String content) {
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
