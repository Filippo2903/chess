package client.Movements;

import client.Game;
import client.Piece;
import client.PieceType;

import java.awt.Point;

public class KingsideCastle implements Movement {
    @Override
    public boolean canMove(Point from, Point to) {
        Piece[][] board = Game.getBoard();

        final Point KING_POSITION = new Point(4, 7);
        final Point ROOK_POSITION = new Point(7, 7);

        Point move = new Point(2, 0);

        return
            MovementUtils.checkMove(from, to, move) &&
            board[KING_POSITION.y][KING_POSITION.x] != null &&
            board[KING_POSITION.y][KING_POSITION.x].getType() == PieceType.KING &&
            board[KING_POSITION.y][KING_POSITION.x].hasMoved() == false &&
            board[ROOK_POSITION.y][ROOK_POSITION.x] != null &&
            board[ROOK_POSITION.y][ROOK_POSITION.x].getType() == PieceType.ROOK &&
            board[ROOK_POSITION.y][ROOK_POSITION.x].hasMoved() == false &&
            MovementUtils.isPathFree(KING_POSITION, ROOK_POSITION, new Point(1, 0));
    }
}
