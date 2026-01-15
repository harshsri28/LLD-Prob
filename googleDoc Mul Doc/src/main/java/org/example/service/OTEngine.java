package org.example.service;

import org.example.operation.EditOperation;

import java.util.*;

public class OTEngine {
    private final List<EditOperation> history = new ArrayList<>();

    public synchronized EditOperation transform(EditOperation incoming) {
        for (EditOperation old : history) {
            if (old.revision >= incoming.revision) {
                incoming = incoming.transformAgainst(old);
            }
        }
        return incoming;
    }

    public synchronized void record(EditOperation op) {
        history.add(op);
    }
}

