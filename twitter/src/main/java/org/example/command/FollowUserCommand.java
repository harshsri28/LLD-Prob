package org.example.command;

import org.example.services.UserService;

public class FollowUserCommand implements Command {
    private UserService userService;
    private String followerUserId;
    private String followeeUserId;

    public FollowUserCommand(UserService userService, String followerUserId, String followeeUserId) {
        this.userService = userService;
        this.followerUserId = followerUserId;
        this.followeeUserId = followeeUserId;
    }

    @Override
    public void execute() {
        userService.followUser(followerUserId, followeeUserId);
    }
}
