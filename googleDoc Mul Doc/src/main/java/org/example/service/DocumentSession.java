package org.example.service;

import org.example.model.ClientSession;
import org.example.model.Document;
import org.example.operation.EditOperation;

import java.util.ArrayList;
import java.util.List;

public class DocumentSession {
    private final String docId;
    private final Document document = new Document();
    private final List<ClientSession> clients = new ArrayList<>();

    public DocumentSession(String docId) {
        this.docId = docId;
    }

    public synchronized void join(ClientSession client) {
        clients.add(client);
        client.sendUpdate(document.getContent(), document.getRevision());
    }

    public synchronized void receive(EditOperation op) {
        document.apply(op);
        broadcast();
    }

    private void broadcast() {
        for (ClientSession c : clients) {
            c.sendUpdate(document.getContent(), document.getRevision());
        }
    }
}
