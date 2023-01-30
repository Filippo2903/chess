package client.Piece;

import client.Game;
import client.DragAndDrop;
import gameUtils.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Bishop extends Piece implements CheckPlayerMove, PieceImage, DiagonalMove {
    public Bishop(PlayerColor playerColor) {
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

        Point ghostBishop = new Point(currentPosition.x, currentPosition.y);

        Point direction = diagonalMove(currentPosition, to);

        if (direction.x == 0 && direction.y == 0) {
            return false;
        }

        while (ghostBishop.x != to.x - direction.x || ghostBishop.y != to.y - direction.y) {
            ghostBishop.x += direction.x;
            ghostBishop.y += direction.y;

            if (board[ghostBishop.y][ghostBishop.x] != null) {
                return false;
            }
        }

        if (board[to.y][to.x] != null) {
            board[to.y][to.x].kill();
        }

        return true;
    }

    @Override
    public void setImage() {
        String path = "assets/" + pieceColor + "bishop.png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
    }
}
