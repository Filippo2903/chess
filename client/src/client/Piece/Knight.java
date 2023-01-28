package client.Piece;

import client.Game;
import gameUtils.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Knight extends Piece implements PieceImage {
    public Knight(PlayerColor playerColor, Point startPosition) {
        super(playerColor, startPosition);
    }

    @Override
    public boolean canMove(Point cell) {
        if (isNotPlayerTurn() || isNotPlayerPiece()) {
            return false;
        }

        Piece[][] board = Game.getBoard();

        Point[] moves = {
                new Point(-2, -1),
                new Point(-1, -2),
                new Point(1, -2),
                new Point(2, -1),
                new Point(2, 1),
                new Point(1, 2),
                new Point(-1, 2),
                new Point(-2, 1)
        };

        boolean canMove = false;
        for (Point move : moves) {
            if (cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) {
                canMove = true;
                break;
            }
        }

        if (canMove) {
            if (board[cell.y][cell.x] != null) {
                board[cell.y][cell.x].kill();
            }
        }

        return canMove;
    }

    @Override
    public void setImage() {
        String path = "assets/" + pieceColor + "knight.png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
    }
}
