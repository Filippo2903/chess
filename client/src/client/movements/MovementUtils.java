package client.movements;

import client.Game;
import client.piece.Piece;
import gameUtils.PieceType;

import java.awt.Point;

public class MovementUtils {
    private static Piece[][] board = new Piece[Game.DIM_CHESSBOARD][Game.DIM_CHESSBOARD];
    public static boolean checkMove(Point from, Point to, Point move) {
        return
            to.x == from.x + move.x &&
            to.y == from.y + move.y;
    }

    public static void setBoard(Piece[][] board) {
        MovementUtils.board = board;
    }

    public static boolean isPathFree(Point from, Point to, Point direction) {
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

        return
            board[KING_POSITION.y][KING_POSITION.x] != null &&
            board[KING_POSITION.y][KING_POSITION.x].getType() == PieceType.KING &&
            !board[KING_POSITION.y][KING_POSITION.x].hasMoved() &&
            board[rookPosition.y][rookPosition.x] != null &&
            board[rookPosition.y][rookPosition.x].getType() == PieceType.ROOK &&
            !board[rookPosition.y][rookPosition.x].hasMoved();
    }
}
