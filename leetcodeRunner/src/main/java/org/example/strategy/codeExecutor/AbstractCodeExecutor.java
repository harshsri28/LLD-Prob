package org.example.strategy.codeExecutor;

import org.example.models.ExecutionResult;
import org.example.models.Submission;
import org.example.models.TestCase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public abstract class AbstractCodeExecutor implements CodeExecutor {

    @Override
    public ExecutionResult execute(Submission submission) {
        long startTime = System.currentTimeMillis();
        ExecutionResult result = new ExecutionResult();
        Path sourceFile = null;

        try {
            // 1. Create temporary file with user code
            sourceFile = createSourceFile(submission.getCode());

            // 2. Compile code (if needed)
            compileCode(sourceFile);

            // 3. Run the code with test cases
            for (TestCase testCase : submission.getTestCases()) {
                String[] command = getRunCommand(sourceFile, testCase.getInput());
                
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.directory(sourceFile.getParent().toFile());
                pb.redirectErrorStream(true); // Merge stderr into stdout
                
                Process runProcess = pb.start();

                // Start reading output in a separate thread to prevent deadlock
                // Simple approach: Use a Future or just a basic Thread for this example
                final StringBuilder outputBuilder = new StringBuilder();
                Thread outputReader = new Thread(() -> {
                    try {
                        outputBuilder.append(readStream(runProcess.getInputStream()));
                    } catch (Exception e) {
                        // Ignore stream reading errors
                    }
                });
                outputReader.start();

                // Set timeout
                boolean finished = runProcess.waitFor(
                        submission.getTimeout(), TimeUnit.MILLISECONDS);

                if (!finished) {
                    runProcess.destroy();
                    // Ensure reader thread finishes
                    try { outputReader.join(100); } catch (InterruptedException e) {}
                    result.setError("Timeout exceeded");
                    return result;
                }
                
                try { outputReader.join(1000); } catch (InterruptedException e) {}

                // Verify output
                String actualOutput = outputBuilder.toString().trim();
                String expectedOutput = testCase.getExpectedOutput().trim();
                
                if (!actualOutput.equals(expectedOutput)) {
                    result.setError("Test case failed");
                    result.setFailedTestCase(testCase);
                    result.setActualOutput(actualOutput);
                    return result;
                }
            }

            result.setSuccess(true);
        } catch (Exception e) {
            String msg = e.getMessage();
            if ("Compilation error".equals(msg)) {
                result.setError("Compilation error");
            } else {
                result.setError("Runtime error: " + (msg != null ? msg : e.getClass().getSimpleName()));
            }
        } finally {
            cleanup(sourceFile);
            result.setExecutionTime(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    protected abstract Path createSourceFile(String code) throws Exception;

    protected void compileCode(Path sourceFile) throws Exception {
        // Default implementation does nothing (for interpreted languages)
    }

    protected abstract String[] getRunCommand(Path sourceFile, String input);

    protected abstract void cleanup(Path sourceFile);

    private String readStream(java.io.InputStream inputStream) throws java.io.IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }
}

