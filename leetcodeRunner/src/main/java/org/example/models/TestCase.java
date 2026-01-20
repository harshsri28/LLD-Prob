package org.example.models;

public class TestCase {
    private final String input;
    private final String expectedOutput;
    private final String description;

    public TestCase(String input, String expectedOutput, String description) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.description = description;
    }

    // Getters
    public String getInput() { return input; }
    public String getExpectedOutput() { return expectedOutput; }
    public String getDescription() { return description; }
}
