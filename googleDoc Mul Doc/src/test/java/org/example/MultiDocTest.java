package org.example;

import org.example.model.ClientSession;
import org.example.service.CollaborationServer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MultiDocTest {

    static class TestClientSession extends ClientSession {
        String lastContent = "";
        
        public TestClientSession(String userId) {
            super(userId);
        }
        
        @Override
        public void sendUpdate(String content, long revision) {
            super.sendUpdate(content, revision);
            this.lastContent = content;
        }
    }

    @Test
    public void testMultiDocIsolation() {
        CollaborationServer server = new CollaborationServer();

        TestClientSession u1 = new TestClientSession("A");
        TestClientSession u2 = new TestClientSession("B");
        TestClientSession u3 = new TestClientSession("C");

        server.joinDocument("doc1", u1);
        server.joinDocument("doc1", u2);
        server.joinDocument("doc2", u3);

        // Initial state is empty
        assertEquals("", u1.lastContent);
        assertEquals("", u2.lastContent);
        assertEquals("", u3.lastContent);

        // Update doc1
        server.receive("doc1", u1.createInsert(0, "Hello"));
        
        assertEquals("Hello", u1.lastContent);
        assertEquals("Hello", u2.lastContent);
        assertEquals("", u3.lastContent); // u3 is in doc2, should remain empty

        // Update doc2
        server.receive("doc2", u3.createInsert(0, "Secret"));
        
        assertEquals("Hello", u1.lastContent);
        assertEquals("Hello", u2.lastContent);
        assertEquals("Secret", u3.lastContent);
    }
}
