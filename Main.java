import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {

    private TextArea outputArea;

    @Override
    public void start(Stage primaryStage) {
        Button runButton = new Button("Run Python Script");
        outputArea = new TextArea();
        outputArea.setEditable(false);

        runButton.setOnAction(event -> runPythonScript());

        VBox root = new VBox(10, runButton, outputArea);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("JavaFX + Python");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void runPythonScript() {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "script.py");
            pb.redirectErrorStream(true); // 把 stderr 合并进 stdout
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            outputArea.setText(output.toString());

        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
