package morpher.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CodeCompiler {
    private CodeEditor codeEditor;

    public CodeCompiler(CodeEditor codeEditor) {
        this.codeEditor = codeEditor;
    }

    public void runCCode() {
        // TODO: improve the alert window
        String code = codeEditor.getCodeText();
        if (code == null || code.isBlank()) {
            AlertHelper.showError("Error", "No Source code to run. Please upload or enter code.");
            return;
        }

        Path codePath = null;
        Path exePath = null;

        try {
            codePath = Files.createTempFile("model", ".c");

            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            exePath = isWindows
                    ? Files.createTempFile("modelExe", ".exe")
                    : Files.createTempFile("modelExe", "");
            Files.write(codePath, code.getBytes(StandardCharsets.UTF_8));

            compile(codePath, exePath);
            execute(exePath);
        } catch (IOException e) {
            AlertHelper.showError("I/O Error", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AlertHelper.showError("Interrupted", e.getMessage());
        } catch (RuntimeException e) {
            // AlertHelper.showError("Runtime Error", e.getMessage());
        } catch (Exception e) {
            AlertHelper.showError("Execution Error", e.getMessage());
        } finally {
            cleanupTempFiles(codePath, exePath);
        }
    }

    private void compile(Path codePath, Path exePath) throws IOException, InterruptedException, RuntimeException {
        ProcessBuilder compilePb = new ProcessBuilder(
                "gcc",
                codePath.toAbsolutePath().toString(),
                "-o",
                exePath.toAbsolutePath().toString()
        );
        compilePb.redirectErrorStream(true);
        Process compileProc = compilePb.start();
        String compileOutput = readProcessOutput(compileProc);
        int compileExit = compileProc.waitFor();

        if (compileExit != 0) {
            AlertHelper.showError("Compile Error", compileOutput);
            throw new RuntimeException("Compilation failed");
        }
    }

    private void execute(Path exePath) throws IOException, InterruptedException {
        ProcessBuilder runPb = new ProcessBuilder(
                exePath.toAbsolutePath().toString()
        );
        runPb.redirectErrorStream(true);
        Process runProc = runPb.start();
        String runOutput = readProcessOutput(runProc);
        runProc.waitFor();

        if (!runOutput.isBlank()) {
            AlertHelper.showInfo("Program Output", runOutput);
        }
    }

    private String readProcessOutput(Process proc) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(proc.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    private void cleanupTempFiles(Path codePath, Path exePath) {
        try {
            Files.deleteIfExists(codePath);
            Files.deleteIfExists(exePath);
        } catch (IOException e) {
            System.err.println("Warning: unable to delete temporary file!");
        }
    }
}
