package client.Piece;

import client.Game;
import gameUtils.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Bishop extends Piece implements PieceImage {
    public Bishop(PlayerColor playerColor, Point startPosition) {
        super(playerColor, startPosition);
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

        Point ghostBishop = new Point(currentPosition.x, currentPosition.y);

        Point direction = diagonalMove(cell);

        if (direction.x == 0 && direction.y == 0) {
            return false;
        }

        while (ghostBishop.x != cell.x - direction.x || ghostBishop.y != cell.y - direction.y) {
            ghostBishop.x += direction.x;
            ghostBishop.y += direction.y;

            if (board[ghostBishop.y][ghostBishop.x] != null) {
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
        String path = "assets/" + pieceColor + "bishop.png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
    }
}
