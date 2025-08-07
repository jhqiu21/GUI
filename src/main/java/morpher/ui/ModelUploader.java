package morpher.ui;

import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

/**
 * Handles the process of uploading and loading source code files into the CodeEditor.
 * Supports both C and Python files via a file chooser dialog.
 */
public class ModelUploader {
    private CodeEditor codeEditor;
    public ModelUploader (CodeEditor codeEditor) {
        this.codeEditor = codeEditor;
    }

    public void upload(Node source) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Source File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("C Script", "*.c"),
                new FileChooser.ExtensionFilter("Python Script", "*.py")
        );
        Window window = source.getScene().getWindow();
        File file = chooser.showOpenDialog(window);
        if (file != null) {
            codeEditor.loadCode(file);
        }
    }
}
