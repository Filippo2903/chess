package client.piece;

import client.Game;
import client.movements.LMovement;
import gameUtils.PieceType;
import gameUtils.PlayerColor;

import java.awt.*;
import java.util.LinkedList;

public class Check {
    private static boolean isInsideBoard(Point cell) {
        return
                cell.y < Game.DIM_CHESSBOARD && cell.y >= 0 &&
                        cell.x < Game.DIM_CHESSBOARD && cell.x >= 0;
    }

    public static LinkedList<Point> getAttackersCell(Piece[][] board, Point cell, PlayerColor pieceColor) {
        LinkedList<Point> attackers = new LinkedList<>();

        final Point[] pawnTakeMoves = {
                new Point(-1, 1),
                new Point(1, 1)
        };

        for (Point pawnTakeMove : pawnTakeMoves) {
            Point checkedCell = new Point(cell.x + pawnTakeMove.x, cell.y + pawnTakeMove.y);

            if (isInsideBoard(checkedCell) &&
                    board[checkedCell.y][checkedCell.x] != null &&
                    board[checkedCell.y][checkedCell.x].getType() == PieceType.PAWN &&
                    board[checkedCell.y][checkedCell.x].getColor() != pieceColor) {

                attackers.add(checkedCell);
            }
        }

        for (Point move : LMovement.getMoves()) {
            Point checkedCell = new Point(cell.x + move.x, cell.y + move.y);

            if (isInsideBoard(checkedCell) &&
                    board[checkedCell.y][checkedCell.x] != null &&
                    board[checkedCell.y][checkedCell.x].getColor() != pieceColor &&
                    board[checkedCell.y][checkedCell.x].canMove(checkedCell, cell, board)) {

                attackers.add(checkedCell);
            }
        }

        final Point[] directionsLinearMovements = {
                new Point(-1, -1),
                new Point(1, -1),
                new Point(-1, 1),
                new Point(1, 1),
                new Point(0, -1),
                new Point(-1, 0),
                new Point(0, 1),
                new Point(1, 0)
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

            if (board[pathController.y][pathController.x].canMove(pathController, cell, board)) {
                attackers.add(pathController);
            }
        }

        return attackers;
    }

    private static LinkedList<Point> getReachers(Piece[][] board, Point cell, PlayerColor pieceColor) {
        LinkedList<Point> reachers = new LinkedList<>();

        final Point[] pawnMoves = {
                new Point(0, -1),
                new Point(0, -2),
                new Point(-1, -1),
                new Point(1, -1)
        };

        for (Point pawnMove : pawnMoves) {
            Point checkedCell = new Point(cell.x + pawnMove.x, cell.y + pawnMove.y);

            if (isInsideBoard(checkedCell) &&
                    board[checkedCell.y][checkedCell.x] != null &&
                    board[checkedCell.y][checkedCell.x].getType() == PieceType.PAWN &&
                    board[checkedCell.y][checkedCell.x].getColor() == pieceColor &&
                    board[checkedCell.y][checkedCell.x].canMove(checkedCell, cell, board)) {

                reachers.add(checkedCell);
            }
        }

        for (Point move : LMovement.getMoves()) {
            Point checkedCell = new Point(cell.x + move.x, cell.y + move.y);

            if (isInsideBoard(checkedCell) &&
                    board[checkedCell.y][checkedCell.x] != null &&
                    board[checkedCell.y][checkedCell.x].getType() == PieceType.KNIGHT &&
                    board[checkedCell.y][checkedCell.x].getColor() == pieceColor &&
                    board[checkedCell.y][checkedCell.x].canMove(checkedCell, cell, board)) {

                reachers.add(checkedCell);
            }
        }

        final Point[] directionsLinearMovements = {
                new Point(-1, -1),
                new Point(1, -1),
                new Point(-1, 1),
                new Point(1, 1),
                new Point(0, -1),
                new Point(-1, 0),
                new Point(0, 1),
                new Point(1, 0)
        };

        for (Point direction : directionsLinearMovements) {
            Point pathController = new Point(cell.x + direction.x, cell.y + direction.y);

            while (isInsideBoard(pathController) &&
                    board[pathController.y][pathController.x] == null) {

                pathController.x += direction.x;
                pathController.y += direction.y;
            }

            if (isInsideBoard(pathController) && board[pathController.y][pathController.x].getColor() == pieceColor) {
                if (board[pathController.y][pathController.x].canMove(pathController, cell, board)) {
                    reachers.add(pathController);
                }
            }
        }

        return reachers;
    }

