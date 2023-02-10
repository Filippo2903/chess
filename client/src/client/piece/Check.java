package client.piece;

import client.movements.*;
import gameUtils.PieceType;
import gameUtils.PlayerColor;

import java.awt.Point;

public class Check {
    private static boolean insideTheBoard(Point cell) {
        return
            cell.y < 8 && cell.y >= 0 &&
            cell.x  < 8 && cell.x >= 0;
    }

    public static boolean isInCheck(Point cell, PlayerColor pieceColor, Piece[][] board) {
        final Point[] pawnTakeMoves = { new Point(-1, -1), new Point(1, -1) };
        for (Point pawnTakeMove : pawnTakeMoves) {
            if (insideTheBoard(new Point(cell.y + pawnTakeMove.y, cell.x + pawnTakeMove.x)) &&
                board[cell.y + pawnTakeMove.y][cell.x + pawnTakeMove.x] != null &&
                board[cell.y + pawnTakeMove.y][cell.x + pawnTakeMove.x].getType() == PieceType.PAWN &&
                board[cell.y + pawnTakeMove.y][cell.x + pawnTakeMove.x].getColor() != pieceColor) {

                return true;
            }
        }

        for (Point move : LMovement.getMoves()) {
            if (insideTheBoard(new Point(cell.y + move.y, cell.x + move.x)) &&
                board[cell.y + move.y][cell.x + move.x] != null &&
                board[cell.y + move.y][cell.x + move.x].getColor() != pieceColor &&
                board[cell.y + move.y][cell.x + move.x].canMove(cell)) {

                return true;
            }
        }

        final Point[] directionsLinearMovements = {
                new Point(-1, -1),
                new Point(1, -1),
                new Point(1, 1),
                new Point(-1, 1),
                new Point(0, -1),
                new Point(1, 0),
                new Point(0, 1),
                new Point(-1, 0)
        };
        for (Point direction : directionsLinearMovements) {
            Point pathController = new Point(cell.x + direction.x, cell.y + direction.y);

            while (insideTheBoard(pathController) &&
                   board[pathController.y][pathController.x] == null) {

                pathController.x += direction.x;
                pathController.y += direction.y;
            }

            if (insideTheBoard(pathController) == false) {
                continue;
            }

            if (board[pathController.y][pathController.x].getColor() != pieceColor &&
                board[pathController.y][pathController.x].canMove(cell)) {

                return true;
            }
        }

        return false;
    }
}
