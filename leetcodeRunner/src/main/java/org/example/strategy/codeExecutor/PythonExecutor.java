package org.example.strategy.codeExecutor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PythonExecutor extends AbstractCodeExecutor {

    @Override
    protected Path createSourceFile(String code) throws Exception {
        Path tempFile = Files.createTempFile("leetcode_py_", ".py");
        Files.write(tempFile, code.getBytes());
        return tempFile;
    }

    @Override
    protected String[] getRunCommand(Path sourceFile, String input) {
        List<String> command = new ArrayList<>();
        command.add("python3");
        command.add(sourceFile.toString());
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
                Files.deleteIfExists(sourceFile);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}

