package org.example.service;

import java.util.concurrent.ConcurrentHashMap;

public class DocumentManager {
    private final ConcurrentHashMap<String, DocumentSession> documents = new ConcurrentHashMap<>();

    public DocumentSession getOrCreate(String docId) {
        return documents.computeIfAbsent(docId, id -> new DocumentSession(id));
    }
}
