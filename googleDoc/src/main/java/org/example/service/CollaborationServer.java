package org.example.service;

import org.example.model.ClientSession;
import org.example.model.Document;
import org.example.operation.EditOperation;

import java.util.*;

public class CollaborationServer {
    private final Document document = new Document();
    private final List<ClientSession> clients = new ArrayList<>();

    public synchronized void connect(ClientSession client) {
        clients.add(client);
    }

    public synchronized void receive(EditOperation op) {
        document.apply(op);

        for (ClientSession c : clients) {
            c.sendUpdate(document.getContent(), document.getRevision());
        }
    }
}

