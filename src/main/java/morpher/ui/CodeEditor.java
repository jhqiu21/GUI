package morpher.ui;

import javafx.scene.layout.StackPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * A simple source code editor component based on {@link CodeArea}, with support for line numbers,
 * file loading, and language type detection.
 *
 * The editor currently supports C and Python code. It automatically infers the file type
 * based on the file extension when loading code from disk.
 */
public class CodeEditor {
    /** The rich text area used for editing code, with syntax and line number support. */
    private CodeArea codeEditor;
    /** The detected code type, either "c" or "python". */
    private String type;

    public CodeEditor(StackPane container) {
        codeEditor = new CodeArea();
        codeEditor.setParagraphGraphicFactory(LineNumberFactory.get(codeEditor));
        container.getChildren().add(new VirtualizedScrollPane<>(codeEditor));
    }

    public void loadCode(File file) {
        String fileName = file.getName().toLowerCase();
        type = fileName.endsWith(".c") ? "c" : "python";
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            codeEditor.replaceText(sb.toString());
        } catch (IOException e) {
            AlertHelper.showError("File Read Error", e.getMessage());
        }
    }

    public String getCodeText() {
        return codeEditor.getText();
    }

    public String getType() {
        return type;
    }
}
