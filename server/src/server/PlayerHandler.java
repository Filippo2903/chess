package server;

import gameUtils.PlayerColor;
import modal.ErrorPopup;

import java.awt.desktop.ScreenSleepEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class PlayerHandler {

    private PlayerColor playerColor;
    private Socket socket;
    private Scanner input;
    private PrintWriter output;

    private PlayerHandler opponent;

    private boolean ready = false;

    public PlayerHandler(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public void waitConnection() {
        try {
            socket = Server.serverSocket.accept();
        } catch (IOException e) {
            ErrorPopup.show("Error in server connection");
        }

        System.out.println("Player " + playerColor + " connected!");

        ready = true;

        System.out.println("Player  "+ playerColor + " is ready (" + ready + ")");

        try {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            ErrorPopup.show(205);
            System.exit(-1);
        }

        while (!opponent.isReady()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Send the color
        output.println(playerColor);
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public String listenMove() {
        String serializedMove = null;

        try {
            // Wait from sender client
            serializedMove = input.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("Player " + playerColor + " disconnected");
            Server.startMatchmaking();
        }

        return serializedMove;
    }

    public void send(String serializedMove) {
        output.println(serializedMove);
    }

    public void setOpponent(PlayerHandler opponent) {
        this.opponent = opponent;
    }
    public PlayerHandler getOpponent() {
        return opponent;
    }
    public PlayerColor getPlayerColor() {
        return playerColor;
    }
    public void setPlayerColor(PlayerColor color) {
        playerColor = color;
    }

    public boolean isReady() {
        return ready;
    }
}
