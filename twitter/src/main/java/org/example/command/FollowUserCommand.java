package org.example.command;

import org.example.services.UserService;

public class FollowUserCommand implements Command {
    private UserService userService;
    private String followerUsername;
    private String followeeUsername;

    public FollowUserCommand(UserService userService, String followerUsername, String followeeUsername) {
        this.userService = userService;
        this.followerUsername = followerUsername;
        this.followeeUsername = followeeUsername;
    }

    @Override
    public void execute() {
        userService.followUser(followerUsername, followeeUsername);
    }
}
