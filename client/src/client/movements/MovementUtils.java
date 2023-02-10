package client.movements;

import client.Game;
import client.piece.Piece;
import gameUtils.PieceType;

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

    public static boolean checkCastleRules(Point rookPosition) {
        final Point KING_POSITION = new Point(4, 7);
        Piece[][] board = Game.getBoard();

        return
            board[KING_POSITION.y][KING_POSITION.x] != null &&
            board[KING_POSITION.y][KING_POSITION.x].getType() == PieceType.KING &&
            board[KING_POSITION.y][KING_POSITION.x].hasMoved() == false &&
            board[rookPosition.y][rookPosition.x] != null &&
            board[rookPosition.y][rookPosition.x].getType() == PieceType.ROOK &&
            board[rookPosition.y][rookPosition.x].hasMoved() == false;
    }
}
