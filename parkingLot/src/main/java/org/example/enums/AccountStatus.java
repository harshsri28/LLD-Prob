package org.example.enums;

public enum AccountStatus {
    ACTIVE("Active"),
    BLOCKED("Blocked"),
    BANNED("Banned"),
    COMPROMISED("Compromised"),
    ARCHIVED("Archived"),
    UNKNOWN("Unknown");

    String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}
