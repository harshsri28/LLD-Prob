package org.example.models;

public class TestCaseBuilder {
    private String input;
    private String expectedOutput;
    private String description;

    public TestCaseBuilder setInput(String input) {
        this.input = input;
        return this;
    }

    public TestCaseBuilder setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
        return this;
    }

    public TestCaseBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TestCase build() {
        return new TestCase(input, expectedOutput, description);
    }
}
