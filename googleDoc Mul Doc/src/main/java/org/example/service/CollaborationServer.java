package org.example.service;

import org.example.model.ClientSession;
import org.example.model.Document;
import org.example.operation.EditOperation;

import java.util.*;

public class CollaborationServer {
    private final DocumentManager documentManager = new DocumentManager();

    public void joinDocument(String docId, ClientSession client) {
        documentManager.getOrCreate(docId).join(client);
    }

    public void receive(String docId, EditOperation op) {
        documentManager.getOrCreate(docId).receive(op);
    }
}

