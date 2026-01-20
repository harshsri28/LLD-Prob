package org.example.models;

public class ExecutionResult {
    private boolean success;
    private String error;
    private long executionTime;
    private TestCase failedTestCase;
    private String actualOutput;

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
    public TestCase getFailedTestCase() { return failedTestCase; }
    public void setFailedTestCase(TestCase failedTestCase) { this.failedTestCase = failedTestCase; }
    public String getActualOutput() { return actualOutput; }
    public void setActualOutput(String actualOutput) { this.actualOutput = actualOutput; }
}
