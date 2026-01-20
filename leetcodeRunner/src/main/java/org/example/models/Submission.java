package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class Submission {
    private final String code;
    private final String language;
    private final List<TestCase> testCases;
    private final long timeout;

    private Submission(Builder builder) {
        this.code = builder.code;
        this.language = builder.language;
        this.testCases = builder.testCases;
        this.timeout = builder.timeout;
    }

    // Getters
    public String getCode() { return code; }
    public String getLanguage() { return language; }
    public List<TestCase> getTestCases() { return testCases; }
    public long getTimeout() { return timeout; }

    // Builder for Submission
    public static class Builder {
        private String code;
        private String language;
        private List<TestCase> testCases = new ArrayList<>();
        private long timeout = 5000; // Default 5 seconds

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Builder setLanguage(String language) {
            this.language = language;
            return this;
        }

        public Builder addTestCase(TestCase testCase) {
            this.testCases.add(testCase);
            return this;
        }

        public Builder setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Submission build() {
            return new Submission(this);
        }
    }
}
