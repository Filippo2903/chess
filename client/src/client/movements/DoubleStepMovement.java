package client.movements;

import client.Game;
import client.piece.Piece;

import java.awt.Point;

public class DoubleStepMovement implements Movement {
    @Override
    public boolean canMove(Point from, Point to) {
        final int START_ROW = 6;
        Piece[][] board = Game.getBoard();
        Point move = new Point(0, -2);

        return
            MovementUtils.checkMove(from, to, move) &&
            from.y == START_ROW &&
            board[to.y][to.x] == null;
    }
}
