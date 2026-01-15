package org.example.operation;

public class InsertOperation extends EditOperation {
    public final String text;

    public InsertOperation(String userId, int position, String text, long revision) {
        super(userId, position, revision);
        this.text = text;
    }

    @Override
    public void apply(StringBuilder doc) {
        doc.insert(position, text);
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
