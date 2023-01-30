package client.Piece;

import client.Game;
import client.DragAndDrop;
import gameUtils.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Rook extends Piece implements CheckPlayerMove, PieceImage, StraightMove {
    public Rook(PlayerColor playerColor) {
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

        Point ghostRook = new Point(currentPosition.x, currentPosition.y);
        Point direction = straightMove(currentPosition, to);

        if (direction.x == 0 && direction.y == 0) {
            return false;
        }

        while (ghostRook.x != to.x - direction.x || ghostRook.y != to.y - direction.y) {
            ghostRook.x += direction.x;
            ghostRook.y += direction.y;

            if (board[ghostRook.y][ghostRook.x] != null) {
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
        String path = "assets/" + pieceColor + "rook.png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
    }
}
