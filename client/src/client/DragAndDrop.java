package client;

import client.Piece.Piece;
import gameUtils.Packet;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DragAndDrop extends MouseAdapter {
    private final static int CELL_SIZE = Game.CELL_SIZE;
    private boolean undo;
    private final Piece piece;
    private final Point newPosition = new Point();

    public DragAndDrop(Piece piece) {
        this.piece = piece;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            undo = false;
            piece.getParent().setComponentZOrder(piece, 0);
        } else {
            undo = true;

            piece.setPosition(piece.currentPosition);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (undo) return;

        int x = (int) Math.floor(((double) newPosition.x + (CELL_SIZE >> 1)) / CELL_SIZE);
        int y = (int) Math.floor(((double) newPosition.y + (CELL_SIZE >> 1)) / CELL_SIZE);

        if (x < 0 || x > 7 || y < 0 || y > 7) {
            undo = true;

            piece.setPosition(piece.currentPosition);
        }

        newPosition.x = piece.getLocation().x + e.getX() - (CELL_SIZE >> 1);
        newPosition.y = piece.getLocation().y + e.getY() - (CELL_SIZE >> 1);
        piece.setLocation(newPosition.x, newPosition.y);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (undo) return;

        Point to = new Point(
                (newPosition.x + (CELL_SIZE >> 1)) / CELL_SIZE,
                (newPosition.y + (CELL_SIZE >> 1)) / CELL_SIZE
        );

        if (piece.canMove(to)) {
            Point prevPosition = new Point(piece.currentPosition.x, piece.currentPosition.y);

            piece.setPosition(to);

            Client.sendMove(new Packet(prevPosition, piece.currentPosition, piece.getClass()));

            Thread recieveThread = new Thread(Client::receiveMove);
            recieveThread.start();

            Game.changePlayerTurn();

            return;
        }

        // Modify the position
        piece.setPosition(piece.currentPosition);
    }
}
