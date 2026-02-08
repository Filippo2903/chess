package client.movements;

import client.piece.Piece;
import gameUtils.SpecialMoveType;

import java.awt.*;

public interface Movement {
    /**
     * Check if the piece can move from a cell to another in a given board
     *
     * @param from  The starting cell
     * @param to    The arrival cell
     * @param board The board where the piece exists
     * @return <code>true</code> if the piece can be moved, <code>false</code> if the piece cannot be moved
     */
    boolean canMove(Point from, Point to, Piece[][] board);

    /**
     * Get the special move
     *
     * @return <code>null</code> if there's not a special move, otherwise a <code>SpecialMoveType</code>
     */
    default SpecialMoveType getSpecialMove() {
        return null;
    }
}
