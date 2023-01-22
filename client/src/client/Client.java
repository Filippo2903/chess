package client;

import gameUtils.*;

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

    public static void sendMove(Packet packet) {
        try {
            os.println(packet.serializeToString());
        } catch (IOException e) {
            System.err.println("Error while trying to serialize Move");
            System.exit(-1);
        }
    }

    public static void receiveMove() {
        String response = null;

        try {
            response = is.readLine();
        } catch (Exception e) {
            System.err.println("Error getting the response");
            System.exit(-1);
        }

    Packet packet = null;
        try {
            packet = Packet.fromString(response);
        } catch (Exception e) {
            System.err.println("Error serializing the packet");
            System.exit(-1);
        }

        Game.enemyMove(packet);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Starting client...");

        InetAddress address = InetAddress.getLocalHost();

        Game game;

        try {
            socket = new Socket(address, 4445);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.print("IO Exception");
            System.exit(-1);
        }

        System.out.println("Client Address : " + address);

        String colorName = is.readLine();
        PlayerColor color = colorName.equals("WHITE") ? PlayerColor.WHITE : PlayerColor.BLACK;
        game = new Game(color);
        game.initGame();
        System.out.println(color);

        if (color == PlayerColor.BLACK) {
            receiveMove();
        }
    }

    public static void endCommunication() {
        try {
            is.close();
        } catch (IOException e) {
            System.err.println("Error closing the is");
        }
        os.close();
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing the socket");
        }
        System.out.println("Connection Closed");
    }
}
