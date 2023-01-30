package client.Piece;

import client.DragAndDrop;
import gameUtils.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Queen extends Piece implements CheckPlayerMove, PieceImage {
    private final Rook queenStraightMoves;
    private final Bishop queenDiagonalMoves;

    public Queen(PlayerColor playerColor) {
        super(playerColor);

        DragAndDrop dragAndDrop = new DragAndDrop(this);

        this.addMouseListener(dragAndDrop);
        this.addMouseMotionListener(dragAndDrop);

        queenStraightMoves = new Rook(pieceColor);
        queenDiagonalMoves = new Bishop(pieceColor);
    }

    @Override
    public boolean canMove(Point to) {
        if (isNotPlayerTurn() || isNotPlayerPiece(this.getColor())) {
            return false;
        }

        queenStraightMoves.currentPosition = this.currentPosition;
        queenDiagonalMoves.currentPosition = this.currentPosition;

        return queenStraightMoves.canMove(to) || queenDiagonalMoves.canMove(to);
    }

    @Override
    public void setImage() {
        String path = "assets/" + pieceColor + "queen.png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
    }
}
