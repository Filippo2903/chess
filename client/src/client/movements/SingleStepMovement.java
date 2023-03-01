package client.movements;

import client.Client;
import client.Game;
import client.piece.Piece;

import java.awt.Point;

public class SingleStepMovement implements Movement {
    @Override
    public boolean canMove(Point from, Point to) {
        Piece[][] board = Client.getGame().getBoard();
        Point move = new Point(0, -1);

        return
            MovementUtils.checkMove(from, to, move) &&
            board[to.y][to.x] == null;
    }
}
