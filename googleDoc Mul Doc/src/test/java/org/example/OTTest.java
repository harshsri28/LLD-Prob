package org.example;

import org.example.operation.DeleteOperation;
import org.example.operation.EditOperation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OTTest {

    @Test
    public void testDeleteOverlappingDelete() {
        // Doc: "ABCDE"
        // Op1: Delete "BC" (pos 1, len 2)
        // Op2: Delete "C" (pos 2, len 1)
        
        // Op2 is concurrent with Op1.
        // Op1 is executed first. Doc becomes "ADE".
        // Op2 transforms against Op1.
        
        DeleteOperation op1 = new DeleteOperation("user1", 1, 2, 0);
        DeleteOperation op2 = new DeleteOperation("user2", 2, 1, 0);
        
        EditOperation transformedOp2 = op2.transformAgainst(op1);
        
        // Expected: "C" was already deleted by Op1. Op2 should be a No-Op (len 0).
        // Or at least it should NOT delete "D".
        
        if (transformedOp2 instanceof DeleteOperation) {
            DeleteOperation res = (DeleteOperation) transformedOp2;
            System.out.println("Transformed Pos: " + res.position + ", Len: " + res.length);
            
            // If it deleted "D" (which is at index 1 now), pos would be 1, len 1.
            // But we want it to delete NOTHING.
            assertEquals(0, res.length, "Should reduce length to 0 because overlapping content was deleted");
        }
    }

    @Test
    public void testDeleteBeforeDelete() {
        // Doc: "ABCDE"
        // Op1: Delete "AB" (pos 0, len 2)
        // Op2: Delete "D" (pos 3, len 1)
        
        // Op1 applied: "CDE"
        // Op2 transforms. Pos 3 should become 3 - 2 = 1.
        // Deletes "D" at index 1.
        
        DeleteOperation op1 = new DeleteOperation("user1", 0, 2, 0);
        DeleteOperation op2 = new DeleteOperation("user2", 3, 1, 0);
        
        EditOperation transformedOp2 = op2.transformAgainst(op1);
        assertEquals(1, transformedOp2.position);
        
        if (transformedOp2 instanceof DeleteOperation) {
             assertEquals(1, ((DeleteOperation)transformedOp2).length);
        }
    }
}
