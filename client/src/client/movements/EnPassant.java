package client.movements;

import client.Client;
import client.piece.Piece;
import gameUtils.PieceType;
import gameUtils.SpecialMoveType;

import java.awt.*;

public class EnPassant implements Movement {
    private final Point[] moves = {
            new Point(-1, -1),
            new Point(1, -1)
    };

    /**
     * Check if the piece can do en passant
     *
     * @param from The starting cell
     * @param to   The arrival cell
     * @return <code>true</code> if the piece can do en passant
     */
    private boolean canEnPassant(Point from, Point to, Piece[][] board) {
        final int EN_PASSANT_ROW = 3;
        final int START_ROW = 1;

        Point[] enemyMove = Client.getGame().getEnemyMove();

        final int FROM = 0;
        final int TO = 1;
        return from.y == EN_PASSANT_ROW &&
                board[from.y][to.x] != null &&
                board[from.y][to.x].getType() == PieceType.PAWN &&
                enemyMove[FROM].x == to.x &&
                enemyMove[FROM].y == START_ROW &&
                enemyMove[TO].y == from.y;
    }

    @Override
    public boolean canMove(Point from, Point to, Piece[][] board) {
        for (Point move : moves) {
            if (MovementUtils.checkMove(from, to, move) &&
                    canEnPassant(from, to, board)) {

                return true;
            }
        }

        return false;
    }

    @Override
    public SpecialMoveType getSpecialMove() {
        return SpecialMoveType.EN_PASSANT;
    }
}
