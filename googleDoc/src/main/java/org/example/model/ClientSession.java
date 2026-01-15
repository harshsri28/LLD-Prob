package org.example.model;

import org.example.operation.DeleteOperation;
import org.example.operation.EditOperation;
import org.example.operation.InsertOperation;

public class ClientSession {
    private final String userId;
    private long revision = 0;

    public ClientSession(String userId) {
        this.userId = userId;
    }

    public void sendUpdate(String content, long revision) {
        this.revision = revision;
        System.out.println("User " + userId + " sees: " + content);
    }

    public EditOperation createInsert(int pos, String text) {
        return new InsertOperation(userId, pos, text, revision);
    }

    public EditOperation createDelete(int pos, int len) {
        return new DeleteOperation(userId, pos, len, revision);
    }
}

