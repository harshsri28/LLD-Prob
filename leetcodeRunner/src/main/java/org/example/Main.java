package org.example;

import org.example.models.ExecutionResult;
import org.example.models.Submission;
import org.example.models.TestCase;
import org.example.models.TestCaseBuilder;
import org.example.observer.LoggerObserver;
import org.example.services.CodeRunnerService;

import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) {
        CodeRunnerService service = new CodeRunnerService();
        service.addObserver(new LoggerObserver());

        // Build test case
        TestCase testCase = new TestCaseBuilder()
                .setInput("10 20")
                .setExpectedOutput("30")
                .setDescription("Simple addition test")
                .build();

        // Build submission
        Submission submission = new Submission.Builder()
                .setCode("public class Solution {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        int a = Integer.parseInt(args[0]);\n" +
                        "        int b = Integer.parseInt(args[1]);\n" +
                        "        System.out.println(a + b);\n" +
                        "    }\n" +
                        "}")
                .setLanguage("java")
                .addTestCase(testCase)
                .setTimeout(3000)
                .build();

        CompletableFuture<ExecutionResult> future1 = service.runCode(submission)
                .thenApply(result -> {
                    System.out.println("=== Java Submission ===");
                    printResult(result);
                    return result;
                });

        // Build Python submission
        Submission pythonSubmission = new Submission.Builder()
                .setCode("import sys\n" +
                        "if __name__ == '__main__':\n" +
                        "    a = int(sys.argv[1])\n" +
                        "    b = int(sys.argv[2])\n" +
                        "    print(a + b)")
                .setLanguage("python")
                .addTestCase(testCase)
                .setTimeout(3000)
                .build();

        CompletableFuture<ExecutionResult> future2 = service.runCode(pythonSubmission)
                .thenApply(result -> {
                    System.out.println("\n=== Python Submission ===");
                    printResult(result);
                    return result;
                });
        
        // Wait for both to complete
        CompletableFuture.allOf(future1, future2).join();
        
        service.shutdown();
    }

    private static void printResult(ExecutionResult result) {
        if (result.isSuccess()) {
            System.out.println("All test cases passed!");
        } else {
            System.out.println("Failed: " + result.getError());
            if (result.getFailedTestCase() != null) {
                System.out.println("Failed test case: " +
                        result.getFailedTestCase().getDescription());
                System.out.println("Expected: " +
                        result.getFailedTestCase().getExpectedOutput());
                System.out.println("Actual: " + result.getActualOutput());
            }
        }
    }
}
