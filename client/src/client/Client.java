package client;

import gameUtils.*;
import modal.ErrorPopup;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private static PrintWriter os = null;
    private static BufferedReader is = null;
    private static Socket socket = null;

    private static Game game;

    public static void sendMove(Packet packet) {
        try {
            os.println(packet.serializeToString());
        } catch (IOException e) {
            ErrorPopup.show(200);
            System.exit(-1);
        }
    }

    public static void receiveMove() {
        String response = null;

        // Try receiving the next move
        try {
            response = is.readLine();
        } catch (Exception e) {
            ErrorPopup.show(201);
            System.exit(-1);
        }

        // Deserialize the packet
        Packet packet = null;

        try {
            packet = Packet.fromString(response);
        } catch (Exception e) {
            ErrorPopup.show(202);
            System.exit(-1);
        }

        // Move the enemy in the client
        game.enemyMove(packet);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Starting client...");

        // Set native windows look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            ErrorPopup.show(1);
        }

        InetAddress address = InetAddress.getLocalHost();

        game = new Game();
        game.startWindow();



        // Try connecting to the server
        while (true) {
            try {
                socket = new Socket(address, 4445);
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                os = new PrintWriter(socket.getOutputStream(), true);

                break;
            } catch (IOException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException err) {
                    throw new RuntimeException(e);
                }
            }
        }

        // Read and set the assigned color to the client
        String colorName = is.readLine();
        PlayerColor color = colorName.equals("WHITE") ? PlayerColor.WHITE : PlayerColor.BLACK;

        game.startGame(color);

        System.out.println("Client Address : " + address);

        System.out.println("Client has color " + color);

        // If the client has black, it has to listen first
        if (color == PlayerColor.BLACK) {
            receiveMove();
        }
    }

    public static void endCommunication() {
        try {
            is.close();
        } catch (IOException e) {
            ErrorPopup.show(299);
        }
        os.close();
        try {
            socket.close();
        } catch (IOException e) {
            ErrorPopup.show(298);
        }
        System.out.println("Connection Closed");
    }
}
