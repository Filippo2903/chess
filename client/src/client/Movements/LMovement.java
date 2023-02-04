package client.Movements;

import java.awt.Point;

public class LMovement implements Movement {
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
            if (MovementUtils.checkMove(from, to, move)) {
                return true;
            }
        }

        return false;
    }
}
