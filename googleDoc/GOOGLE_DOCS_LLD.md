# Google Docs Low-Level Design (LLD) Analysis

This document analyzes the current repository implementation for a collaborative editor (Google Docs style) and verifies the correctness of the defined methods.

## 1. Overview of Current Implementation

The repository implements a basic **Operational Transformation (OT)** system in Java. It follows a Centralized Server architecture where the server acts as the single source of truth and responsible for transforming operations.

### Key Components:

- **`model.Document`**: Represents the document state. It uses a `StringBuilder` for content storage and an `OTEngine` to handle concurrency.
- **`model.ClientSession`**: Represents a connected user. It generates operations (`createInsert`, `createDelete`) and receives updates.
- **`service.CollaborationServer`**: The central coordinator. It receives operations, applies them to the document, and broadcasts the _full document state_ to all clients.
- **`service.OTEngine`**: The core logic for Operational Transformation. It maintains a history of operations and transforms incoming operations against the history to ensure convergence.
- **`operation.EditOperation`**: Abstract base class for mutations (`InsertOperation`, `DeleteOperation`).

## 2. Verification of Methods

### 2.1. `OTEngine.transform(EditOperation incoming)`

- **Definition**: Iterates through the history of executed operations and transforms the `incoming` operation against any operation that has a revision number greater than or equal to the `incoming` operation's basis revision.
- **Correctness**: **Partially Correct**.
  - The logic `if (old.revision >= incoming.revision)` correctly identifies concurrent operations that have already been executed but were unknown to the incoming operation at the time of creation.
  - **Limitation**: It assumes a linear transformation path is sufficient. In complex OT systems (like Jupiter or COT), the order of transformation and the properties of the transformation function (TP1, TP2) are critical. For this simple server-centric model, it works provided the transformation functions are correct.

### 2.2. `InsertOperation.transformAgainst(EditOperation other)`

- **Definition**: Adjusts the insertion position based on a previous operation.
- **Logic**:
  - If `other` is `Insert` before `this`: Shift `this.position` right.
  - If `other` is `Delete` before `this`: Shift `this.position` left.
- **Correctness**: **Mostly Correct**.
  - It handles the basic index shifting.
  - **Edge Case**: Tie-breaking when `this.pos == other.pos` (both inserting at the same spot). The current logic `other.position <= this.position` means the _earlier_ operation (history) pushes the _later_ operation (incoming) to the right. This effectively gives priority to the server's history (or earlier arrived op).

### 2.3. `DeleteOperation.transformAgainst(EditOperation other)`

- **Definition**: Adjusts the delete position based on a previous operation.
- **Logic**:
  - If `other` is `Insert` before `this`: Shift `this.position` right.
  - If `other` is `Delete` before `this`: Shift `this.position` left.
- **Correctness**: **Incorrect (Critical Bug)**.
  - **The Problem**: The current implementation _only_ adjusts `position`. It does **not** adjust `length` or handle overlapping deletions.
  - **Scenario**:
    1.  Doc: "ABC".
    2.  User A deletes "B" (pos 1, len 1). (Executed first). Doc -> "AC".
    3.  User B (concurrently) deletes "BC" (pos 1, len 2).
  - **Current Execution**:
    - User B's op (pos 1) transforms against User A's op (pos 1).
    - Logic: `other.position < this.position` is False (1 < 1 is false).
    - It might fall through or not shift enough.
  - **Worse Scenario**:
    1.  Doc: "ABCDE".
    2.  Op1 (History): Delete at 1, len 2 ("BC"). Doc -> "ADE".
    3.  Op2 (Incoming): Delete at 2, len 1 ("C").
    4.  Transformation: `other.pos` (1) < `this.pos` (2). Shift `this` left by `min(2, 2-1) = 1`. `this.pos` becomes 1.
    5.  Result Op2': Delete at 1, len 1.
    6.  Apply Op2' to "ADE": Deletes "D".
    7.  **Result**: "AE".
    8.  **Expected**: Op2 wanted to delete "C", but "C" was already deleted by Op1. Op2 should have become a No-Op (or length 0). Instead, it deleted "D".

## 3. Architecture Gaps for a "Google Docs" LLD

To upgrade this to a real Google Docs LLD, the following changes are needed:

1.  **Data Structure**:

    - **Current**: `StringBuilder` (O(N) insert/delete).
    - **Required**: **Piece Table**, **Rope**, or **Gap Buffer** for efficient editing of large documents.

2.  **Communication Protocol**:

    - **Current**: Sends full document text on every update (`sendUpdate`).
    - **Required**: Send only **Deltas/Operations**. Clients should apply operations to their local state.

3.  **Client-Side OT**:

    - **Current**: Clients are dumb terminals.
    - **Required**: Clients need their own `OTEngine` and State Machine (Synchronized, Awaiting Confirm, Awaiting With Buffer) to handle local optimistic updates and server acknowledgments.

4.  **Delete Transformation Logic**:
    - Must be updated to handle:
      - Range reduction (if part of the range was already deleted).
      - Range splitting (if an insert happened in the middle of a delete range).
      - Identity preservation.

## 4. Proposed Fix for DeleteOperation

```java
@Override
public EditOperation transformAgainst(EditOperation other) {
    if (other instanceof InsertOperation) {
        InsertOperation ins = (InsertOperation) other;
        if (ins.position <= this.position) {
            this.position += ins.text.length();
        } else if (ins.position < this.position + this.length) {
            // Insert happened INSIDE the delete range.
            // The delete range must now expand to skip the inserted text
            // OR split into two deletes (complex).
            // Simplest valid OT approach: split the delete?
            // Or often in string OT, we assume delete swallows the insert or we treat them as independent chars.
            // Standard approach: Split into two deletes.
        }
    } else if (other instanceof DeleteOperation) {
        DeleteOperation del = (DeleteOperation) other;
        if (del.position < this.position) {
             int deletedBeforeStart = Math.min(del.length, this.position - del.position);
             this.position -= deletedBeforeStart;
             // We also need to reduce length if 'del' overlapped with 'this'
             // ... logic for overlap ...
        }
    }
    return this;
}
```

## 5. Summary

The repository provides a **proof-of-concept** for OT. It correctly identifies the need for transformation and history. However, the transformation logic for `DeleteOperation` is flawed and will lead to data corruption in overlapping delete scenarios. The architecture is also simplified (sending full text) compared to a production-grade Collaborative Editor.
