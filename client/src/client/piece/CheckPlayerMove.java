package client.piece;

import client.Client;
import client.Game;
import gameUtils.PlayerColor;

import java.awt.Point;

public class CheckPlayerMove {
    /**
     * Check if the piece the player is trying to move is his or not
     * @param color color of the piece
     * @return true if the piece doesn't belong to him, otherwise true
     */
    public static boolean isNotPlayerPiece(PlayerColor color) {
        return color != Client.getGame().getPlayerColor();
    }

    /**
     * Check if it is not the player's turn
     * @return true if it is not the player's turn, otherwise false
     */
    public static boolean isNotPlayerTurn() {
        return Client.getGame().getPlayerTurn() != Client.getGame().getPlayerColor();
    }

    /**
     * Check if the player is moving on his own piece
     * @param color color of the player
     * @param cell where the player is trying to move
     * @return true if it is his piece, otherwise false
     */
    public static boolean isMovingOnHisOwnPiece(PlayerColor color, Point cell) {
        Piece[][] board = Client.getGame().getBoard();
        return board[cell.y][cell.x] != null && board[cell.y][cell.x].getColor() == color;
    }
}
