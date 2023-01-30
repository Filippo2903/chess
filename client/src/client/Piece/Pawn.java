package client.Piece;

import client.Game;
import client.DragAndDrop;
import gameUtils.PlayerColor;
import modal.ErrorPopup;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class Pawn extends Piece implements CheckPlayerMove, PieceImage {
    public Pawn(PlayerColor color) {
        super(color);

        DragAndDrop dragAndDrop = new DragAndDrop(this);

        this.addMouseListener(dragAndDrop);
        this.addMouseMotionListener(dragAndDrop);
    }

    private Class<?> choicePromotionType() {
        // TODO finestra popup con scelta
        return Queen.class;
    }

    private void promote(Class<?> typePromotion, Point promotingCell) {
        JPanel chessboardPanel = (JPanel) this.getParent();

        Piece promotedPiece = null;
        try {
            promotedPiece = (Piece) typePromotion
                                .getDeclaredConstructor(PlayerColor.class)
                                .newInstance(pieceColor);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            ErrorPopup.show(5);
        }

        assert promotedPiece != null;
        promotedPiece.setBounds(-1, -1, cellSize, cellSize);

        try {
            Method setImage = promotedPiece.getClass().getMethod("setImage");
            setImage.invoke(promotedPiece);
        } catch (Exception e) {
            ErrorPopup.show(6);
        }

        chessboardPanel.add(promotedPiece);
        promotedPiece.setPosition(promotingCell);

        this.kill();
        chessboardPanel.repaint();
    }

    private boolean isOccupied(Point cell) {
        Piece[][] board = Game.getBoard();

        return board[cell.y][cell.x] != null;
    }

    private boolean standardMove(Point to) {
        Point standardMove = new Point(0, -1);

        return to.x == currentPosition.x + standardMove.x && to.y == currentPosition.y + standardMove.y;
    }

    private boolean takeMoves(Point to) {
        final int EN_PASSANT_ROW = 3;

        Piece[][] board = Game.getBoard();

        Point[] takeMoves = {
                new Point(-1, -1),
                new Point(1, -1)
        };

        // Take move
        for (Point move : takeMoves) {
            if ((to.x == currentPosition.x + move.x && to.y == currentPosition.y + move.y) &&
                    board[to.y][to.x] != null) {

                board[to.y][to.x].kill();
                return true;
            }

            // En passant
            if ((to.x == currentPosition.x + move.x && to.y == currentPosition.y + move.y) &&
                    board[to.y + 1][to.x] != null &&
                    board[to.y + 1][to.x].getClass().getName().equals("client.Piece.Pawn") &&
                    currentPosition.y == EN_PASSANT_ROW) {

                board[to.y + 1][to.x].kill();
                return true;
            }
        }

        return false;
    }

    private boolean doubleMove(Point to) {
        final int START_ROW = 6;
        Point doubleMove = new Point(0, -2);

        return (to.x == currentPosition.x + doubleMove.x && to.y == currentPosition.y + doubleMove.y) &&
                currentPosition.y == START_ROW;
    }

    @Override
    public boolean canMove(Point to) {
        if (isNotPlayerTurn() || isNotPlayerPiece(this.getColor())) {
            return false;
        }

        if (!standardMove(to) &&
            !takeMoves(to) &&
            !doubleMove(to)) {
            return false;
        }

        if ((standardMove(to) || doubleMove(to)) && isOccupied(to)) {
            return false;
        }

        // If is in the last row
        if (to.y == 0) {
            Class<?> typePromotion = choicePromotionType();

            this.promote(typePromotion, to);
        }

        return true;
    }

    @Override
    public void setImage() {
        String path = "assets/" + pieceColor + "pawn.png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
    }
}
