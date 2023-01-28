package client.Piece;

import client.Game;
import gameUtils.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class King extends Piece implements PieceImage {
    public King(PlayerColor playerColor, Point startPosition) {
        super(playerColor, startPosition);
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

    private Point diagonalMove(Point cell) {
        Point direction = new Point();

        if (Math.abs(cell.x - currentPosition.x) == Math.abs(cell.y - currentPosition.y) ||
                Math.abs(cell.x + currentPosition.x) == Math.abs(cell.y - currentPosition.y) ||
                Math.abs(cell.x + currentPosition.x) == Math.abs(cell.y + currentPosition.y) ||
                Math.abs(cell.x - currentPosition.x) == Math.abs(cell.y + currentPosition.y) ) {

            direction.x = currentPosition.x - cell.x > 0 ? -1 : 1;
            direction.y = currentPosition.y - cell.y > 0 ? -1 : 1;
        }

        return direction;
    }

    @Override
    public boolean canMove(Point cell) {
        if (isNotPlayerTurn() || isNotPlayerPiece()) {
            return false;
        }

        Piece[][] board = Game.getBoard();

        Point direction = straightMove(cell);
        if (direction.x == 0 && direction.y == 0) {
            direction = diagonalMove(cell);
            if (direction.x == 0 && direction.y == 0) {
                return false;
            }
        }

        if (currentPosition.x + direction.x == cell.x && currentPosition.y + direction.y == cell.y) {
            if (board[cell.y][cell.x] != null) {
                board[cell.y][cell.x].kill();
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
