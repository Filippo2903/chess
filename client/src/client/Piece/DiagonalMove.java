package client.Piece;

import java.awt.*;

public interface DiagonalMove {
    /**
     * @return the direction of the move, return a Point(0, 0) if the move is not diagonal
     */
    default Point diagonalMove(Point from, Point to) {
        Point direction = new Point(0, 0);

        if (from.x == to.x && from.y == to.y) {
            return direction;
        }

        if (Math.abs(to.x - from.x) == Math.abs(to.y - from.y) ||
                Math.abs(to.x + from.x) == Math.abs(to.y - from.y) ||
                Math.abs(to.x + from.x) == Math.abs(to.y + from.y) ||
                Math.abs(to.x - from.x) == Math.abs(to.y + from.y) ) {

            direction.x = from.x > to.x ? -1 : 1;
            direction.y = from.y > to.y ? -1 : 1;
        }

        return direction;
    }
}
