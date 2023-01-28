package client.Piece;

import gameUtils.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Queen extends Piece implements PieceImage {
    public Queen(PlayerColor playerColor, Point startPosition) {
        super(playerColor, startPosition);
    }

    @Override
    public boolean canMove(Point cell) {
        if (isNotPlayerTurn() || isNotPlayerPiece()) {
            return false;
        }

        Rook queenStraightMoves = new Rook(pieceColor, currentPosition);
        Bishop queenDiagonalMoves = new Bishop(pieceColor, currentPosition);

        return queenStraightMoves.canMove(cell) || queenDiagonalMoves.canMove(cell);
    }

    @Override
    public void setImage() {
        String path = "assets/" + pieceColor + "queen.png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
    }
}
