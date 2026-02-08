package client.movements;

import client.piece.Piece;

import java.awt.*;

public class DiagonalMovement implements Movement {
    private final boolean isLongMovement;

    public DiagonalMovement(boolean isLongMovement) {
        this.isLongMovement = isLongMovement;
    }

    /**
     * @return the direction of the move, return a <code>Point(0, 0)</code> if the move is not diagonal
     */
    private Point moveDirection(Point from, Point to) {
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
    public boolean canMove(Point from, Point to, Piece[][] board) {
        Point direction = moveDirection(from, to);

        if (direction.x == 0 && direction.y == 0) {
            return false;
        }

        if (isLongMovement) {
            return MovementUtils.isPathFree(from, to, direction, board);
        }

        return MovementUtils.checkMove(from, to, direction);
    }
}
