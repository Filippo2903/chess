package server;

import gameUtils.PlayerColor;
import modal.ErrorPopup;

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

    public PlayerHandler(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }


    public void waitConnection() {
        // Wait the connection
        try {
            socket = Server.serverSocket.accept();
        } catch (IOException e) {
            ErrorPopup.show("Error in server connection");
        }

        System.out.println("Player " + playerColor + " connected!");

        try {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            ErrorPopup.show(205);
            System.exit(-1);
        }

        // Send the color
        output.println(playerColor);
    }

    public String listenMove() {
        String serializedMove = null;

        try {
            // Wait from sender client
            serializedMove = input.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("Player " + playerColor + " disconnected");
            Server.matchmaking();
        }

        return serializedMove;
    }

    public String listenTypePromotion() {
        String serializedMove = null;

        try {
            // Wait from sender client
            serializedMove = input.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("Player " + playerColor + " disconnected");
            Server.matchmaking();
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
}
