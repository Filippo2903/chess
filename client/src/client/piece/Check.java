package client.piece;

import client.movements.LMovement;
import gameUtils.PieceType;
import gameUtils.PlayerColor;

import client.Game;

import java.awt.Point;

public class Check {
    private static boolean isInsideBoard(Point cell) {
        return
            cell.y < Game.DIM_CHESSBOARD && cell.y >= 0 &&
            cell.x  < Game.DIM_CHESSBOARD && cell.x >= 0;
    }

    public static boolean isCellAttacked(Point cell, PlayerColor pieceColor, Piece[][] board) {
        System.out.println("===");
        Game.printBoard(board);
        System.out.println("Checking cell x: " + cell.x + " y: " + cell.y + " (" + board[cell.y][cell.x].getType() + board[cell.y][cell.x].getColor() + ")");
        System.out.println("===\n\n");

        final Point[] pawnTakeMoves = { new Point(-1, -1), new Point(1, -1) };

        for (Point pawnTakeMove : pawnTakeMoves) {
            if (isInsideBoard(new Point(cell.y + pawnTakeMove.y, cell.x + pawnTakeMove.x)) &&
                board[cell.y + pawnTakeMove.y][cell.x + pawnTakeMove.x] != null &&
                board[cell.y + pawnTakeMove.y][cell.x + pawnTakeMove.x].getType() == PieceType.PAWN &&
                board[cell.y + pawnTakeMove.y][cell.x + pawnTakeMove.x].getColor() != pieceColor) {

                System.out.println("Attacked by pawn");
                return true;
            }
        }

        for (Point move : LMovement.getMoves()) {
            if (isInsideBoard(new Point(cell.y + move.y, cell.x + move.x)) &&
                board[cell.y + move.y][cell.x + move.x] != null &&
                board[cell.y + move.y][cell.x + move.x].getColor() != pieceColor &&
                board[cell.y + move.y][cell.x + move.x].canMove(cell)) {

                System.out.println("Attacked by knight");
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

            while (isInsideBoard(pathController) &&
                   board[pathController.y][pathController.x] == null) {

                pathController.x += direction.x;
                pathController.y += direction.y;
            }

            if (!isInsideBoard(pathController)) {
                continue;
            }

            if (board[pathController.y][pathController.x].canMove(cell)) {
                return true;
            }
        }

        return false;
    }
}
