package gameUtils;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private static Socket socket;
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 9999;
        try {
            socket = new Socket(host, port);
            Scanner input = new Scanner(socket.getInputStream());
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            String message;
            Scanner userInput = new Scanner(System.in);

            message = input.nextLine();
            System.out.println("Im client " + message);
            if (message.equals("1")) {
                message = userInput.nextLine();
                output.println(message);
            }

            while (true) {
                message = input.nextLine();
                System.out.println("Received: " + message);
                message = userInput.nextLine();
                output.println(message);
            }
        } finally {
            socket.close();
        }
    }
}
