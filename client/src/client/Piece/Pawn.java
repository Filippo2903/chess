package client.Piece;

import client.Game;
import gameUtils.PlayerColor;
import modal.ErrorPopup;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class Pawn extends Piece implements PieceImage {
    public Pawn(PlayerColor color, Point startPosition) {
        super(color, startPosition);
    }

    private void promote(Class<?> promotedPiece) {
        promoted = true;

        Piece[][] board = Game.getBoard();

        JPanel chessboardPanel = (JPanel) this.getParent();

        this.kill();

        Piece newPiece = null;
        try {
            newPiece = (Piece) promotedPiece
                            .getDeclaredConstructor(PlayerColor.class, Point.class)
                            .newInstance(pieceColor, currentPosition);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            ErrorPopup.show(5);
        }

        board[currentPosition.y][currentPosition.x] = newPiece;

        assert newPiece != null;
        newPiece.setBounds(
                currentPosition.x * cellSize, currentPosition.y * cellSize,
                cellSize, cellSize
        );

        try {
            Method setImage = newPiece.getClass().getMethod("setImage");
            setImage.invoke(newPiece);
        } catch (Exception e) {
            ErrorPopup.show(6);
        }

        chessboardPanel.add(newPiece);
    }

    @Override
    public boolean canMove(Point cell) {
        if (isNotPlayerTurn() || isNotPlayerPiece()) {
            return false;
        }

        Piece[][] board = Game.getBoard();
        boolean canMove = false;
        int startPositionY = 6;
        int enPassantPositionY = 3;

        Point standardMove = new Point(0, -1);
        Point doubleMove = new Point(0, -2);
        Point[] takeMoves = {
                new Point(-1, -1),
                new Point(1, -1)
        };

        // Take move
        for (Point move : takeMoves) {
            if ((cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) &&
                    board[cell.y][cell.x] != null) {

                board[cell.y][cell.x].kill();
                canMove = true;
            }
        }

        // En passant
        for (Point move : takeMoves) {
            // TODO catch if the piece is a pawn
            if ((cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) &&
                board[cell.y + 1][cell.x] != null &&
                currentPosition.y == enPassantPositionY) {

                board[cell.y + 1][cell.x].kill();
                canMove = true;
            }
        }

        // If the cell in front is occupied
        if (board[cell.y][cell.x] != null) {
            return false;
        }

        // Standard move
        if (cell.x == currentPosition.x + standardMove.x && cell.y == currentPosition.y + standardMove.y) {
            canMove = true;
        }

        // Double move
        if (cell.x == currentPosition.x + doubleMove.x && cell.y == currentPosition.y + doubleMove.y &&
            currentPosition.y == startPositionY) {
            canMove = true;
        }

        // Promote
        if (canMove && cell.y == 0) {
            // TODO finestra popup con scelta
            Class<?> promoteType = Queen.class;

            this.promote(promoteType);
        }

        return canMove;
    }

    @Override
    public void setImage() {
        String path = "assets/" + pieceColor + "pawn.png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
    }
}
