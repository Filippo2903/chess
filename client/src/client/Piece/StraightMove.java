package client.Piece;

import java.awt.*;

public interface StraightMove {
    /**
     * @return the direction of the move, return a Point(0, 0) if the move is not straight
     */
    default Point straightMove(Point from, Point to) {
        Point direction = new Point(0, 0);

        if (from.x == to.x && from.y == to.y) {
            return direction;
        }

        if (to.x == from.x) {
            direction.x = 0;
            direction.y = from.y > to.y ? -1 : 1;
        }
        else if (to.y == from.y) {
            direction.x = from.x > to.x ? -1 : 1;
            direction.y = 0;
        }

        return direction;
    }
}
