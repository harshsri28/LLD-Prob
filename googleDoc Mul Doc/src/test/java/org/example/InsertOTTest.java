package org.example;

import org.example.operation.DeleteOperation;
import org.example.operation.EditOperation;
import org.example.operation.InsertOperation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InsertOTTest {

    @Test
    public void testInsertVsInsert_SamePosition() {
        // Scenario: Two users insert at the same position.
        // Server policy: The operation already in history (op1) comes first.
        
        // Doc: ""
        // Op1 (History): Insert "A" at 0.
        // Op2 (Incoming): Insert "B" at 0.
        
        InsertOperation op1 = new InsertOperation("u1", 0, "A", 0);
        InsertOperation op2 = new InsertOperation("u2", 0, "B", 0);
        
        // Transform op2 against op1
        op2.transformAgainst(op1);
        
        // Op1 inserts "A" at 0. Doc: "A"
        // Op2 originally at 0.
        // Logic: other.pos (0) <= this.pos (0). Shift right.
        // Op2 pos should become 0 + 1 = 1.
        
        assertEquals(1, op2.position);
        // Result application: "AB".
    }

    @Test
    public void testInsertVsInsert_Before() {
        // Doc: "CD"
        // Op1 (History): Insert "AB" at 0.
        // Op2 (Incoming): Insert "X" at 1 (originally intended between C and D? No, "CD" is at 0,1).
        // Let's say Doc is "CD".
        // Op1 inserts at 0. Doc becomes "ABCD".
        // Op2 intended to insert at 1 (between C and D).
        // Op2 should shift by length of Op1 (2). New pos: 3.
        
        InsertOperation op1 = new InsertOperation("u1", 0, "AB", 0);
        InsertOperation op2 = new InsertOperation("u2", 1, "X", 0);
        
        op2.transformAgainst(op1);
        
        assertEquals(3, op2.position);
    }

    @Test
    public void testInsertVsDelete_Before() {
        // Doc: "ABC"
        // Op1 (History): Delete "A" (Pos 0, Len 1).
        // Op2 (Incoming): Insert "X" at 2 (After "B").
        
        // Op1 applied: "BC".
        // Op2 original intent: "ABXC".
        // Transformed intent: "BXC". (Insert at 1).
        
        DeleteOperation op1 = new DeleteOperation("u1", 0, 1, 0);
        InsertOperation op2 = new InsertOperation("u2", 2, "X", 0);
        
        op2.transformAgainst(op1);
        
        // 2 - 1 = 1.
        assertEquals(1, op2.position);
    }

    @Test
    public void testInsertVsDelete_Inside() {
        // Doc: "ABCDE"
        // Op1 (History): Delete "BCD" (Pos 1, Len 3).
        // Op2 (Incoming): Insert "X" at 2 (At "C").
        
        // Op1 applied: "AE".
        // Op2 original intent: Insert at "C". But "C" is gone.
        // Usually, it should slide to the start of the deletion (Pos 1).
        // Result: "AXE".
        
        DeleteOperation op1 = new DeleteOperation("u1", 1, 3, 0);
        InsertOperation op2 = new InsertOperation("u2", 2, "X", 0);
        
        op2.transformAgainst(op1);
        
        // Logic: this.pos (2) - min(3, 2-1) = 2 - 1 = 1.
        assertEquals(1, op2.position);
    }
}
