package client.Piece;

import client.Game;
import gameUtils.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Rook extends Piece implements PieceImage {
    public Rook(PlayerColor color, Point startPosition) {
        super(color, startPosition);
    }

    private Point straightMove(Point cell) {
        Point direction = new Point();

        if (cell.x == currentPosition.x) {
            direction.x = 0;
            direction.y = currentPosition.y > cell.y ? -1 : 1;
        }
        else if (cell.y == currentPosition.y) {
            direction.x = currentPosition.x > cell.x ? -1 : 1;
            direction.y = 0;
        }

        return direction;
    }

    @Override
    public boolean canMove(Point cell) {
        if (isNotPlayerTurn() || isNotPlayerPiece()) {
            return false;
        }

        Piece[][] board = Game.getBoard();

        Point ghostRook = new Point(currentPosition.x, currentPosition.y);
        Point direction = straightMove(cell);

        if (direction.x == 0 && direction.y == 0) {
            return false;
        }

        while (ghostRook.x != cell.x - direction.x || ghostRook.y != cell.y - direction.y) {
            ghostRook.x += direction.x;
            ghostRook.y += direction.y;

            if (board[ghostRook.y][ghostRook.x] != null) {
                return false;
            }
        }

        if (board[cell.y][cell.x] != null) {
            board[cell.y][cell.x].kill();
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
