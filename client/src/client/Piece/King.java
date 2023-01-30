package client.Piece;

import client.Game;
import client.DragAndDrop;
import gameUtils.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class King extends Piece implements CheckPlayerMove, PieceImage, DiagonalMove, StraightMove {
    public King(PlayerColor playerColor) {
        super(playerColor);

        DragAndDrop dragAndDrop = new DragAndDrop(this);

        this.addMouseListener(dragAndDrop);
        this.addMouseMotionListener(dragAndDrop);
    }

    @Override
    public boolean canMove(Point to) {
        if (isNotPlayerTurn() || isNotPlayerPiece(this.getColor())) {
            return false;
        }

        Piece[][] board = Game.getBoard();

        Point direction = straightMove(currentPosition, to);
        if (direction.x == 0 && direction.y == 0) {
            direction = diagonalMove(currentPosition, to);
            if (direction.x == 0 && direction.y == 0) {
                return false;
            }
        }

        if (currentPosition.x + direction.x == to.x && currentPosition.y + direction.y == to.y) {
            if (board[to.y][to.x] != null) {
                board[to.y][to.x].kill();
            }
            return true;
        }

        return false;
    }

    @Override
    public void setImage() {
        String path = "assets/" + pieceColor + "king.png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
    }
}
