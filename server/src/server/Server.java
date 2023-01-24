package server;

import gameUtils.Packet;
import gameUtils.PlayerColor;
import modal.ErrorPopup;
import modal.Theme;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Server {
    private static Scanner inputPlayerOne, inputPlayerTwo;
    private static PrintWriter outputPlayerOne, outputPlayerTwo;

    public static void main(String[] args) throws IOException {
        Theme.setTheme();

        ServerSocket serverSocket = new ServerSocket(4445);

        System.out.println("Server listening...");

        Socket clientSocketOne = serverSocket.accept();
        System.out.println("Player one connected!");

        Socket clientSocketTwo = serverSocket.accept();
        System.out.println("Player two connected!");

        PlayerColor colorPlayerOne, colorPlayerTwo;
        if ((int) (Math.random() * 2) == 0) {
            colorPlayerOne = PlayerColor.WHITE;
            colorPlayerTwo = PlayerColor.BLACK;
        } else {
            colorPlayerOne = PlayerColor.BLACK;
            colorPlayerTwo = PlayerColor.WHITE;
        }

        System.out.println("Assigned color " + colorPlayerOne + " to player one");
        System.out.println("Assigned color " + colorPlayerTwo + " to player two");

        ClientHandler server = new ClientHandler(clientSocketOne, clientSocketTwo, colorPlayerOne, colorPlayerTwo);

        boolean playing = true;

        if (colorPlayerTwo == PlayerColor.WHITE) {
            playing = server.send(inputPlayerTwo, outputPlayerOne);
        }
        while (playing) {
            playing = server.send(inputPlayerOne, outputPlayerTwo);

            if (!playing) break;

            playing = server.send(inputPlayerTwo, outputPlayerOne);
        }

        serverSocket.close();
        inputPlayerOne.close();
        inputPlayerTwo.close();
        outputPlayerOne.close();
        outputPlayerTwo.close();
    }

    private static class ClientHandler {
        public ClientHandler(Socket clientPlayerOne, Socket clientPlayerTwo, PlayerColor colorPlayerOne, PlayerColor colorPlayerTwo) {
            try {
                inputPlayerOne = new Scanner(clientPlayerOne.getInputStream());
                outputPlayerOne = new PrintWriter(clientPlayerOne.getOutputStream(), true);

                inputPlayerTwo = new Scanner(clientPlayerTwo.getInputStream());
                outputPlayerTwo = new PrintWriter(clientPlayerTwo.getOutputStream(), true);
            } catch (IOException e) {
                ErrorPopup.show(301);
            }

            outputPlayerOne.println(colorPlayerOne);
            outputPlayerTwo.println(colorPlayerTwo);
        }

        public boolean send(Scanner sender, PrintWriter receiver) {
            String serializedMove = null;

            try {
                // Read from sender client
                serializedMove = sender.nextLine();
            } catch (NoSuchElementException e) {
                ErrorPopup.show("Client disconnected.");
                System.exit(-1);
            }

            Packet packet = null;
            try {
                packet = Packet.fromString(serializedMove);
            } catch (Exception e) {
                ErrorPopup.show(203);
                System.exit(-1);
            }

            packet.from.y = 7 - packet.from.y;
            packet.to.y = 7 - packet.to.y;

            try {
                serializedMove = packet.serializeToString();
            } catch (IOException e) {
                ErrorPopup.show(204);
                System.exit(-1);
            }

            // Send to other client
            receiver.println(serializedMove);

            return packet.endGame;
        }
    }
}
