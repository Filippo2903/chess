package client.movements;

import client.piece.Piece;

import java.awt.*;

public class LMovement implements Movement {
    private static final Point[] moves = {
            new Point(-2, -1),
            new Point(-1, -2),
            new Point(1, -2),
            new Point(2, -1),
            new Point(2, 1),
            new Point(1, 2),
            new Point(-1, 2),
            new Point(-2, 1)
    };

    public static Point[] getMoves() {
        return moves;
    }

    @Override
    public boolean canMove(Point from, Point to, Piece[][] board) {
        for (Point move : moves) {
            if (MovementUtils.checkMove(from, to, move)) {
                return true;
            }
        }

        return false;
    }
}
