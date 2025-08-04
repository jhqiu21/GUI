package morpher.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CodeCompiler {
    private CodeEditor codeEditor;
    private final Controller controller;

    public CodeCompiler(CodeEditor codeEditor, Controller controller) {
        this.codeEditor = codeEditor;
        this.controller = controller;
    }

    public void runSourceCode() {
        String code = codeEditor.getCodeText();
        String type = codeEditor.getType();

        if (code == null || code.isBlank()) {
            AlertHelper.showError("Error", "No Source code to run. Please upload or enter code.");
            return;
        }

        switch (type) {
        case "c":
            runCCode(code);
            break;
        case "python":
            runPythonCode(code);
            break;
        }

        // TODO Call update() here to update ui
    }


    private void runCCode(String code) {
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
            AlertHelper.showError("Runtime Error", e.getMessage());
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

    private void runPythonCode(String code) {
        try {
            Path workDir = Files.createTempDirectory("morpherPyRun");
            Path script = Files.createTempFile(workDir,"model", ".py");
            Files.writeString(script, code, StandardCharsets.UTF_8);

            ProcessBuilder pb = new ProcessBuilder("python", script.getFileName().toString());
            pb.directory(workDir.toFile());
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            String output = readProcessOutput(proc);
            int exit = proc.waitFor();
            if (exit != 0) {
                AlertHelper.showError("Python Error", output);
            }
            if (!output.isBlank()) {
                AlertHelper.showInfo("Script Output", output);
            }

            Path targetDir = Files.createTempDirectory("morpherProgDir");
            copyRecursively(workDir, targetDir);
            FabricMatrixVisualizer.reload(controller.getFabricMatrixVisualizer(), targetDir);
            deleteRecursively(targetDir);
            deleteRecursively(workDir);
        } catch (Exception e) {
            AlertHelper.showError("Error", e.getMessage());
        }
    }

    private static void copyRecursively(Path srcPath, Path dstPath) throws IOException {
        if (!Files.exists(srcPath)) {
            return;
        }
        try (var stream = Files.walk(srcPath)) {
            stream.filter(p -> p.toString().toLowerCase().endsWith(".prog"))
                    .forEach(p -> {
                        try {
                            Files.copy(p,
                                    dstPath.resolve(p.getFileName()),
                                    StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
        }
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (var walk = Files.walk(path)) {
            walk.sorted(java.util.Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.delete(p); } catch (IOException ignored) {}
                    });
        }
    }
}
