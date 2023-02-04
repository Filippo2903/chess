package client.Movements;

import client.Game;
import client.Piece;

import java.awt.Point;

public class PawnTakeMovement implements Movement {
    @Override
    public boolean canMove(Point from, Point to) {
        Piece[][] board = Game.getBoard();
        Point[] moves = {
                new Point(-1, -1),
                new Point(1, -1)
        };

        for (Point move : moves) {
            if (MovementUtils.checkMove(from, to, move) &&
                board[to.y][to.x] != null) {

                return true;
            }
        }

        return false;
    }
}
