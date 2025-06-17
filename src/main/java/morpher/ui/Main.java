package morpher.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
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

    public static void main(String[] args) {
        launch(args);
    }
}
