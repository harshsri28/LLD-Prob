package org.example;

import org.example.model.ClientSession;
import org.example.service.CollaborationServer;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        CollaborationServer server = new CollaborationServer();

        ClientSession u1 = new ClientSession("A");
        ClientSession u2 = new ClientSession("B");

        server.connect(u1);
        server.connect(u2);

        server.receive(u1.createInsert(0, "Hello"));
        server.receive(u2.createInsert(5, " World"));
    }
}