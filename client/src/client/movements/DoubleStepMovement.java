package client.movements;

import client.Client;
import client.piece.Piece;

import java.awt.*;

public class DoubleStepMovement implements Movement {
    private final Point move = new Point(0, -2);

    @Override
    public boolean canMove(Point from, Point to, Piece[][] board) {
        final int START_ROW = 6;

        return MovementUtils.checkMove(from, to, move) &&
                from.y == START_ROW &&
                board[to.y][to.x] == null;
    }
}
