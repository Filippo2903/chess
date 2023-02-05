package client.movements;

import client.Game;
import client.Piece;

import java.awt.Point;

public class SingleStepMovement implements Movement {
    @Override
    public boolean canMove(Point from, Point to) {
        Piece[][] board = Game.getBoard();
        Point move = new Point(0, -1);

        return
            MovementUtils.checkMove(from, to, move) &&
            board[to.y][to.x] == null;
    }
}
