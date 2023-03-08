package client.movements;

import client.Client;
import client.piece.Piece;

import java.awt.*;

public class DoubleStepMovement implements Movement {
    private final Point move = new Point(0, -2);

    @Override
    public boolean canMove(Point from, Point to) {
        final int START_ROW = 6;
        Piece[][] board = Client.getGame().getBoard();

        return MovementUtils.checkMove(from, to, move) &&
                from.y == START_ROW &&
                board[to.y][to.x] == null;
    }
}
