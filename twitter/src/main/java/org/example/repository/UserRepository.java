package org.example.repository;

import org.example.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserRepository {
    private Map<String, User> usersById = new ConcurrentHashMap<>();
    private Map<String, User> usersByUsername = new ConcurrentHashMap<>();

    public void addUser(User user) {
        usersById.put(user.getUserId(), user);
        usersByUsername.put(user.getUsername(), user);
    }

    public Optional<User> getUserById(String userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    public void removeUser(String userId) {
        User user = usersById.remove(userId);
        if (user != null) {
            usersByUsername.remove(user.getUsername());
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(usersById.values());
    }

    public boolean existsByUsername(String username) {
        return usersByUsername.containsKey(username);
    }

    public boolean existsById(String userId) {
        return usersById.containsKey(userId);
    }

    public List<User> searchByUsername(String query) {
        return usersByUsername.values().stream()
                .filter(u -> u.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                        u.getDisplayName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}
