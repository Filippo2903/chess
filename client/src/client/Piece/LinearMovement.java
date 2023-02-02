package client.Piece;

import client.Game;

import java.awt.*;

public class LinearMovement{
    public static boolean isThereAnObstacle(Point from, Point to, Point direction) {
        Piece[][] board = Game.getBoard();
        Point ghostPiece = new Point(from.x, from.y);

        while (ghostPiece.x != to.x - direction.x || ghostPiece.y != to.y - direction.y) {
            ghostPiece.x += direction.x;
            ghostPiece.y += direction.y;

            if (board[ghostPiece.y][ghostPiece.x] != null) {
                return true;
            }
        }

        return false;
    }
}
