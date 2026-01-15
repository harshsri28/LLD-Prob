package org.example.model;

import org.example.operation.EditOperation;
import org.example.service.OTEngine;

public class Document {
    private final StringBuilder content = new StringBuilder();
    private final OTEngine otEngine = new OTEngine();
    private long revision = 0;

    public synchronized void apply(EditOperation op) {
        EditOperation transformed = otEngine.transform(op);
        transformed.apply(content);
        otEngine.record(transformed);
        revision++;
    }

    public synchronized String getContent() {
        return content.toString();
    }

    public synchronized long getRevision() {
        return revision;
    }
}

