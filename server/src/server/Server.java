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

    private static Packet turnBoard(Packet packet) {
        packet.from.y = 7 - packet.from.y;
        packet.to.y = 7 - packet.to.y;

        return packet;
    }

    private static boolean handleMove(PlayerHandler player) {
        String serializedMove = player.listenMove();

        Packet packet = null;
        try {
            packet = Server.turnBoard(Packet.fromString(serializedMove));
        } catch (Exception e) {
            ErrorPopup.show(203);
            System.exit(-1);
        }

        if (!packet.endGame) {
            return false;
        }

        try {
            player.getOpponent().send(Objects.requireNonNull(packet).serializeToString());
        } catch (IOException e) {
            ErrorPopup.show(204);
            System.exit(-1);
        }

        return true;
    }

    public static void matchmaking() {
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

        playerOne.waitConnection();
        playerTwo.waitConnection();

        boolean playing = true;
        if (colorPlayerTwo == PlayerColor.WHITE) {
            playing = Server.handleMove(playerTwo);
        }

        while (playing) {
            playing = handleMove(playerOne);

            if (!playing) break;

            playing = handleMove(playerTwo);
        }
    }

    public static void main(String[] args) throws IOException {
        Theme.setTheme();

        int port = 4445;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            ErrorPopup.show("Error starting the server");
        }

        matchmaking();

        serverSocket.close();
    }
}
