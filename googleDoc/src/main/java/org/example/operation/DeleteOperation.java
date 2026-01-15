package org.example.operation;

public class DeleteOperation extends EditOperation {
    public int length;

    public DeleteOperation(String userId, int position, int length, long revision) {
        super(userId, position, revision);
        this.length = length;
    }

    @Override
    public void apply(StringBuilder doc) {
        if (length > 0) {
            doc.delete(position, position + length);
        }
    }

    @Override
    public EditOperation transformAgainst(EditOperation other) {
        if (other instanceof InsertOperation) {
            InsertOperation ins = (InsertOperation) other;
            if (ins.position <= this.position) {
                this.position += ins.text.length();
            } else if (ins.position < this.position + this.length) {
                // Insert is inside the delete range.
                // We choose to swallow the inserted text to avoid splitting the operation.
                this.length += ins.text.length();
            }
        } else if (other instanceof DeleteOperation) {
            DeleteOperation del = (DeleteOperation) other;

            // 1. Calculate how many characters of 'this' were already deleted by 'del' (overlap)
            int startOverlap = Math.max(this.position, del.position);
            int endOverlap = Math.min(this.position + this.length, del.position + del.length);
            int overlap = Math.max(0, endOverlap - startOverlap);

            // 2. Calculate how much to shift 'this.position' left.
            // Shift is equal to the number of characters deleted by 'del' that were strictly BEFORE 'this.position'.
            int deletedBefore = Math.max(0, Math.min(del.position + del.length, this.position) - del.position);

            // Apply updates
            this.length -= overlap;
            this.position -= deletedBefore;
        }
        return this;
    }
}

