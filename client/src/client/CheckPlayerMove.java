package client;

import gameUtils.PlayerColor;

public class CheckPlayerMove {
    /**
     * Check if the piece the player is trying to move is his or not
     * @param color color of the piece
     * @return true if the piece doesn't belong to him, otherwise true
     */
    public static boolean isNotPlayerPiece(PlayerColor color) {
        return color != Game.getPlayerColor();
    }

    /**
     * Check if it is not the player's turn
     * @return true if it is not the player's turn, otherwise false
     */
    public static boolean isNotPlayerTurn() {
        return Game.getPlayerTurn() != Game.getPlayerColor();
    }
}
