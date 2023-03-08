package server;

import gameUtils.Packet;
import gameUtils.PlayerColor;
import modal.ErrorPopup;
import modal.Theme;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;

public class Server {
    public static ServerSocket serverSocket;

    /**
     * Turn the move, because every player sees the opponent's move mirrored
     *
     * @param packet Packet that contains the move
     * @return The modified packet
     */
    private static Packet mirrorMove(Packet packet) {
        packet.from.y = 7 - packet.from.y;
        packet.to.y = 7 - packet.to.y;

        return packet;
    }

    /**
     * Wait and handle a move made from a player
     *
     * @param player The player who has to make the move
     */
    private static void waitAndHandleMove(PlayerHandler player) {
        String serializedMove = player.listenMove();

        Packet packet = null;
        try {
            packet = Server.mirrorMove(Packet.fromString(serializedMove));
        } catch (Exception e) {
            ErrorPopup.show(203);
            System.exit(-1);
        }

        try {
            player.getOpponent().send(Objects.requireNonNull(packet).serializeToString());
        } catch (IOException e) {
            ErrorPopup.show(204);
            System.exit(-1);
        }
    }

    /**
     * Start the matchmaking
     */
    public static void startMatchmaking() {
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

        PlayerHandler playerOne = new PlayerHandler(colorPlayerOne);
        PlayerHandler playerTwo = new PlayerHandler(colorPlayerTwo);

        playerOne.setOpponent(playerTwo);
        playerTwo.setOpponent(playerOne);

        Thread playerOneThread = new Thread(playerOne::waitConnection);
        Thread playerTwoThread = new Thread(playerTwo::waitConnection);

        playerOneThread.start();
        playerTwoThread.start();

        try {
            playerOneThread.join();
            playerTwoThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (colorPlayerTwo == PlayerColor.WHITE) {
            Server.waitAndHandleMove(playerTwo);
        }

        while (true) {
            waitAndHandleMove(playerOne);

            waitAndHandleMove(playerTwo);
        }
    }

    public static void main(String[] args) throws IOException {
        Theme.setTheme();

        int port = 4445;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            ErrorPopup.show("Error starting the server");
            System.exit(1);
        }

        startMatchmaking();

        serverSocket.close();
    }
}
