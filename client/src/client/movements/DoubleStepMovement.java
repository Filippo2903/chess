package client.movements;

import client.Game;
import client.piece.Piece;

import java.awt.Point;

public class DoubleStepMovement implements Movement {
    private final Point move = new Point(0, -2);

    private boolean checkRules(Point from, Point to) {
        final int START_ROW = 6;
        Piece[][] board = Game.getBoard();

        return
            from.y == START_ROW &&
            board[to.y][to.x] == null;
    }

    @Override
    public boolean canMove(Point from, Point to) {
        return
            MovementUtils.checkMove(from, to, move) &&
            checkRules(from, to);
    }
}
