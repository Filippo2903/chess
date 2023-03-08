package client.piece;

import client.Game;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DragAndDrop extends MouseAdapter {
    private final Piece piece;
    private final Point newPosition = new Point();
    private boolean undo;

    public DragAndDrop(Piece piece) {
        this.piece = piece;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            undo = false;
            piece.getParent().setComponentZOrder(piece, 0);
            return;
        }

        piece.setLocation(piece.currentPosition.x * Game.CELL_SIZE, piece.currentPosition.y * Game.CELL_SIZE);
        undo = true;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (undo) return;

        newPosition.x = piece.getLocation().x + e.getX() - (Game.CELL_SIZE >> 1);
        newPosition.y = piece.getLocation().y + e.getY() - (Game.CELL_SIZE >> 1);

        int x = (int) Math.floor(((double) newPosition.x + (Game.CELL_SIZE >> 1)) / Game.CELL_SIZE);
        int y = (int) Math.floor(((double) newPosition.y + (Game.CELL_SIZE >> 1)) / Game.CELL_SIZE);

        if (x < 0 || x > 7 || y < 0 || y > 7) {
            piece.setLocation(piece.currentPosition.x * Game.CELL_SIZE, piece.currentPosition.y * Game.CELL_SIZE);
            undo = true;
            return;
        }

        piece.setLocation(newPosition);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (undo) return;

        Point to = new Point(
                (newPosition.x + (Game.CELL_SIZE >> 1)) / Game.CELL_SIZE,
                (newPosition.y + (Game.CELL_SIZE >> 1)) / Game.CELL_SIZE
        );

        piece.move(to);
    }
}
