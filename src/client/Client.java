package client;

import gameUtils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getLocalHost();
        Socket socket = null;
        String line;
        Scanner scanner = new Scanner(System.in);
        Scanner is = null;
        PrintWriter os = null;

        try {
            socket = new Socket(address, 4445);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.print("IO Exception");
            System.exit(-1);
        }

        System.out.println("Client Address : " + address);
        System.out.println("Enter Data to echo Server ( Enter QUIT to end):");

        String colorName = is.readLine();
        PlayerColor color = colorName.equals("WHITE") ? PlayerColor.WHITE : PlayerColor.BLACK;
        game = new Game(color);
        game.initGame();
        System.out.println("I am color " + color);

        if (color == PlayerColor.BLACK) {
            receiveMove();
        }
    }

    public static void endCommunication() {
        try {
            is.close();
            os.close();
            scanner.close();
            socket.close();
            System.out.println("Connection Closed");
        }
    }
}
