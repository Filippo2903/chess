package client.Movements;

import java.awt.Point;

public class AllDirectionMovement implements Movement {
    private final StraightMovement straightMovement;
    private final DiagonalMovement diagonalMovement;
    public AllDirectionMovement(boolean linearMovement) {
        straightMovement = new StraightMovement(linearMovement);
        diagonalMovement = new DiagonalMovement(linearMovement);
    }

    @Override
    public boolean canMove(Point from, Point to) {
        return straightMovement.canMove(from, to) || diagonalMovement.canMove(from, to);
    }
}