    public static boolean isCellAttacked(Point cell, PlayerColor pieceColor, Piece[][] board) {
        return !getAttackersCell(board, cell, pieceColor).isEmpty();
    }

    private static boolean canKingEscape(Piece[][] board, Point kingPosition, PlayerColor pieceColor) {
        final Point[] kingMoves = {
                new Point(-1, -1),
                new Point(1, -1),
                new Point(-1, 1),
                new Point(1, 1),
                new Point(0, -1),
                new Point(-1, 0),
                new Point(0, 1),
                new Point(1, 0)
        };

        for (Point move : kingMoves) {
            Point checkedCell = new Point(kingPosition.x + move.x, kingPosition.y + move.y);
            if (!isInsideBoard(checkedCell) || isCellAttacked(checkedCell, pieceColor, board)) continue;

            if (board[checkedCell.y][checkedCell.x] != null &&
                    board[checkedCell.y][checkedCell.x].getColor() == pieceColor) {
                continue;
            }

            return true;
        }

        return false;
    }

    private static boolean isAttackerEatable(Piece[][] board, Point kingAttacker, Point kingPosition, PlayerColor pieceColor) {
        PlayerColor attackerColor = pieceColor == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
        LinkedList<Point> eatingPieces = getAttackersCell(board, kingAttacker, attackerColor);
        if (!eatingPieces.isEmpty()) {
            for (Point eatingPiece : eatingPieces) {
                if (eatingPiece.x == kingPosition.x && eatingPiece.y == kingPosition.y) {
                    if (!isCellAttacked(kingAttacker, attackerColor, board)) continue;
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isAttackerKnight(Point kingAttacker, Point kingPosition) {
        return (Math.abs(kingAttacker.x - kingPosition.x) == 2 && Math.abs(kingAttacker.y - kingPosition.y) == 1)
                || (Math.abs(kingAttacker.x - kingPosition.x) == 1 && Math.abs(kingAttacker.y - kingPosition.y) == 2);
    }

    private static boolean canProtectKing(Piece[][] board, Point kingAttacker, Point kingPosition, PlayerColor pieceColor) {
        Point direction = new Point(
            Integer.compare(kingAttacker.x, kingPosition.x),
            Integer.compare(kingAttacker.y, kingPosition.y)
        );

        Point pathController = new Point(kingPosition.x + direction.x, kingPosition.y + direction.y);

        while (isInsideBoard(pathController) &&
                board[pathController.y][pathController.x] == null) {

            LinkedList<Point> shieldPieces = getReachers(board, pathController, pieceColor);

            for (Point shieldPiece : shieldPieces) {
                if (shieldPiece.x == kingPosition.x && shieldPiece.y == kingPosition.y) {
                    continue;
                }
                return true;
            }

            pathController.x += direction.x;
            pathController.y += direction.y;
        }
        return false;
    }

    public static Point isCheckMate(Piece[][] board, Point kingPosition, PlayerColor pieceColor) {
        LinkedList<Point> kingAttackers = getAttackersCell(board, kingPosition, pieceColor);

        if (kingAttackers.isEmpty()) {
            return null;
        }

        if (canKingEscape(board, kingPosition, pieceColor)) return null;

        if (kingAttackers.size() >= 2) {
            return kingAttackers.getFirst();
        }

        Point kingAttacker = kingAttackers.getFirst();

        if (isAttackerEatable(board, kingAttacker, kingPosition, pieceColor)) return null;
        if (isAttackerKnight(kingAttacker, kingPosition)) return kingAttacker;
        if (canProtectKing(board, kingAttacker, kingPosition, pieceColor)) return null;

        return kingAttacker;
    }
}
