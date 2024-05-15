package de.porsche;

import java.io.*;
import java.net.*;

public class MultithreadedServer {

    public void start() throws IOException {
        int portNumber = 7788; // Port to listen on

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept a new connection

                // Create a new thread to handle the client connection
                Runnable clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        }
    }
}

class ClientHandler implements Runnable {

    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        int counter = 0;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                counter ++;
                System.out.println("Client sent: " + inputLine);

                // Process the client's message (replace with your logic)
                String response = "Antwort #" + counter + "  --  " + inputLine;
                out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
