package org.example.operation;

public class DeleteOperation extends EditOperation {
    public final int length;

    public DeleteOperation(String userId, int position, int length, long revision) {
        super(userId, position, revision);
        this.length = length;
    }

    @Override
    public void apply(StringBuilder doc) {
        doc.delete(position, position + length);
    }

    @Override
    public EditOperation transformAgainst(EditOperation other) {
        if (other instanceof InsertOperation && other.position <= this.position) {
            this.position += ((InsertOperation) other).text.length();
        } else if (other instanceof DeleteOperation && other.position < this.position) {
            this.position -= Math.min(((DeleteOperation) other).length, this.position - other.position);
        }
        return this;
    }
}

