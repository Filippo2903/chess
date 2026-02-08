package client;

import client.audio.AudioPlayer;
import client.audio.SoundEffect;
import gameUtils.Packet;
import gameUtils.PlayerColor;
import modal.ErrorPopup;
import modal.Theme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    static Socket socket = null;
    private static PrintWriter os = null;
    private static BufferedReader is = null;
    private static Game game;

    public static void main(String[] args) {
        Theme.setTheme();

        game = new Game();
        game.startWindow();
    }

    /**
     * Get the game
     *
     * @return The game
     */
    public static Game getGame() {
        return game;
    }

    /**
     * Send a packet to the server
     *
     * @param packet The packet to be sent
     */
    public static void sendMove(Packet packet) {
        try {
            os.println(packet.serializeToString());
        } catch (IOException e) {
            ErrorPopup.show(200);
            System.exit(-1);
        }
    }

    /**
     * Recive a move from the server
     */
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
        game.moveEnemyPiece(packet);
    }

    /**
     * Create the match
     */
    public static void createMatch() {
        System.out.println("Connecting to the server...");
        while (true) {
            try {
                socket = new Socket(InetAddress.getLocalHost(), 4445);
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                os = new PrintWriter(socket.getOutputStream(), true);

                break;
            } catch (Exception e) {
                System.out.print("Connection failed, retrying in 3s...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException err) {
                    throw new RuntimeException(e);
                }
            }
        }

        String colorName = "";
        try {
            colorName = is.readLine();
        } catch (IOException e) {
            ErrorPopup.show("Connection Reset");
            System.exit(-1);
        }

        PlayerColor color = colorName.equals("WHITE") ? PlayerColor.WHITE : PlayerColor.BLACK;

        game.startGame(color);

        System.out.println("Client has color " + color);

        game.chessboardPanel.repaint();

        AudioPlayer.play(SoundEffect.GAME_START);

        // If the client has black, it has to listen first
        if (color == PlayerColor.BLACK) {
            new Thread(Client::receiveMove).start();
        }
    }

    /**
     * End the communication with the server
     */
    public static void endCommunication() {
        try {
            is.close();
        } catch (IOException e) {
            ErrorPopup.show(298);
        }
        os.close();
        try {
            socket.close();
        } catch (IOException e) {
            ErrorPopup.show(299);
        }
    }
}
