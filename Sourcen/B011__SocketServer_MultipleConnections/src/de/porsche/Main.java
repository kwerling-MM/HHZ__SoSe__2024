package de.porsche;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        MultithreadedServer server=new MultithreadedServer();
        try {
            server.start();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
