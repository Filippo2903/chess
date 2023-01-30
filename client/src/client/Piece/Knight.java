package client.Piece;

import client.Game;
import client.DragAndDrop;
import gameUtils.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Knight extends Piece implements CheckPlayerMove, PieceImage {
    public Knight(PlayerColor playerColor) {
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

        for (Point move : moves) {
            if (to.x == currentPosition.x + move.x && to.y == currentPosition.y + move.y) {
                Piece[][] board = Game.getBoard();
                if (board[to.y][to.x] != null) {
                    board[to.y][to.x].kill();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void setImage() {
        String path = "assets/" + pieceColor + "knight.png";

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
    }
}
