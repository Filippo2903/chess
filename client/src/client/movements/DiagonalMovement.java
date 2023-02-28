package client.movements;

import client.Game;

import java.awt.*;

public class DiagonalMovement implements Movement {
    private final boolean linearMovement;
    public DiagonalMovement(boolean linearMovement) {
        this.linearMovement = linearMovement;
    }

    /**
     * @return the direction of the move, return a Point(0, 0) if the move is not diagonal
     */
    private Point diagonalMove(Point from, Point to) {
        Point direction = new Point(0, 0);

        if (Math.abs(to.x - from.x) == Math.abs(to.y - from.y) ||
            Math.abs(to.x + from.x) == Math.abs(to.y - from.y) ||
            Math.abs(to.x + from.x) == Math.abs(to.y + from.y) ||
            Math.abs(to.x - from.x) == Math.abs(to.y + from.y)) {

            direction.x = from.x > to.x ? -1 : 1;
            direction.y = from.y > to.y ? -1 : 1;
        }

        return direction;
    }

    @Override
    public boolean canMove(Point from, Point to) {
        Point direction = diagonalMove(from, to);

        if (direction.x == 0 && direction.y == 0) {
            return false;
        }

        if (linearMovement) {
            return MovementUtils.isPathFree(from, to, direction);
        }

        return MovementUtils.checkMove(from, to, direction);
    }
}
