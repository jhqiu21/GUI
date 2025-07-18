package morpher.ui;

import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class ModelUploader {
    private CodeEditor codeEditor;
    public ModelUploader (CodeEditor codeEditor) {
        this.codeEditor = codeEditor;
    }

    public void upload(Node source) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Source File");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("C Files", "*.c")
        );
        Window window = source.getScene().getWindow();
        File file = chooser.showOpenDialog(window);
        if (file != null) {
            codeEditor.loadCCode(file);
        }
    }
}
