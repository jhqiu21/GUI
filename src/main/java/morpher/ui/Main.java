package morpher.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the JavaFX application.
 *
 * This class sets up the main window using MainWindow.fxml, initializes
 * the primary scene, and launches the application UI.
 */
public class Main extends Application {
    /**
     * Called by the JavaFX runtime to start the application.
     *
     * @param stage the primary stage for this application
     * @throws Exception if the FXML resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getClassLoader().getResource("view/MainWindow.fxml")
        );
        Scene scene = new Scene(root, 1100, 600);
        stage.setMinWidth(810);
        stage.setMinHeight(520);
        stage.setTitle("Morpher");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args the command-line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
