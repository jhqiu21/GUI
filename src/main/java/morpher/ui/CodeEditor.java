package morpher.ui;

import javafx.scene.layout.StackPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CodeEditor {
    private CodeArea codeEditor;
    private File currentFile;

    public CodeEditor(StackPane container) {
        codeEditor = new CodeArea();
        codeEditor.setParagraphGraphicFactory(LineNumberFactory.get(codeEditor));
        container.getChildren().add(new VirtualizedScrollPane<>(codeEditor));
    }

    public void loadCCode(File file) {
        this.currentFile = file;
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
}
