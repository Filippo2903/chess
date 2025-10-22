package client.piece;

import client.Client;
import client.Game;
import client.movements.LMovement;
import gameUtils.PieceType;
import gameUtils.PlayerColor;

import java.awt.*;

public class Check {
    private static boolean isInsideBoard(Point cell) {
        return
                cell.y < Game.DIM_CHESSBOARD && cell.y >= 0 &&
                        cell.x < Game.DIM_CHESSBOARD && cell.x >= 0;
    }

    public static Point getAttackerCell(Point cell, PlayerColor pieceColor, Piece[][] board) {

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
        return getAttackerCell(cell, pieceColor, board) != null;
    }

    public static boolean isCheckMate(Point kingPosition, PlayerColor pieceColor) {
        System.out.println("START checking check mate...");
        Piece[][] board = Client.getGame().getBoard();

        System.out.println("checking if king is safe...");
        Point kingAttacker = getAttackerCell(kingPosition, pieceColor, board);
        if (kingAttacker == null) {
            return false;
        }

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

        System.out.println("checking if king can escape...");
        for (Point move : kingMoves) {
            Point checkedCell = new Point(kingPosition.x + move.x, kingPosition.y + move.y);
            if (!isInsideBoard(checkedCell) || isCellAttacked(checkedCell, pieceColor, board)) continue;

            if (board[checkedCell.y][checkedCell.x] != null &&
                    board[checkedCell.y][checkedCell.x].getColor() == pieceColor) {
                continue;
            }

            return false;
        }

        System.out.println("checking if piece is eatable...");
        PlayerColor attackerColor = pieceColor == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
        Point eatingPiece = getAttackerCell(kingAttacker, attackerColor, board);
        if (eatingPiece != null) {
            if (eatingPiece.x == kingPosition.x && eatingPiece.y == kingPosition.y) {
                if (!isCellAttacked(kingAttacker, attackerColor, board)) return false;
            } else {
                return false;
            }
        }

        Point direction = new Point(
                Integer.compare(kingAttacker.x, kingPosition.x),
                Integer.compare(kingAttacker.y, kingPosition.y)
        );

        System.out.println("checking if piece is a knight...");
        if (Math.abs(direction.x) == Math.abs(direction.y)
                && Math.abs(kingAttacker.x - kingPosition.x) != Math.abs(kingAttacker.y - kingPosition.y))
            return false;

        Point pathController = new Point(kingPosition.x + direction.x, kingPosition.y + direction.y);

        System.out.println("checking if some piece can shield...");
        while (isInsideBoard(pathController) &&
                board[pathController.y][pathController.x] == null) {

            Point shieldPiece = getAttackerCell(pathController, pieceColor, board);

            System.out.println(shieldPiece);
            if (shieldPiece != null && shieldPiece.x == kingPosition.x && shieldPiece.y == kingPosition.y) {
                if (board[shieldPiece.y][shieldPiece.x].canMove(pathController, board)) return false;
            }

            pathController.x += direction.x;
            pathController.y += direction.y;
        }

        System.out.println("returning true");
        return true;
    }
}
