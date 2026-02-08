package server;

import gameUtils.PlayerColor;
import modal.ErrorPopup;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Handles a client (player)
 */
public class PlayerHandler {

    private final PlayerColor playerColor;
    private Socket socket;
    private Scanner input;
    private PrintWriter output;

    private PlayerHandler opponent;

    private boolean ready = false;

    /**
     * Initialize a PlayerHandler
     *
     * @param playerColor The player color
     */
    public PlayerHandler(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    /**
     * Wait for the client connection
     */
    public void waitConnection() {
        try {
            socket = Server.serverSocket.accept();
        } catch (IOException e) {
            ErrorPopup.show("Error in server connection");
        }

        System.out.println("Player " + playerColor + " connected!");

        ready = true;

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

    /**
     * Wait for the client to make a move
     *
     * @return The serialized, base64 encoded move
     */
    public String listenMove() {
        String serializedMove = null;

        try {
            // Wait for a sender client
            serializedMove = input.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("Player " + playerColor + " disconnected");
            Server.startMatchmaking();
        }

        return serializedMove;
    }

    /**
     * Send a serialized move to the client
     *
     * @param serializedMove The serialized, base64 encoded string to send
     */
    public void send(String serializedMove) {
        output.println(serializedMove);
    }

    /**
     * Returns the opponent Player Handler
     *
     * @return The opponent <code>PlayerHandler</code>
     */
    public PlayerHandler getOpponent() {
        return opponent;
    }

    /**
     * Set the player's opponent
     *
     * @param opponent The opponent <code>PlayerHandler</code>
     */
    public void setOpponent(PlayerHandler opponent) {
        this.opponent = opponent;
    }

    /**
     * Returns if the player is ready to play
     *
     * @return <code>true</code> if the player is ready to play, otherwise <code>false</code>
     */
    public boolean isReady() {
        return ready;
    }
}
