package org.example;

import org.example.model.ClientSession;
import org.example.service.CollaborationServer;

public class Main {
    public static void main(String[] args) {
        CollaborationServer server = new CollaborationServer();

        ClientSession u1 = new ClientSession("A");
        ClientSession u2 = new ClientSession("B");

        server.connect(u1);
        server.connect(u2);

        // 1. Initial State: "Hello World"
        // We simulate this by having one user insert it (or two users appending).
        server.receive(u1.createInsert(0, "Hello World")); 
        
        System.out.println("\n--- Starting Concurrent Deletes ---");
        // State is "Hello World" (Revision 1)
        
        // 2. Concurrent Deletes
        // User A wants to delete "World" (index 6, len 5)
        // User B wants to delete "o World" (index 4, len 7)
        // Both operations are created against Revision 1.
        
        var opA = u1.createDelete(6, 5); // "World"
        var opB = u2.createDelete(4, 7); // "o World"
        
        // Server receives A first
        System.out.println("Processing Op A (Delete 'World')...");
        server.receive(opA);
        
        // Server receives B second
        System.out.println("Processing Op B (Delete 'o World')...");
        server.receive(opB);
        
        // Expected Final State: "Hell"
    }
}
