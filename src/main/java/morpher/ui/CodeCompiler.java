package morpher.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Handles the compilation and execution of source code submitted through a CodeEditor.
 *
 * Supports both C and Python code.
 * C scripts are compiled and executed using gcc.
 * Python scripts are interpreted using the python command.
 *
 * Program outputs or errors are reported via AlertHelper, and compiled Python
 * outputs (e.g., .prog files) are passed to FabricMatrixVisualizer for UI updates.
 */
public class CodeCompiler {
    private CodeEditor codeEditor;
    private final Controller controller;

    public CodeCompiler(CodeEditor codeEditor, Controller controller) {
        this.codeEditor = codeEditor;
        this.controller = controller;
    }

    /**
     * Entry point for compiling and running the source code based on the selected language.
     *
     * Dispatches to either {@link #runCCode(String)} or {@link #runPythonCode(String)},
     * depending on the file type selected in CodeEditor.
     */
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

    /**
     * Compiles and executes the provided C code using the system’s gcc compiler.
     * Temporary source and executable files are created and deleted automatically.
     *
     * @param code the C source code to compile and run
     */
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

    /**
     * Invokes gcc to compile the C source file to a native executable.
     *
     * @param codePath the path to the C source file
     * @param exePath the target executable path
     * @throws IOException if file I/O fails
     * @throws InterruptedException if the compilation process is interrupted
     * @throws RuntimeException if compilation fails (non-zero exit code)
     */
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

    /**
     * Executes the compiled C binary and shows its output in an info dialog.
     *
     * @param exePath the path to the executable
     * @throws IOException if execution fails
     * @throws InterruptedException if the process is interrupted
     */
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

    /**
     * Reads and returns the combined output (stdout and stderr) of a running process.
     *
     * @param proc the process to read from
     * @return the complete output of the process
     * @throws IOException if the stream cannot be read
     */
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

    /**
     * Deletes the temporary source and executable files used in the C compilation process.
     *
     * @param codePath path to the temporary C source file
     * @param exePath path to the compiled executable file
     */
    private void cleanupTempFiles(Path codePath, Path exePath) {
        try {
            Files.deleteIfExists(codePath);
            Files.deleteIfExists(exePath);
        } catch (IOException e) {
            System.err.println("Warning: unable to delete temporary file!");
        }
    }

    /**
     * Executes the given Python code and optionally reloads `.prog` files produced by it.
     *
     * The Python script is written to a temporary directory, and executed using the system
     * python interpreter. All `.prog` files produced are collected and passed to the FabricMatrixVisualizer.
     *
     * @param code the Python code to run
     */
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

    /**
     * Copies all `.prog` files from the source directory to the destination directory.
     *
     * @param srcPath the directory to copy from
     * @param dstPath the directory to copy to
     * @throws IOException if copying fails
     */
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

    /**
     * Recursively deletes all files and subdirectories under the given path.
     *
     * @param path the root directory or file to delete
     * @throws IOException if deletion fails
     */
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
