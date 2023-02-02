package client.Piece;

import client.Game;
import java.awt.Point;

public class PawnMovement implements Movement {
    private boolean isOccupied(Point cell) {
        Piece[][] board = Game.getBoard();

        return board[cell.y][cell.x] != null;
    }

    private boolean standardMove(Point from, Point to) {
        Point standardMove = new Point(0, -1);

        return to.x == from.x + standardMove.x && to.y == from.y + standardMove.y;
    }

    private boolean takeMoves(Point from, Point to) {
        final int EN_PASSANT_ROW = 3;

        Piece[][] board = Game.getBoard();

        Point[] takeMoves = {
                new Point(-1, -1),
                new Point(1, -1)
        };

        // Take move
        for (Point move : takeMoves) {
            if ((to.x == from.x + move.x && to.y == from.y + move.y) &&
                board[to.y][to.x] != null) {

                return true;
            }

            // En passant
//            if ((to.x == from.x + move.x && to.y == from.y + move.y) &&
//                    board[to.y + 1][to.x] != null &&
//                    board[to.y + 1][to.x].getClass().getName().equals("client.Piece.Pawn") &&
//                    from.y == EN_PASSANT_ROW) {
//
//                return true;
//            }
        }

        return false;
    }

    private boolean doubleMove(Point from, Point to) {
        final int START_ROW = 6;
        Point doubleMove = new Point(0, -2);

        return (to.x == from.x + doubleMove.x && to.y == from.y + doubleMove.y) &&
                from.y == START_ROW;
    }

	@Override
	public boolean canMove(Point from, Point to) {
		if (!standardMove(from, to) &&
            !takeMoves(from, to) &&
            !doubleMove(from, to)) {
            return false;
        }

        if ((standardMove(from, to) || doubleMove(from, to)) && isOccupied(to)) {
            return false;
        }

        return true;
	}
}
