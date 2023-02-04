package client.Movements;

import client.Game;
import client.Piece;

import java.awt.Point;

public class MovementUtils {
    public static boolean checkMove(Point from, Point to, Point move) {
        return
            to.x == from.x + move.x &&
            to.y == from.y + move.y;
    }
    public static boolean isPathFree(Point from, Point to, Point direction) {
        Piece[][] board = Game.getBoard();
        Point ghostPiece = new Point(from.x, from.y);

        while (ghostPiece.x != to.x - direction.x || ghostPiece.y != to.y - direction.y) {
            ghostPiece.x += direction.x;
            ghostPiece.y += direction.y;

            if (board[ghostPiece.y][ghostPiece.x] != null) {
                return false;
            }
        }

        return true;
    }
}
