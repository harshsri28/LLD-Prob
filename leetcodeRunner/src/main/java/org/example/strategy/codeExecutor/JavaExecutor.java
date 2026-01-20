package org.example.strategy.codeExecutor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JavaExecutor extends AbstractCodeExecutor {

    @Override
    protected Path createSourceFile(String code) throws Exception {
        Path tempDir = Files.createTempDirectory("leetcode_java_");
        Path sourceFile = tempDir.resolve("Solution.java");
        Files.write(sourceFile, code.getBytes());
        return sourceFile;
    }

    @Override
    protected void compileCode(Path sourceFile) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "javac", "--release", "8", sourceFile.toString());
        pb.redirectErrorStream(true);
        Process compileProcess = pb.start();
        
        // Read output to prevent deadlock
        // For compilation, simple reading in main thread is usually fine if buffer doesn't fill up
        // But to be safe and "proper", we should consume it.
        // Since we wait for it, let's use a simple loop.
        java.io.InputStream is = compileProcess.getInputStream();
        byte[] buffer = new byte[1024];
        while (is.read(buffer) != -1) {} // Discard output
        
        int compileStatus = compileProcess.waitFor();

        if (compileStatus != 0) {
            throw new Exception("Compilation error");
        }
    }

    @Override
    protected String[] getRunCommand(Path sourceFile, String input) {
        Path tempDir = sourceFile.getParent();
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-cp");
        command.add(tempDir.toString());
        command.add("Solution");
        if (input != null && !input.isEmpty()) {
            for (String arg : input.split("\\s+")) {
                command.add(arg);
            }
        }
        return command.toArray(new String[0]);
    }

    @Override
    protected void cleanup(Path sourceFile) {
        if (sourceFile != null) {
            try {
                // Delete the directory and its contents
                Files.walk(sourceFile.getParent())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }
}

