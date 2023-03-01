package client.movements;

import client.Client;
import client.Game;
import client.piece.Piece;

import java.awt.Point;

public class PawnTakeMovement implements Movement {
    private final Point[] moves = {
            new Point(-1, -1),
            new Point(1, -1)
    };

    @Override
    public boolean canMove(Point from, Point to) {
        Piece[][] board = Client.getGame().getBoard();

        for (Point move : moves) {
            if (MovementUtils.checkMove(from, to, move) &&
                board[to.y][to.x] != null) {

                return true;
            }
        }

        return false;
    }
}
