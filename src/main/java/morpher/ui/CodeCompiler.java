package morpher.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class CodeCompiler {
    private CodeEditor codeEditor;

    public CodeCompiler(CodeEditor codeEditor) {
        this.codeEditor = codeEditor;
    }

    public void runCCode() {
        if (codeEditor.getCodeText().isBlank()) {
            AlertHelper.showError("Error", "No C code to run. Please upload or enter code.");
            return;
        }

        File code = new File(System.getProperty("user.dir"), "model.c");
        File exe = new File(System.getProperty("user.dir"), "modelExe");

        try {
            compile(code, exe);
            execute(exe);
        } catch (IOException e) {
            AlertHelper.showError("I/O Error", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AlertHelper.showError("Interrupted", e.getMessage());
        } catch (Exception e) {
            AlertHelper.showError("Execution Error", e.getMessage());
        }
        cleanupTempFiles(code, exe);
    }

    private void compile(File code, File exe) throws IOException, InterruptedException {
        try (FileWriter fw = new FileWriter(code)) {
            fw.write(codeEditor.getCodeText());
        }

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            exe = new File(System.getProperty("user.dir"), "tempExe.exe");
        }

        // Compile c code to generate execute file
        ProcessBuilder compilePb = new ProcessBuilder(
                "gcc",
                code.getAbsolutePath(),
                "-o",
                exe.getAbsolutePath()
        );
        compilePb.directory(new File(System.getProperty("user.dir")));
        compilePb.redirectErrorStream(true);

        Process compileProc = compilePb.start();
        String compileOutput = readProcessOutput(compileProc);
        int compileExit = compileProc.waitFor();

        // Generate error message
        if (compileExit != 0) {
            AlertHelper.showError("Compile Error", compileOutput);
            return;
        }
    }

    private void execute(File exe) throws IOException, InterruptedException {
        ProcessBuilder runPb = new ProcessBuilder(exe.getAbsolutePath());
        runPb.directory(new File(System.getProperty("user.dir")));
        runPb.redirectErrorStream(true);

        // read output
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

    private void cleanupTempFiles(File tempC, File exeFile) {
        if (tempC.exists()) {
            boolean deletedC = tempC.delete();
            if (!deletedC) {
                System.err.println("Warning: unable to delete temp file " + tempC.getAbsolutePath());
            }
        }
        if (exeFile.exists()) {
            boolean deletedExe = exeFile.delete();
            if (!deletedExe) {
                System.err.println("Warning: unable to delete temp file " + exeFile.getAbsolutePath());
            }
        }
    }
}
