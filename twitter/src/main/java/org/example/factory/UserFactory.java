package org.example.factory;

import org.example.models.User;

public class UserFactory {

    public static User createUser(String username, String displayName, String bio, String profilePictureUrl) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Display name cannot be null or blank");
        }
        return new User(username, displayName, bio, profilePictureUrl);
    }

    public static User createUser(String username, String displayName) {
        return createUser(username, displayName, "", "");
    }
}
