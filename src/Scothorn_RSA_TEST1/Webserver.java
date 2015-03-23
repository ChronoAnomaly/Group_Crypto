package Scothorn_RSA_TEST1;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by Brett on 3/23/2015.
 */
public class Webserver {

    public static void main(String argv[]) throws Exception {

        int port = 55573;

        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            // Listen for a TCP connection request.
            Socket clientSocket = serverSocket.accept();

            // TODO: implement file transfer

            // Construct an object to process key request message.
            KeyRequest request = new KeyRequest(clientSocket);

            // Create a new thread to process the request.
            Thread thread = new Thread(request);

            // Start the thread.
            thread.start();
        }
    }
}

/**
 *
 */
final class KeyRequest implements Runnable
{
    final static String CRLF = "\r\n";
    Socket socket;

    public KeyRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    // Implement the run() method of the Runnable interface.
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {

    }
}
