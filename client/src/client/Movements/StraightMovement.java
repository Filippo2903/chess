package client.Movements;

import java.awt.*;

public class StraightMovement implements Movement {
    private final boolean linearMovement;

    public StraightMovement(boolean linearMovement) {
        this.linearMovement = linearMovement;
    }
    /**
     * @return the direction of the move, return a Point(0, 0) if the move is not straight
     */
    private Point straightMove(Point from, Point to) {
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

    @Override
    public boolean canMove(Point from, Point to) {
        Point direction = straightMove(from, to);

        if (direction.x == 0 && direction.y == 0) {
            return false;
        }

        if (linearMovement) {
            if (LinearMovement.isThereAnObstacle(from, to, direction)) {
                return false;
            }

            return true;
        }

        if (from.x + direction.x != to.x || from.y + direction.y != to.y) {
            return false;
        }

        return true;
    }
}
