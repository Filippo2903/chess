package client.movements;

import client.Game;
import client.piece.Piece;
import gameUtils.PieceType;
import gameUtils.SpecialMoveType;

import java.awt.Point;

public class EnPassant implements Movement {
    private final Point[] moves = {
            new Point(-1, -1),
            new Point(1, -1)
    };

    private boolean checkRules(Point from, Point to) {
        final int EN_PASSANT_ROW = 3;
        final int START_ROW = 1;

        Piece[][] board = Game.getBoard();
        Point[] enemyMove = Game.getEnemyMove();

        final int FROM = 0;
        final int TO = 1;
        return
            from.y == EN_PASSANT_ROW &&
            board[from.y][to.x] != null &&
            board[from.y][to.x].getType() == PieceType.PAWN &&
            enemyMove[FROM].x == to.x &&
            enemyMove[FROM].y == START_ROW &&
            enemyMove[TO].y == from.y;
    }

    @Override
    public boolean canMove(Point from, Point to) {
        for (Point move : moves) {
            if (MovementUtils.checkMove(from, to, move) &&
                checkRules(from, to)) {

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
