package client.piece;

import client.Client;
import client.movements.LMovement;
import gameUtils.PieceType;
import gameUtils.PlayerColor;

import client.Game;
import modal.ErrorPopup;

import java.awt.Point;
import java.util.Objects;

public class Check {
    private static boolean isInsideBoard(Point cell) {
        return
            cell.y < Game.DIM_CHESSBOARD && cell.y >= 0 &&
            cell.x  < Game.DIM_CHESSBOARD && cell.x >= 0;
    }

    public static Point whoIsAttackingCell(Point cell, PlayerColor pieceColor, Piece[][] board) {

        final Point[] pawnTakeMoves = {
                new Point(-1, -1),
                new Point(1, -1)
        };

        for (Point pawnTakeMove : pawnTakeMoves) {
            Point checkedCell = new Point(cell.x + pawnTakeMove.x, cell.y + pawnTakeMove.y);

            if (isInsideBoard(checkedCell) &&
                board[checkedCell.y][checkedCell.x] != null &&
                board[checkedCell.y][checkedCell.x].getType() == PieceType.PAWN &&
                board[checkedCell.y][checkedCell.x].getColor() != pieceColor) {

                return checkedCell;
            }
        }

        for (Point move : LMovement.getMoves()) {
            Point checkedCell = new Point(cell.x + move.x, cell.y + move.y);

            if (isInsideBoard(checkedCell) &&
                board[checkedCell.y][checkedCell.x] != null &&
                board[checkedCell.y][checkedCell.x].getColor() != pieceColor &&
                board[checkedCell.y][checkedCell.x].canMove(cell, board)) {

                return checkedCell;
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

            if (!isInsideBoard(pathController) || board[pathController.y][pathController.x].getColor() == pieceColor) {
                continue;
            }

            if (board[pathController.y][pathController.x].canMove(cell, board)) {
                return pathController;
            }
        }

        return null;
    }

    public static boolean isCellAttacked(Point cell, PlayerColor pieceColor, Piece[][] board) {
        return whoIsAttackingCell(cell, pieceColor, board) != null;
    }

    public static boolean isCheckMate(Point kingPosition, PlayerColor pieceColor) {
        Piece[][] board = Client.getGame().getBoard();

        final Point[] kingMoves = {
                new Point(-1, -1),
                new Point(1, -1),
                new Point(1, 1),
                new Point(-1, 1),
                new Point(0, -1),
                new Point(1, 0),
                new Point(0, 1),
                new Point(-1, 0)
        };

        for (Point move : kingMoves) {
            Point checkedCell = new Point(kingPosition.x + move.x, kingPosition.y + move.y);

            if (isInsideBoard(checkedCell) &&
                !isCellAttacked(checkedCell, pieceColor, board)) {

                if (board[checkedCell.y][checkedCell.x] != null &&
                    board[checkedCell.y][checkedCell.x].getColor() == pieceColor) {
                    continue;
                }

                return false;
            }
        }

        Point kingAttacker = whoIsAttackingCell(kingPosition, pieceColor, board);
        if (kingAttacker == null) {
            return false;
        }

        Point direction = new Point(
                Integer.compare(kingPosition.x, kingAttacker.x),
                Integer.compare(kingPosition.y, kingAttacker.y)
        );

        Point pathController = new Point(kingPosition.x + direction.x, kingPosition.y + direction.y);

        while (isInsideBoard(pathController) &&
                board[pathController.y][pathController.x] == null) {

            if (isCellAttacked(pathController, Client.getGame().getOpponentColor(), board)) {

                return false;
            }

            pathController.x += direction.x;
            pathController.y += direction.y;
        }

        return true;
    }
}
