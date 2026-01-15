package org.example;

import org.example.model.ClientSession;
import org.example.service.CollaborationServer;

public class Main {
    public static void main(String[] args) {
        CollaborationServer server = new CollaborationServer();

        ClientSession u1 = new ClientSession("A");
        ClientSession u2 = new ClientSession("B");
        ClientSession u3 = new ClientSession("C");

        System.out.println("--- Joining Documents ---");
        server.joinDocument("doc1", u1);
        server.joinDocument("doc1", u2);
        server.joinDocument("doc2", u3);

        System.out.println("\n--- Sending Updates ---");
        System.out.println("User A inserts 'Hello' into doc1");
        server.receive("doc1", u1.createInsert(0, "Hello"));

        System.out.println("User C inserts 'Secret' into doc2");
        server.receive("doc2", u3.createInsert(0, "Secret"));
        
        // Add more interaction to prove independence
        System.out.println("\n--- Testing Independence ---");
        System.out.println("User B (in doc1) should see 'Hello' but not 'Secret'");
        // Note: The output is printed by ClientSession.sendUpdate which prints to System.out
        
        System.out.println("User C (in doc2) should see 'Secret' but not 'Hello'");
    }
}
