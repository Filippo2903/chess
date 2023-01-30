package client.Piece;

import client.Game;
import gameUtils.PlayerColor;

public interface CheckPlayerMove {
    /**
     * Check if the piece the player is trying to move is his or not
     * @param color color of the piece
     * @return true if the piece doesn't belong to him, otherwise true
     */
    default boolean isNotPlayerPiece(PlayerColor color) {
        return color != Game.getPlayerColor();
    }

    /**
     * Check if it is not the player's turn
     * @return true if it is not the player's turn, otherwise false
     */
    default boolean isNotPlayerTurn() {
        return Game.getPlayerTurn() != Game.getPlayerColor();
    }
}
