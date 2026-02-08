package client.movements;

import client.Client;
import client.piece.Piece;

import java.awt.*;

public class SingleStepMovement implements Movement {
    @Override
    public boolean canMove(Point from, Point to, Piece[][] board) {
        Point move = new Point(0, -1);

        return MovementUtils.checkMove(from, to, move) &&
                board[to.y][to.x] == null;
    }
}
