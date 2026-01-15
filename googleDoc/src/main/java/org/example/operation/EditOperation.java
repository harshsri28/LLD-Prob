package org.example.operation;

public abstract class EditOperation {
    public final String userId;
    public int position;
    public final long revision;

    public EditOperation(String userId, int position, long revision) {
        this.userId = userId;
        this.position = position;
        this.revision = revision;
    }

    public abstract void apply(StringBuilder doc);
    public abstract EditOperation transformAgainst(EditOperation other);
}
