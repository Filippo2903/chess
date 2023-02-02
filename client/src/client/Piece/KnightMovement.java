package client.Piece;

import client.Game;

import java.awt.Point;

public class KnightMovement implements Movement {
    @Override
    public boolean canMove(Point from, Point to) {
        Point[] moves = {
                new Point(-2, -1),
                new Point(-1, -2),
                new Point(1, -2),
                new Point(2, -1),
                new Point(2, 1),
                new Point(1, 2),
                new Point(-1, 2),
                new Point(-2, 1)
        };

        for (Point move : moves) {
            if (to.x == from.x + move.x && to.y == from.y + move.y) {
                return true;
            }
        }

        return false;
    }
}
